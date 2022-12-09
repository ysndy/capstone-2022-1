package com.example.oneshortsserver.facebook;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FacebookService {

    private final FacebookRepository facebookRepository;

    public void saveAccessToken(String username, String accessToken){

        FacebookAccountInfo facebookAccountInfo = new FacebookAccountInfo();
        facebookAccountInfo.setAccessToken(accessToken);
        facebookAccountInfo.setUsername(username);
        this.facebookRepository.save(facebookAccountInfo);

    }

}
