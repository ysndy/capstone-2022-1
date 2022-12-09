package com.example.oneshortsserver.youtube;

import com.example.oneshortsserver.youtube.account.YoutubeAccountInfo;
import com.example.oneshortsserver.youtube.account.YoutubeAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class YoutubeService {

    private final YoutubeAccountRepository youtubeAccountRepository;

    public void saveAccessToken(String username, String accessToken){

        YoutubeAccountInfo youtubeAccountInfo = new YoutubeAccountInfo();
        youtubeAccountInfo.setAccessToken(accessToken);
        youtubeAccountInfo.setUsername(username);
        this.youtubeAccountRepository.save(youtubeAccountInfo);

    }

}
