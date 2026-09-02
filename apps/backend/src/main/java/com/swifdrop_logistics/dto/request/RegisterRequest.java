package com.swifdrop_logistics.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 1, max = 100)
    private String fullName;

    @NotBlank
    @Size(min = 8, max = 60)
    private String passWord;

    @NotBlank
    @Pattern(regexp = "^0[0-9]{9}$")
    private String phoneNumber;
}
