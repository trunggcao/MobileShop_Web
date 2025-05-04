package com.example.Mobile.Ecommerce.dao;

import com.example.Mobile.Ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product save(Product product);
    Optional<Product> findById(Long id);

    List<Product> findByCategoryId(Long id);
    List<Product> findByNameContainingIgnoreCase(String keyword);


}
