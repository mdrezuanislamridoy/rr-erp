package com.erp.rr_erp.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.erp.rr_erp.auth.dto.LoginDto;
import com.erp.rr_erp.auth.dto.LoginResponse;
import com.erp.rr_erp.auth.dto.ProfileDto;
import com.erp.rr_erp.auth.dto.SignupDto;
import com.erp.rr_erp.auth.dto.UpdateProfileDto;
import com.erp.rr_erp.auth.entity.OtpEntity;
import com.erp.rr_erp.auth.entity.ProfileEntity;
import com.erp.rr_erp.auth.entity.UserEntity;
import com.erp.rr_erp.auth.repository.AuthRepository;
import com.erp.rr_erp.auth.repository.OtpRepository;
import com.erp.rr_erp.common.utils.JwtUtil;
import com.erp.rr_erp.common.utils.SendMail;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final OtpRepository otpRepository;
    private final SendMail sendMail;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(AuthRepository authRepository, OtpRepository otpRepository,
                       SendMail sendMail, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.authRepository = authRepository;
        this.otpRepository = otpRepository;
        this.sendMail = sendMail;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    private int autoGenerateCode() {
        return new Random().nextInt(900000) + 100000;
    }

    public void sendOtp(String email) throws Exception {
        if (authRepository.existsByEmail(email)) {
            throw new Exception("User already exists");
        }

        int code = autoGenerateCode();

        OtpEntity otpEntity = new OtpEntity();
        otpEntity.setEmail(email);
        otpEntity.setCode(String.valueOf(code));
        otpEntity.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpEntity.setVerified(false);
        otpRepository.save(otpEntity);

        sendMail.send(email, code);
    }

    public void verifyOtp(String email, int code) throws Exception {
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

        otpEntity.setVerified(true);
        otpRepository.save(otpEntity);
    }

    public void signup(SignupDto dto) throws Exception {
        if (authRepository.existsByEmail(dto.getEmail())) {
            throw new Exception("User already exists");
        }

        Optional<OtpEntity> otpOpt = otpRepository.findTopByEmailOrderByExpiresAtDesc(dto.getEmail());
        if (otpOpt.isEmpty() || !otpOpt.get().isVerified()) {
            throw new Exception("Email not verified. Please verify your email first.");
        }

        ProfileEntity profile = new ProfileEntity();

        UserEntity ue = new UserEntity();
        ue.setFullName(dto.getFullName());
        ue.setEmail(dto.getEmail());
        ue.setPassword(passwordEncoder.encode(dto.getPassword()));
        ue.setIsEmailVerified(true);
        ue.setProfile(profile);
        authRepository.save(ue);

        otpRepository.delete(otpOpt.get());
    }

    public LoginResponse login(LoginDto dto) throws Exception {
        UserEntity user = authRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new Exception("Invalid email or password"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new Exception("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());
        return new LoginResponse(token, user.getId(), user.getFullName(), user.getEmail(), user.getRole());
    }

    public ProfileDto getProfile(String email) throws Exception {
        UserEntity user = authRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));

        ProfileDto dto = new ProfileDto();
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());

        if (user.getProfile() != null) {
            dto.setBio(user.getProfile().getBio());
            dto.setProfilePictureUrl(user.getProfile().getProfilePictureUrl());
        }

        return dto;
    }

    public ProfileDto updateProfile(String email, UpdateProfileDto dto) throws Exception {
        UserEntity user = authRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));

        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }

        if (user.getProfile() == null) {
            user.setProfile(new ProfileEntity());
        }

        if (dto.getBio() != null) {
            user.getProfile().setBio(dto.getBio());
        }

        if (dto.getProfilePictureUrl() != null) {
            user.getProfile().setProfilePictureUrl(dto.getProfilePictureUrl());
        }

        authRepository.save(user);
        return getProfile(email);
    }
}
