package com.griddynamics.ngolovin.store.product.dao;

import com.griddynamics.ngolovin.store.product.domain.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
