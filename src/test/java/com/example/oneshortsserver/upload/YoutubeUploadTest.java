package com.example.oneshortsserver.upload;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.oneshortsserver.youtube.UploadVideo;

@SpringBootTest
public class YoutubeUploadTest {

    @Test
    public void upload(){
        new UploadVideo("test", "test");
    }

}
