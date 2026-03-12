package com.javatechie.repository;



import com.javatechie.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByStockSymbol(String symbol);
    List<OrderEntity> findByOrderType(String orderType);
    OrderEntity findByOrderId(String orderId);
}