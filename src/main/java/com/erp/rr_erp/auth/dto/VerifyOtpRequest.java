package com.erp.rr_erp.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpRequest {
    @NotBlank(message = "Email cannot be empty")
    @Email
    private String email;

    @NotBlank(message = "Code cannot be empty")
    private String code;
}
