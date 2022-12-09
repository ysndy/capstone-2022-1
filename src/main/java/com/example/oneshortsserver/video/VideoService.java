package com.example.oneshortsserver.video;


import com.example.oneshortsserver.youtube.UploadVideo;
import com.example.oneshortsserver.youtube.account.YoutubeAccountInfo;
import com.example.oneshortsserver.youtube.account.YoutubeAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@RequiredArgsConstructor
@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final YoutubeAccountRepository youtubeAccountRepository;

    public SiteVideo upload(String title, String detail, String userId, InputStream inputStream){

        SiteVideo siteVideo = new SiteVideo();

        YoutubeAccountInfo youtubeAccountInfo = youtubeAccountRepository.findYoutubeInfoByUsername(userId).get();

        UploadVideo uploadVideo = new UploadVideo(title, userId, detail, inputStream,  youtubeAccountInfo.getAccessToken());
        siteVideo.setVideoId(uploadVideo.getVideoId());
        System.out.println("----------------"+uploadVideo.getVideoId());
        siteVideo.setUserId(userId);
        this.videoRepository.save(siteVideo);
        return siteVideo;

    }

}
