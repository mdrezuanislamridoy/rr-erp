package com.erp.rr_erp.auth.dto;

import com.erp.rr_erp.auth.entity.enums.RoleName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDto{
    
    private String fullName;
    private String email;
    private String bio;
    private String profilePictureUrl;
    private RoleName role;
}
