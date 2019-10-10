package com.griddynamics.ngolovin.store.auth.web.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RegisterUserDto {

    @NotBlank
    private String name;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 6)
    private String password;
}
