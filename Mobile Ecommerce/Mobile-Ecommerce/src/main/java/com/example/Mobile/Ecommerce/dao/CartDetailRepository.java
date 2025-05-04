package com.example.Mobile.Ecommerce.dao;


import com.example.Mobile.Ecommerce.entity.Cart;
import com.example.Mobile.Ecommerce.entity.CartDetail;
import com.example.Mobile.Ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail ,Long> {
    boolean existsByCartAndProduct(Cart cart, Product product);

    CartDetail findByCartAndProduct(Cart cart, Product product);

    List<CartDetail> findByProductId(Long id);
    void deleteAllByProductId(Long id);
}
