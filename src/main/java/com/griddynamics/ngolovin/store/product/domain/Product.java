package com.griddynamics.ngolovin.store.product.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", columnDefinition = "varchar (255)", nullable = false)
    private String title;

    @Column(name = "available", columnDefinition = "bigint", nullable = false)
    private Long available;

    @Column(name = "price", columnDefinition = "numeric (20, 2)", nullable = false)
    private BigDecimal price;
}
