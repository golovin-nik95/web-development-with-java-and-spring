package com.griddynamics.ngolovin.store.order.dao;

import com.griddynamics.ngolovin.store.order.domain.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByUser_Email(String email);
}
