package com.example.oneshortsserver.video;


import com.example.oneshortsserver.youtube.UploadVideo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@RequiredArgsConstructor
@Service
public class VideoService {

    private final VideoRepository videoRepository;

    public SiteVideo upload(String title, String detail, String userId, InputStream inputStream){

        SiteVideo siteVideo = new SiteVideo();
        UploadVideo uploadVideo = new UploadVideo(title, detail, inputStream);
        siteVideo.setVideoId(uploadVideo.getVideoId());
        System.out.println("----------------"+uploadVideo.getVideoId());
        siteVideo.setUserId(userId);
        this.videoRepository.save(siteVideo);
        return siteVideo;

    }

}
