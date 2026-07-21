package com.erp.rr_erp.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.rr_erp.auth.dto.SignupDto;
import com.erp.rr_erp.auth.entity.OtpEntity;
import com.erp.rr_erp.auth.entity.UserEntity;
import com.erp.rr_erp.auth.repository.AuthRepository;
import com.erp.rr_erp.auth.repository.OtpRepository;
import com.erp.rr_erp.common.utils.SendMail;

@Service
public class AuthService {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private SendMail sendMail;

    private int autoGenerateCode() {
        Random random = new Random();
        return random.nextInt(900000) + 100000;
    }

    public void sendOtp(String email) throws Exception {
        try {
            Optional<UserEntity> user = authRepository.findByEmail(email);

            if (user.isPresent()) {
                throw new Exception("User already exists");
            }

            int code = autoGenerateCode();

            // Save to database
            OtpEntity otpEntity = new OtpEntity();
            otpEntity.setEmail(email);
            otpEntity.setCode(String.valueOf(code));
            otpEntity.setExpiresAt(LocalDateTime.now().plusMinutes(5));
            otpRepository.save(otpEntity);

            sendMail.send(email, code);

        } catch (Exception e) {
            throw e;
        }
    }

    public void verifyOtp(String email, int code) throws Exception {
        try {
            Optional<OtpEntity> otpOpt = otpRepository.findTopByEmailOrderByExpiresAtDesc(email);
            if (otpOpt.isEmpty()) {
                throw new Exception("OTP not found");
            }

            OtpEntity otpEntity = otpOpt.get();
            if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new Exception("OTP has expired");
            }

            if (!otpEntity.getCode().equals(String.valueOf(code))) {
                throw new Exception("Invalid OTP");
            }

            UserEntity ue = authRepository.findByEmail(email).get();
            ue.setIsEmailVerified(true);
            authRepository.save(ue);

            otpRepository.delete(otpEntity);
        } catch (Exception e) {
            throw e;
        }
    }

    public void signup(SignupDto dto){
        try {
            
        } catch (Exception e) {
            
        }
    }
}
