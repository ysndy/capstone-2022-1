package com.example.oneshortsserver.controller;

import com.example.oneshortsserver.dto.UploadDTO;
import com.example.oneshortsserver.youtube.UploadVideo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UploadController {

    @PostMapping("/youtube")
    public void youtube(@RequestBody UploadDTO uploadDTO){

        String title = uploadDTO.getTitle();
        String detail = uploadDTO.getDetail();
        UploadVideo uploadVideo = new UploadVideo(title, detail);

    }

}
