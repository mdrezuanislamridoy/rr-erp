package com.erp.rr_erp.auth.dto;

import com.erp.rr_erp.auth.entity.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private Long id;
    private String fullName;
    private String email;
    private RoleName role;
}
