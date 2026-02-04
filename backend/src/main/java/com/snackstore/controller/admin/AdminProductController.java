package com.snackstore.controller.admin;

import com.snackstore.common.ApiResponse;
import com.snackstore.common.BizException;
import com.snackstore.entity.Product;
import com.snackstore.service.ProductService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {
  private final ProductService productService;

  public AdminProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping
  public ApiResponse<List<ProductService.ProductDetail>> list(
      @RequestParam(value = "status", required = false) String status,
      @RequestParam(value = "categoryId", required = false) Long categoryId,
      @RequestParam(value = "keyword", required = false) String keyword) {
    return ApiResponse.ok(productService.listAdmin(status, categoryId, keyword));
  }

  @PostMapping
  public ApiResponse<ProductService.ProductDetail> create(@RequestBody SaveProductRequest req) {
    Product product = toProduct(null, req);
    product.setCreatedAt(LocalDateTime.now());
    productService.saveOrUpdateProduct(product, req.getImages(), req.getSpecs(), false);
    return ApiResponse.ok(productService.getDetail(product.getId(), true));
  }

  @PutMapping("/{id}")
  public ApiResponse<ProductService.ProductDetail> update(
      @PathVariable("id") Long id, @RequestBody SaveProductRequest req) {
    Product product = toProduct(id, req);
    productService.saveOrUpdateProduct(product, req.getImages(), req.getSpecs(), false);
    return ApiResponse.ok(productService.getDetail(id, true));
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable("id") Long id) {
    productService.deleteProduct(id);
    return ApiResponse.ok(null);
  }

  @PostMapping("/{id}/status")
  public ApiResponse<Void> updateStatus(@PathVariable("id") Long id, @RequestBody UpdateStatusRequest req) {
    if (req.getStatus() == null || req.getStatus().trim().isEmpty()) throw BizException.badRequest("状态不能为空");
    productService.updateProductStatus(id, req.getStatus().trim());
    return ApiResponse.ok(null);
  }

  private Product toProduct(Long id, SaveProductRequest req) {
    if (req.getCategoryId() == null) throw BizException.badRequest("分类ID不能为空");
    if (req.getName() == null || req.getName().trim().isEmpty()) throw BizException.badRequest("商品名称不能为空");
    if (req.getPrice() == null) throw BizException.badRequest("价格不能为空");
    if (req.getStock() == null) throw BizException.badRequest("库存不能为空");
    if (req.getStatus() == null || req.getStatus().trim().isEmpty()) throw BizException.badRequest("状态不能为空");

    Product product = new Product();
    product.setId(id);
    product.setCategoryId(req.getCategoryId());
    product.setName(req.getName().trim());
    product.setPrice(req.getPrice());
    product.setStock(req.getStock());
    product.setStatus(req.getStatus().trim());
    String mainImage = req.getMainImage();
    if (mainImage == null || mainImage.trim().isEmpty()) mainImage = req.getImage();
    product.setMainImage(mainImage);
    product.setBrand(req.getBrand());
    product.setShipFrom(req.getShipFrom());
    product.setOrigin(req.getOrigin());
    product.setSku(req.getSku());
    product.setDescription(req.getDescription());
    return product;
  }

  @Data
  public static class SaveProductRequest {
    private Long categoryId;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String status;
    private String mainImage;
    private String image;
    private String brand;
    private String shipFrom;
    private String origin;
    private String sku;
    private String description;
    private List<String> images;
    private Map<String, String> specs;
  }

  @Data
  public static class UpdateStatusRequest {
    private String status;
  }
}
