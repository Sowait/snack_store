package com.snackstore.controller;

import com.snackstore.common.ApiResponse;
import com.snackstore.service.ProductService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping
  public ApiResponse<List<ProductService.ProductDetail>> list(
      @RequestParam(value = "status", required = false) String status,
      @RequestParam(value = "categoryId", required = false) Long categoryId,
      @RequestParam(value = "keyword", required = false) String keyword) {
    return ApiResponse.ok(productService.list(status, categoryId, keyword));
  }

  @GetMapping("/{id}")
  public ApiResponse<ProductService.ProductDetail> detail(@PathVariable("id") Long id) {
    return ApiResponse.ok(productService.getDetail(id));
  }

  @GetMapping("/featured")
  public ApiResponse<List<ProductService.ProductDetail>> featured(
      @RequestParam(value = "limit", required = false) Integer limit) {
    return ApiResponse.ok(productService.listFeatured(limit));
  }

  @GetMapping("/new-arrivals")
  public ApiResponse<List<ProductService.ProductDetail>> newArrivals(
      @RequestParam(value = "limit", required = false) Integer limit) {
    return ApiResponse.ok(productService.listNewArrivals(limit));
  }
}
