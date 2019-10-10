package com.griddynamics.ngolovin.store.product.web.controller;

import com.griddynamics.ngolovin.store.product.domain.ProductEntity;
import com.griddynamics.ngolovin.store.product.service.ProductService;
import com.griddynamics.ngolovin.store.product.web.dto.ProductDto;
import com.griddynamics.ngolovin.store.product.web.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductDto> getAllProducts() {
        List<ProductEntity> products = productService.getAll();

        return ProductMapper.INSTANCE.convert(products);
    }
}

