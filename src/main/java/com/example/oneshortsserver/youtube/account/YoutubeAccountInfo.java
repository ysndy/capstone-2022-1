package com.example.oneshortsserver.youtube.account;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class YoutubeAccountInfo {

    @Id
    private String username;

    @Column
    private String accessToken;

}
