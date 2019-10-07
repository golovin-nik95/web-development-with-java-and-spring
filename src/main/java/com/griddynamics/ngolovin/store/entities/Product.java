package com.griddynamics.ngolovin.store.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
