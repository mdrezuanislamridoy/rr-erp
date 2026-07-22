package com.erp.rr_erp.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileDto {
    
    private String fullName;
    private String bio;
    private String profilePictureUrl;
}
