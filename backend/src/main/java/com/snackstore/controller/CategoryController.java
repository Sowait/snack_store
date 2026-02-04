package com.snackstore.controller;

import com.snackstore.common.ApiResponse;
import com.snackstore.entity.Category;
import com.snackstore.service.CategoryService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @GetMapping
  public ApiResponse<List<Category>> list() {
    return ApiResponse.ok(categoryService.listOnSale());
  }
}

