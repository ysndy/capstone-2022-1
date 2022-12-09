package com.example.oneshortsserver.youtube.account;

import com.example.oneshortsserver.youtube.account.YoutubeAccountInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface YoutubeAccountRepository extends JpaRepository<YoutubeAccountInfo, Long> {
    Optional<YoutubeAccountInfo> findYoutubeInfoByUsername(String username);
}
