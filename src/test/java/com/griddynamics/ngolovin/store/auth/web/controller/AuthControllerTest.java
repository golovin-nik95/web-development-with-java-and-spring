package com.griddynamics.ngolovin.store.auth.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.ngolovin.store.auth.dao.UserRepository;
import com.griddynamics.ngolovin.store.auth.domain.UserEntity;
import com.griddynamics.ngolovin.store.auth.web.dto.LoginUserDto;
import com.griddynamics.ngolovin.store.auth.web.dto.RegisterUserDto;
import com.griddynamics.ngolovin.store.auth.web.dto.TokenDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.List;

import static com.griddynamics.ngolovin.store.StoreTestConfig.USER_EMAIL;
import static com.griddynamics.ngolovin.store.StoreTestConfig.USER_NAME;
import static com.griddynamics.ngolovin.store.StoreTestConfig.USER_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void registerUserTest() throws Exception {
        RegisterUserDto registerUserDto = new RegisterUserDto(USER_NAME, USER_EMAIL, USER_PASSWORD);
        String requestBody = objectMapper.writeValueAsString(registerUserDto);

        String responseBody = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(responseBody).isEmpty();

        List<UserEntity> users = userRepository.findAll();
        assertThat(users).hasSize(1);

        UserEntity user = users.get(0);
        assertThat(user.getId()).isNotNull();
        assertThat(user.getEmail()).isEqualTo(USER_EMAIL);
        assertThat(passwordEncoder.matches(USER_PASSWORD, user.getEncryptedPassword())).isTrue();
        assertThat(user.getName()).isEqualTo(USER_NAME);
    }

    @Test
    @Sql(scripts = "classpath:/insert-user.sql")
    public void loginUserTest() throws Exception {
        LoginUserDto loginUserDto = new LoginUserDto(USER_EMAIL, USER_PASSWORD);
        String requestBody = objectMapper.writeValueAsString(loginUserDto);

        String responseBody = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn()
                .getResponse()
                .getContentAsString();
        TokenDto tokenDto = objectMapper.readValue(responseBody, TokenDto.class);
        assertThat(tokenDto.getToken()).isNotBlank();
        assertThat(tokenDto.getType()).isEqualTo("Bearer");
    }
}