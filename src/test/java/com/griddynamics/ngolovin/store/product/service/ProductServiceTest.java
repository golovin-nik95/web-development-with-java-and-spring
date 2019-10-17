package com.griddynamics.ngolovin.store.product.service;

import com.griddynamics.ngolovin.store.StoreTestUtils;
import com.griddynamics.ngolovin.store.product.dao.ProductRepository;
import com.griddynamics.ngolovin.store.product.domain.ProductEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private List<ProductEntity> products;

    @Before
    public void setUp() {
        products = StoreTestUtils.createProducts();
    }

    @Test
    public void getAllProductsTest() {
        when(productRepository.findAll())
                .thenReturn(products);

        List<ProductEntity> allProducts = productService.getAllProducts();

        verify(productRepository).findAll();
        verifyNoMoreInteractions(productRepository);
        assertThat(allProducts).isEqualTo(products);
    }

    @Test
    public void getProductByIdTest() {
        ProductEntity product = StoreTestUtils.createProducts().get(0);
        Long productId = product.getId();
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        Optional<ProductEntity> actual = productService.getProductById(productId);

        verify(productRepository).findById(productId);
        verifyNoMoreInteractions(productRepository);
        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(product);
    }

    @Test
    public void getProductById_ProductNotFoundTest() {
        Long productId = 1L;
        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        Optional<ProductEntity> actual = productService.getProductById(productId);

        verify(productRepository).findById(productId);
        verifyNoMoreInteractions(productRepository);
        assertThat(actual).isEmpty();
    }
}