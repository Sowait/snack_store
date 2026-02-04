package com.snackstore.service;

import com.snackstore.common.BizException;
import com.snackstore.entity.Cart;
import com.snackstore.entity.CartItem;
import com.snackstore.entity.Product;
import com.snackstore.repository.CartItemRepository;
import com.snackstore.repository.CartRepository;
import com.snackstore.repository.ProductRepository;
import com.snackstore.util.ImagePlaceholderUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final ProductRepository productRepository;

  public CartService(
      CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
    this.cartRepository = cartRepository;
    this.cartItemRepository = cartItemRepository;
    this.productRepository = productRepository;
  }

  @Transactional
  public Cart ensureCart(Long userId) {
    return cartRepository
        .findByUserId(userId)
        .orElseGet(
            () -> {
              Cart cart = new Cart();
              cart.setUserId(userId);
              cart.setCreatedAt(LocalDateTime.now());
              cart.setUpdatedAt(LocalDateTime.now());
              return cartRepository.save(cart);
            });
  }

  public List<CartItemView> getCartItems(Long userId) {
    Cart cart = ensureCart(userId);
    List<CartItem> items = cartItemRepository.findByCartIdOrderByCreatedAtDesc(cart.getId());
    List<CartItemView> views = new ArrayList<>();
    for (CartItem item : items) {
      Product product = productRepository.findById(item.getProductId()).orElse(null);
      CartItemView view = new CartItemView();
      view.setId(item.getId());
      view.setCartId(item.getCartId());
      view.setProductId(item.getProductId());
      view.setQuantity(item.getQuantity());
      view.setPrice(item.getPrice());
      view.setCreatedAt(item.getCreatedAt());
      if (product != null) {
        view.setProductName(product.getName());
        view.setProductImage(resolveProductImage(product));
        view.setProductStock(product.getStock());
        view.setProductStatus(product.getStatus());
      }
      views.add(view);
    }
    return views;
  }

  private String resolveProductImage(Product product) {
    String url = product.getMainImage();
    if (url == null || url.trim().isEmpty() || url.startsWith("/images/")) {
      return ImagePlaceholderUtil.buildSvgDataUrl(
          product.getName(),
          "零食商场 · 购物车",
          pickColorA(product.getId(), 1),
          pickColorB(product.getId(), 1));
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
  public CartItem addOrUpdate(Long userId, Long productId, Integer quantity) {
    if (productId == null) throw BizException.badRequest("商品ID不能为空");
    int qty = quantity == null ? 1 : quantity;
    if (qty <= 0) throw BizException.badRequest("数量必须大于0");

    Product product =
        productRepository.findById(productId).orElseThrow(() -> BizException.notFound("商品不存在"));
    if (!"上架".equals(product.getStatus())) throw BizException.badRequest("商品已下架");
    if (product.getStock() == null || product.getStock() <= 0) throw BizException.badRequest("商品缺货");
    if (qty > product.getStock()) qty = product.getStock();

    Cart cart = ensureCart(userId);
    List<CartItem> existed = cartItemRepository.findByCartId(cart.getId());
    Optional<CartItem> same =
        existed.stream().filter(i -> i.getProductId().equals(productId)).findFirst();

    CartItem item;
    if (same.isPresent()) {
      item = same.get();
      int next = item.getQuantity() + qty;
      item.setQuantity(Math.min(next, product.getStock()));
    } else {
      item = new CartItem();
      item.setCartId(cart.getId());
      item.setProductId(productId);
      item.setQuantity(qty);
      item.setCreatedAt(LocalDateTime.now());
    }
    item.setPrice(product.getPrice());
    cart.setUpdatedAt(LocalDateTime.now());
    cartRepository.save(cart);
    return cartItemRepository.save(item);
  }

  @Transactional
  public CartItem updateQuantity(Long userId, Long itemId, Integer quantity) {
    if (itemId == null) throw BizException.badRequest("明细ID不能为空");
    int qty = quantity == null ? 1 : quantity;
    if (qty <= 0) throw BizException.badRequest("数量必须大于0");

    Cart cart = ensureCart(userId);
    CartItem item =
        cartItemRepository.findById(itemId).orElseThrow(() -> BizException.notFound("购物车明细不存在"));
    if (!item.getCartId().equals(cart.getId())) throw BizException.forbidden("无权限");
    Product product =
        productRepository.findById(item.getProductId()).orElseThrow(() -> BizException.notFound("商品不存在"));
    if (product.getStock() != null && product.getStock() > 0) {
      qty = Math.min(qty, product.getStock());
    }
    item.setQuantity(qty);
    item.setPrice(product.getPrice());
    cart.setUpdatedAt(LocalDateTime.now());
    cartRepository.save(cart);
    return cartItemRepository.save(item);
  }

  @Transactional
  public void removeItem(Long userId, Long itemId) {
    Cart cart = ensureCart(userId);
    CartItem item =
        cartItemRepository.findById(itemId).orElseThrow(() -> BizException.notFound("购物车明细不存在"));
    if (!item.getCartId().equals(cart.getId())) throw BizException.forbidden("无权限");
    cartItemRepository.delete(item);
    cart.setUpdatedAt(LocalDateTime.now());
    cartRepository.save(cart);
  }

  @Transactional
  public void clear(Long userId) {
    Cart cart = ensureCart(userId);
    List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
    cartItemRepository.deleteAll(items);
    cart.setUpdatedAt(LocalDateTime.now());
    cartRepository.save(cart);
  }

  public static class CartItemView extends CartItem {
    private String productName;
    private String productImage;
    private Integer productStock;
    private String productStatus;

    public String getProductName() {
      return productName;
    }

    public void setProductName(String productName) {
      this.productName = productName;
    }

    public String getProductImage() {
      return productImage;
    }

    public void setProductImage(String productImage) {
      this.productImage = productImage;
    }

    public Integer getProductStock() {
      return productStock;
    }

    public void setProductStock(Integer productStock) {
      this.productStock = productStock;
    }

    public String getProductStatus() {
      return productStatus;
    }

    public void setProductStatus(String productStatus) {
      this.productStatus = productStatus;
    }
  }
}
