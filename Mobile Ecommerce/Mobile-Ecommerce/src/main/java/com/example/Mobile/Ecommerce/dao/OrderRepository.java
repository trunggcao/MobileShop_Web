package com.example.Mobile.Ecommerce.dao;

import com.example.Mobile.Ecommerce.entity.Order;
import com.example.Mobile.Ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByUser(User user);
}
