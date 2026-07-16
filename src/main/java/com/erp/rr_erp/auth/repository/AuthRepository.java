
package com.erp.rr_erp.auth.repository;


import com.erp.rr_erp.auth.entity.UserEntity;


public class AuthRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
