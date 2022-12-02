package com.example.oneshortsserver.video;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;


@RequiredArgsConstructor
@Controller
@RequestMapping("/api/video")
public class VideoController {

    private final VideoService videoService;
    private final VideoRepository videoRepository;

    @GetMapping("/upload/youtube")
    public String youtube(VideoCreateForm videoCreateForm){
        return "form";
    }

    @PostMapping("/upload/youtube")
    public String youtube(@Valid VideoCreateForm videoCreateForm, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return "upload_form"; //업로드 창
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails)principal;
        System.out.println(userDetails.getUsername());

        videoService.upload(videoCreateForm.getTitle(), videoCreateForm.getDetail(), userDetails.getUsername(), videoCreateForm.getVideoName());

        return "redirect:/";

    }

    @GetMapping("/list")
    public String list(Model model){
        List<SiteVideo> videoList = videoRepository.findAll();
        model.addAttribute("videoList", videoList);
        return "video_list";
    }

}
