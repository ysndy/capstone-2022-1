package com.example.oneshortsserver.youtube.video;

import com.google.api.client.util.DateTime;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class YoutubeVideoInfo {

    @Id
    private String id;
    private String title;
    private String detail;
    private DateTime date;
    private String thumbnailUrl;
    private String likeCount;
    private String viewCount;
    private String commentCount;

}
