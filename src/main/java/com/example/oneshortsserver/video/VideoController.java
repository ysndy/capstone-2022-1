package com.example.oneshortsserver.video;

import com.example.oneshortsserver.facebook.FacebookRepository;
import com.example.oneshortsserver.youtube.Auth;
import com.example.oneshortsserver.youtube.MyUploads;
import com.example.oneshortsserver.youtube.YoutubeService;
import com.example.oneshortsserver.youtube.account.YoutubeAccountInfo;
import com.example.oneshortsserver.youtube.account.YoutubeAccountRepository;
import com.example.oneshortsserver.youtube.video.YoutubeVideoInfo;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@Controller
@RequestMapping("/api/video")
public class VideoController {

    private final FacebookRepository facebookRepository;
    private final YoutubeAccountRepository youtubeAccountRepository;

    @GetMapping("/list")
    public String list(Model model) throws IOException {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String userName = userDetails.getUsername();
        YoutubeAccountInfo youtubeAccountInfo = youtubeAccountRepository.findYoutubeInfoByUsername(userName).get();
        String accessToken = youtubeAccountInfo.getAccessToken();

        List<YoutubeVideoInfo> youtubeVideoInfoList = MyUploads.getYoutubeVideoInfoList(accessToken);

        model.addAttribute("youtubeVideoList", youtubeVideoInfoList);
        return "video_list";
    }

    @GetMapping("/upload")
    public String uploadForm(Model model){

        boolean isFacebookRegisted;
        boolean isYoutubeRegisted;

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails)principal;

        isFacebookRegisted = facebookRepository.findFacebookInfoByUsername(userDetails.getUsername()).isPresent();
        isYoutubeRegisted = youtubeAccountRepository.findYoutubeInfoByUsername(userDetails.getUsername()).isPresent();
        model.addAttribute("facebook", isFacebookRegisted);
        model.addAttribute("youtube", isYoutubeRegisted);

        return "form";
    }

}
