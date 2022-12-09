package com.example.oneshortsserver.video;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class SiteVideo {

    @Id
    private String videoId;

    @Column
    private String userId;

    @CreatedDate
    @Column
    private LocalDateTime date;

}
