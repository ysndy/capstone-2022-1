package controller;


import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import youtube.UploadVideo;

@RestController
@Log4j2
public class UploadController {

    @GetMapping("/uploadTiktok")
    public void tiktok(){

    }

    @GetMapping("/uploadFacebook")
    public void facebook(){

    }

    @GetMapping("/uploadInstagram")
    public void instagram(){

    }

    @GetMapping("/uploadYoutube")
    public void youtube(){
        log.info("youtube");
        new UploadVideo();
    }

}
