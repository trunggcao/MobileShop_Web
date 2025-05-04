package com.example.Mobile.Ecommerce.dao;

import com.example.Mobile.Ecommerce.entity.Cart;
import com.example.Mobile.Ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUser(User user);;
    Cart findByUserId(Long Id);
    Optional<Cart> findById(Long id);
}
