package com.griddynamics.ngolovin.store.product.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.ngolovin.store.product.web.dto.ProductDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;

import static com.griddynamics.ngolovin.store.StoreTestConfig.USER_EMAIL;
import static com.griddynamics.ngolovin.store.StoreTestConfig.USER_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@WithMockUser(username = USER_EMAIL, password = USER_PASSWORD)
@RunWith(SpringRunner.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAllProductsTest() throws Exception {
        String responseBody = mockMvc.perform(get("/api/product"))
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<ProductDto> productDtos = objectMapper.readValue(responseBody, new TypeReference<List<ProductDto>>() {});
        productDtos.sort(Comparator.comparingLong(ProductDto::getId));
        assertThat(productDtos).hasSize(5);

        ProductDto productDto = productDtos.get(0);
        assertThat(productDto.getId()).isEqualTo(1L);
        assertThat(productDto.getTitle()).isEqualTo("Pen");
        assertThat(productDto.getAvailable()).isEqualTo(15);
        assertThat(productDto.getPrice()).isEqualByComparingTo("7.60");
    }
}