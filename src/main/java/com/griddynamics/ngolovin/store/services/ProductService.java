package com.griddynamics.ngolovin.store.services;

import com.griddynamics.ngolovin.store.entities.Product;
import com.griddynamics.ngolovin.store.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAll() {
        return productRepository.findAll();
    }
}
