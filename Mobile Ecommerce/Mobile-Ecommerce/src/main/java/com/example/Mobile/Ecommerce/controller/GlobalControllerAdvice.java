package com.example.Mobile.Ecommerce.controller;

import com.example.Mobile.Ecommerce.entity.Category;
import com.example.Mobile.Ecommerce.service.CategoryService;
import com.example.Mobile.Ecommerce.service.ProductService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    private ProductService productService;
    private CategoryService categoryService;

    public GlobalControllerAdvice(ProductService productService,
                                  CategoryService categoryService) {
        this.productService = productService;
        this.categoryService =categoryService;
    }
    @ModelAttribute("categoryList")
    public List<Category> getCatagoryList() {
        return categoryService.getAllCategory();
    }
}

