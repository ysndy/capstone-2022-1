package com.example.oneshortsserver.video;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class SiteVideo {

    @Id
    private String videoId;

    @Column
    private String userId;

}
