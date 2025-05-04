package com.example.Mobile.Ecommerce.service;

import com.example.Mobile.Ecommerce.dao.CategoryRepository;

import com.example.Mobile.Ecommerce.dao.ProductRepository;
import com.example.Mobile.Ecommerce.entity.Category;
import com.example.Mobile.Ecommerce.entity.OrderDetail;
import com.example.Mobile.Ecommerce.entity.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository =productRepository;
    }

    public List<Category> getAllCategory(){
        return this.categoryRepository.findAll();
    }

    public Category findCategoryById(Long id){
       return this.categoryRepository.findById(id).orElse(null);
    }
    public Category handleSaveCategory(Category category){
        return this.categoryRepository.save(category);
    }

    public void deleteCategory(Long id){

        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            throw new RuntimeException("Category not found");
        }
        Category category = optionalCategory.get();

        // thay the = category: khác
        Optional<Category> defaultCategoryOpt = categoryRepository.findById(8L);
        if (defaultCategoryOpt.isEmpty()) {
            throw new RuntimeException("Default category (ID = 8) not found");
        }
        Category defaultCategory = defaultCategoryOpt.get();

        List<Product> products = productRepository.findByCategoryId(id);
        for (Product product : products) {
            product.setCategory(defaultCategory);
        }
        productRepository.saveAll(products);
        categoryRepository.delete(category);

    }


}
