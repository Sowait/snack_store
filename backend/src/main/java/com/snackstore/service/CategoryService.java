package com.snackstore.service;

import com.snackstore.entity.Category;
import com.snackstore.repository.CategoryRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {
  private final CategoryRepository categoryRepository;

  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public List<Category> listOnSale() {
    return categoryRepository.findByStatusOrderBySortOrderAsc("上架");
  }

  public List<Category> listAll() {
    return categoryRepository.findAll();
  }

  @Transactional
  public Category save(Category category) {
    if (category.getCreatedAt() == null) {
      category.setCreatedAt(LocalDateTime.now());
    }
    return categoryRepository.save(category);
  }
}

