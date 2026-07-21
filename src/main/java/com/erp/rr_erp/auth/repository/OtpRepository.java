package com.erp.rr_erp.auth.repository;

import com.erp.rr_erp.auth.entity.OtpEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<OtpEntity, Long> {
    Optional<OtpEntity> findTopByEmailOrderByExpiresAtDesc(String email);
}
