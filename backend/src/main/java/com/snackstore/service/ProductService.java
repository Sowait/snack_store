package com.snackstore.service;

import com.snackstore.common.BizException;
import com.snackstore.entity.Category;
import com.snackstore.entity.Product;
import com.snackstore.entity.ProductImage;
import com.snackstore.entity.ProductSpec;
import com.snackstore.repository.CategoryRepository;
import com.snackstore.repository.OrderItemRepository;
import com.snackstore.repository.ProductImageRepository;
import com.snackstore.repository.ProductRepository;
import com.snackstore.repository.ProductSpecRepository;
import com.snackstore.util.ImagePlaceholderUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
  private final ProductRepository productRepository;
  private final ProductImageRepository productImageRepository;
  private final ProductSpecRepository productSpecRepository;
  private final CategoryRepository categoryRepository;
  private final OrderItemRepository orderItemRepository;

  public ProductService(
      ProductRepository productRepository,
      ProductImageRepository productImageRepository,
      ProductSpecRepository productSpecRepository,
      CategoryRepository categoryRepository,
      OrderItemRepository orderItemRepository) {
    this.productRepository = productRepository;
    this.productImageRepository = productImageRepository;
    this.productSpecRepository = productSpecRepository;
    this.categoryRepository = categoryRepository;
    this.orderItemRepository = orderItemRepository;
  }

  public List<ProductDetail> list(String status, Long categoryId, String keyword) {
    return listInternal(status, categoryId, keyword, false);
  }

  public List<ProductDetail> listAdmin(String status, Long categoryId, String keyword) {
    String st = status == null ? null : status.trim();
    String kw = keyword == null ? null : keyword.trim();
    List<Product> products = productRepository.findAll();
    products.sort(
        (a, b) -> {
          LocalDateTime at = a.getUpdatedAt() == null ? a.getCreatedAt() : a.getUpdatedAt();
          LocalDateTime bt = b.getUpdatedAt() == null ? b.getCreatedAt() : b.getUpdatedAt();
          if (at == null && bt == null) return 0;
          if (at == null) return 1;
          if (bt == null) return -1;
          return bt.compareTo(at);
        });
    List<ProductDetail> list = new ArrayList<>();
    for (Product p : products) {
      if (st != null && !st.isEmpty() && !st.equals(p.getStatus())) continue;
      if (categoryId != null && !categoryId.equals(p.getCategoryId())) continue;
      if (kw != null && !kw.isEmpty() && (p.getName() == null || !p.getName().contains(kw))) continue;
      list.add(getDetail(p.getId(), true));
    }
    return list;
  }

  private List<ProductDetail> listInternal(
      String status, Long categoryId, String keyword, boolean includeAll) {
    String st = status == null || status.trim().isEmpty() ? "上架" : status.trim();
    String kw = keyword == null ? null : keyword.trim();
    List<Product> products;
    if (kw != null && !kw.isEmpty() && categoryId != null) {
      products = productRepository.findByCategoryIdAndStatusAndNameContainingOrderByCreatedAtDesc(categoryId, st, kw);
    } else if (kw != null && !kw.isEmpty()) {
      products = productRepository.findByStatusAndNameContainingOrderByCreatedAtDesc(st, kw);
    } else if (categoryId != null) {
      products = productRepository.findByCategoryIdAndStatusOrderByCreatedAtDesc(categoryId, st);
    } else {
      products = productRepository.findByStatusOrderByCreatedAtDesc(st);
    }
    List<ProductDetail> list = new ArrayList<>();
    for (Product p : products) {
      list.add(getDetail(p.getId(), includeAll));
    }
    return list;
  }

  public ProductDetail getDetail(Long productId, boolean includeAll) {
    Product product =
        productRepository.findById(productId).orElseThrow(() -> BizException.notFound("商品不存在"));
    return buildDetail(product, includeAll);
  }

  public ProductDetail getDetail(Long productId) {
    return getDetail(productId, true);
  }

  public List<ProductDetail> listFeatured(Integer limit) {
    int size = normalizeLimit(limit, 3, 20);
    List<Long> ids = orderItemRepository.findTopFeaturedProductIds(PageRequest.of(0, size));
    List<ProductDetail> list = new ArrayList<>();
    if (ids != null) {
      for (Long id : ids) {
        if (id == null) continue;
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) continue;
        if (!"上架".equals(product.getStatus())) continue;
        list.add(getDetail(product.getId(), false));
      }
    }
    fillWithNewestProducts(list, size);
    return list;
  }

  public List<ProductDetail> listNewArrivals(Integer limit) {
    int size = normalizeLimit(limit, 3, 20);
    List<ProductDetail> list = new ArrayList<>();
    fillWithNewestProducts(list, size);
    return list;
  }

  private int normalizeLimit(Integer limit, int defaultValue, int maxValue) {
    int size = limit == null ? defaultValue : limit;
    if (size <= 0) size = defaultValue;
    if (size > maxValue) size = maxValue;
    return size;
  }

  private void fillWithNewestProducts(List<ProductDetail> list, int size) {
    if (list.size() >= size) return;
    Set<Long> existed = new HashSet<>();
    for (ProductDetail d : list) {
      if (d == null) continue;
      if (d.getId() != null) existed.add(d.getId());
    }
    List<Product> products = productRepository.findByStatusOrderByCreatedAtDesc("上架");
    for (Product p : products) {
      if (list.size() >= size) break;
      if (p == null || p.getId() == null) continue;
      if (existed.contains(p.getId())) continue;
      list.add(getDetail(p.getId(), false));
      existed.add(p.getId());
    }
  }

  private ProductDetail buildDetail(Product product, boolean includeAll) {
    ProductDetail detail = new ProductDetail();
    detail.setId(product.getId());
    detail.setCategoryId(product.getCategoryId());
    detail.setName(product.getName());
    detail.setPrice(product.getPrice());
    detail.setStock(product.getStock());
    detail.setStatus(product.getStatus());
    detail.setBrand(product.getBrand());
    detail.setShipFrom(product.getShipFrom());
    detail.setOrigin(product.getOrigin());
    detail.setSku(product.getSku());
    detail.setDescription(product.getDescription());
    detail.setCreatedAt(product.getCreatedAt());
    detail.setUpdatedAt(product.getUpdatedAt());

    Map<Long, Category> categoryById = new HashMap<>();
    Optional<Category> category = categoryRepository.findById(product.getCategoryId());
    category.ifPresent(c -> categoryById.put(c.getId(), c));
    detail.setCategoryName(categoryById.get(product.getCategoryId()) == null ? null : categoryById.get(product.getCategoryId()).getName());

    detail.setMainImage(resolveMainImage(product));

    List<ProductImage> images = productImageRepository.findByProductIdOrderBySortOrderAsc(product.getId());
    List<String> urls = new ArrayList<>();
    for (ProductImage img : images) {
      urls.add(resolveImageUrl(product, img.getUrl(), img.getSortOrder() == null ? 1 : img.getSortOrder()));
    }
    if (urls.isEmpty()) {
      urls.add(detail.getMainImage());
      urls.add(
          ImagePlaceholderUtil.buildSvgDataUrl(
              product.getName(), "爆款推荐 · 口感升级", pickColorA(product.getId(), 2), pickColorB(product.getId(), 2)));
      urls.add(
          ImagePlaceholderUtil.buildSvgDataUrl(
              product.getName(), "随手一包 · 解馋必备", pickColorA(product.getId(), 3), pickColorB(product.getId(), 3)));
    }
    detail.setImages(urls);

    Map<String, String> specs = new HashMap<>();
    for (ProductSpec spec : productSpecRepository.findByProductId(product.getId())) {
      specs.put(spec.getSpecKey(), spec.getSpecValue());
    }
    if (specs.isEmpty()) {
      specs.put("净含量", "200g");
      specs.put("口味", "原味");
      specs.put("保质期", "12个月");
      specs.put("储存方式", "阴凉干燥处密封保存");
    }
    detail.setSpecs(specs);

    if (!includeAll) {
      detail.setDescription(null);
      detail.setSpecs(null);
      detail.setImages(null);
    }
    return detail;
  }

  private String resolveMainImage(Product product) {
    String url = product.getMainImage();
    if (url == null || url.trim().isEmpty() || url.startsWith("/images/") || url.startsWith("blob:")) {
      return ImagePlaceholderUtil.buildSvgDataUrl(
          product.getName(),
          "零食商场 · 商品图",
          pickColorA(product.getId(), 1),
          pickColorB(product.getId(), 1));
    }
    return url;
  }

  private String resolveImageUrl(Product product, String url, int index) {
    if (url == null || url.trim().isEmpty() || url.startsWith("/images/") || url.startsWith("blob:")) {
      return ImagePlaceholderUtil.buildSvgDataUrl(
          product.getName(),
          "零食商场 · 图片 " + index,
          pickColorA(product.getId(), index),
          pickColorB(product.getId(), index));
    }
    return url;
  }

  private String pickColorA(Long seed, int offset) {
    String[] colors = {"#7C3AED", "#A78BFA", "#4C1D95", "#22C55E", "#FF1493", "#FFAA00", "#00FFFF"};
    int idx = (int) ((seed == null ? 0 : seed) + offset) % colors.length;
    return colors[idx];
  }

  private String pickColorB(Long seed, int offset) {
    String[] colors = {"#A78BFA", "#7C3AED", "#22C55E", "#FFAA00", "#00FFFF", "#FF1493", "#4C1D95"};
    int idx = (int) ((seed == null ? 0 : seed) + offset) % colors.length;
    return colors[idx];
  }

  @Transactional
  public Product saveOrUpdateProduct(
      Product product, List<String> images, Map<String, String> specs, boolean overwriteCreatedAt) {
    LocalDateTime now = LocalDateTime.now();
    if (product.getId() == null && overwriteCreatedAt) {
      product.setCreatedAt(now);
    }
    product.setUpdatedAt(now);
    Product saved = productRepository.save(product);

    if (images != null) {
      List<ProductImage> existed = productImageRepository.findByProductIdOrderBySortOrderAsc(saved.getId());
      productImageRepository.deleteAll(existed);
      int i = 1;
      for (String url : images) {
        if (url == null || url.trim().isEmpty()) continue;
        ProductImage img = new ProductImage();
        img.setProductId(saved.getId());
        img.setUrl(url.trim());
        img.setSortOrder(i++);
        productImageRepository.save(img);
      }
    }

    if (specs != null) {
      List<ProductSpec> existed = productSpecRepository.findByProductId(saved.getId());
      productSpecRepository.deleteAll(existed);
      for (Map.Entry<String, String> e : specs.entrySet()) {
        if (e.getKey() == null || e.getKey().trim().isEmpty()) continue;
        if (e.getValue() == null || e.getValue().trim().isEmpty()) continue;
        ProductSpec spec = new ProductSpec();
        spec.setProductId(saved.getId());
        spec.setSpecKey(e.getKey().trim());
        spec.setSpecValue(e.getValue().trim());
        productSpecRepository.save(spec);
      }
    }

    return saved;
  }

  @Transactional
  public void deleteProduct(Long productId) {
    Product product =
        productRepository.findById(productId).orElseThrow(() -> BizException.notFound("商品不存在"));
    List<ProductImage> images = productImageRepository.findByProductIdOrderBySortOrderAsc(product.getId());
    if (!images.isEmpty()) {
      productImageRepository.deleteAll(images);
    }
    List<ProductSpec> specs = productSpecRepository.findByProductId(product.getId());
    if (!specs.isEmpty()) {
      productSpecRepository.deleteAll(specs);
    }
    productRepository.delete(product);
  }

  @Transactional
  public Product updateProductStatus(Long productId, String status) {
    Product product =
        productRepository.findById(productId).orElseThrow(() -> BizException.notFound("商品不存在"));
    product.setStatus(status);
    product.setUpdatedAt(LocalDateTime.now());
    return productRepository.save(product);
  }

  public static class ProductDetail extends Product {
    private String categoryName;
    private List<String> images;
    private Map<String, String> specs;

    public String getImage() {
      return getMainImage();
    }

    public void setImage(String image) {
      setMainImage(image);
    }

    public String getCategoryName() {
      return categoryName;
    }

    public void setCategoryName(String categoryName) {
      this.categoryName = categoryName;
    }

    public List<String> getImages() {
      return images;
    }

    public void setImages(List<String> images) {
      this.images = images;
    }

    public Map<String, String> getSpecs() {
      return specs;
    }

    public void setSpecs(Map<String, String> specs) {
      this.specs = specs;
    }
  }
}
