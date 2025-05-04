package com.example.Mobile.Ecommerce.dao;

import com.example.Mobile.Ecommerce.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository  extends JpaRepository<OrderDetail,Long> {

    List<OrderDetail> findByProductId(Long id);
}
