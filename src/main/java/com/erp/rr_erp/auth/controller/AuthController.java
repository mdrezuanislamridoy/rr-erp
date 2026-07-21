package com.erp.rr_erp.auth.controller;

import com.erp.rr_erp.auth.dto.SendOtpRequest;
import com.erp.rr_erp.auth.dto.VerifyOtpRequest;
import com.erp.rr_erp.auth.service.AuthService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@Valid @RequestBody SendOtpRequest request) throws Exception{
        try {
            authService.sendOtp(request.getEmail());
            return ResponseEntity.ok(Map.of("message", "OTP sent successfully to " + request.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest body ){
        try {
            authService.verifyOtp(body.getEmail(), Integer.parseInt(body.getCode()));
            return ResponseEntity.ok(Map.of("message", "OTP verified successfully"));
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "OTP code must be a valid number"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
}
