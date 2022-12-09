package com.example.oneshortsserver.video;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<SiteVideo, String> {


    List<SiteVideo> findSiteVideosByUserId(String userId);

}
