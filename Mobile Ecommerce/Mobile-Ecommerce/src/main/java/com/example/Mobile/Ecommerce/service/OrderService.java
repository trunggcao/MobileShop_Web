package com.example.Mobile.Ecommerce.service;

import com.example.Mobile.Ecommerce.dao.OrderRepository;
import com.example.Mobile.Ecommerce.entity.Order;
import com.example.Mobile.Ecommerce.entity.Product;
import com.example.Mobile.Ecommerce.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrder(){
       return this.orderRepository.findAll();
    }
    public Order findById(Long id){
        return this.orderRepository.findById(id).orElse(null);
    }

    public Order handleSaveOrder(Order order){
        return this.orderRepository.save(order);
    }

    public List<Order> fetchOrderByUser(User user) {
        return this.orderRepository.findByUser(user);
    }
}
