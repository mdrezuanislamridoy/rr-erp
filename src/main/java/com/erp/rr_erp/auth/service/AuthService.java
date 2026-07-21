package com.erp.rr_erp.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

            // Send via email
            sendMail.send(email, code);

        } catch (Exception e) {
            throw e;
        }
    }
}
