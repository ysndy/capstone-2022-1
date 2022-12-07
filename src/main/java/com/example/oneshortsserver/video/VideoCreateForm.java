package com.example.oneshortsserver.video;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class VideoCreateForm {

    private String title;
    private String detail;
    private String videoName;

}
