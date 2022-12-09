package com.example.oneshortsserver.facebook;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacebookRepository extends JpaRepository<FacebookAccountInfo, Long> {
    Optional<FacebookAccountInfo> findFacebookInfoByUsername(String username);
}
