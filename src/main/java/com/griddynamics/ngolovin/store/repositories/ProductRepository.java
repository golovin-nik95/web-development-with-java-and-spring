package com.griddynamics.ngolovin.store.repositories;

import com.griddynamics.ngolovin.store.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
