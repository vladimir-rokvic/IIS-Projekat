package com.iis.projekat.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class RegisterRequest {

    @Email
    @NotBlank
    public String email;

    @Size(min = 6)
    public String password;

    @Size(min = 6)
    public String confirmPassword;

    @NotBlank
    public String name;

    @NotBlank
    public String surname;

    public LocalDate dateOfBirth;

    public String phone;
}
