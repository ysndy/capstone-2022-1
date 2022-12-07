package com.example.oneshortsserver.video;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
public class FileUploadRestController {

    /**
     * Location to save uploaded files on server
     */
    private static String UPLOAD_PATH = "C:\\Users\\82107\\IdeaProjects\\oneShortsServer\\oneshortsserver\\src\\main\\resources\\";
    private final VideoService videoService;
    /*
     * single file upload in a request
     */
    @GetMapping("/fileupload")
    public String uploadFile(){
        return "form";
    }

    @PostMapping("/fileupload")
    public String uploadFile(@RequestParam("multipartFile") MultipartFile uploadfile, @RequestParam("title")String title, @RequestParam("detail") String detail) {

        if (uploadfile.isEmpty()) {
            System.out.println("please select a file!");
        }

        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserDetails userDetails = (UserDetails)principal;
            videoService.upload(title, detail, userDetails.getUsername(), uploadfile.getInputStream());

            //byte[] bytes = uploadfile.getBytes();
            //Path path = Paths.get(UPLOAD_PATH + uploadfile.getOriginalFilename());
            //Files.write(path, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/";

    }



}

