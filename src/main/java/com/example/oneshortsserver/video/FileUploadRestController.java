package com.example.oneshortsserver.video;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.example.oneshortsserver.facebook.FacebookRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;


@RequiredArgsConstructor
@RestController
public class FileUploadRestController {

    /**
     * Location to save uploaded files on server
     */
    private static String UPLOAD_PATH = "src/main/resources/static/";
    private final VideoService videoService;
    private final FacebookRepository facebookRepository;
    /*
     * single file upload in a request
     */
    @PostMapping("/fileupload")
    public void uploadFile(@RequestParam("multipartFile") MultipartFile uploadfile, @RequestParam("title")String title, @RequestParam("detail") String detail, @RequestParam("youtubeCheck")boolean isYoutube, @RequestParam("facebookCheck")boolean isFacebook, @RequestParam("instaCheck")boolean isInsta) {

        if (uploadfile.isEmpty()) {
            System.out.println("please select a file!");
        }

        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String userId = userDetails.getUsername();
            String accessToken = facebookRepository.findFacebookInfoByUsername(userId).get().getAccessToken();
            URI uri;
            RestTemplate restTemplate = new RestTemplate();
            String pageId = null;
            String videoName = null;
            RequestEntity<Void> req = null;
            ResponseEntity<String> result = null;
            JSONParser parser = null;
            JSONObject object = null;
            final String videoURL = "https://43.201.131.44:8080/";

            //유튜브 업로드
            if (isYoutube) {
                videoService.upload(title, detail, userId, uploadfile.getInputStream());
            }
            if(isFacebook||isInsta) {

                videoName = uploadfile.getOriginalFilename();
                byte[] bytes = uploadfile.getBytes();
                Path path = Paths.get(UPLOAD_PATH + videoName);
                Files.write(path, bytes);


                uri = UriComponentsBuilder
                        .fromUriString("https://graph.facebook.com")
                        .path("/v15.0/me/accounts")
                        .queryParam("access_token", accessToken)
                        .encode()
                        .build()
                        .toUri();

                req = RequestEntity.get(uri).build();
                result = restTemplate.exchange(req, String.class);

                parser = new JSONParser();
                object = (JSONObject) parser.parse(result.getBody());
                JSONArray arr = (JSONArray) object.get("data");
                object = (JSONObject) arr.get(0);
                pageId = object.get("id").toString();

            }
            //인스타 업로드
            //pageId 찾기
            if(isInsta) {

                // page에 대한 instagram_business_account 받아오기
                uri = UriComponentsBuilder
                        .fromUriString("https://graph.facebook.com")
                        .path("/v15.0/" + pageId)
                        .queryParam("fields", "instagram_business_account")
                        .queryParam("access_token", accessToken)
                        .encode()
                        .build()
                        .toUri();

                req = RequestEntity.get(uri).build();
                result = restTemplate.exchange(req, String.class);

                object = (JSONObject) parser.parse(result.getBody());
                object = (JSONObject) object.get("instagram_business_account");
                String instagram_business_account = object.get("id").toString();


                //uploadPost 해놓기
                uri = UriComponentsBuilder
                        .fromUriString("https://graph.facebook.com")
                        .path("v14.0/" + instagram_business_account + "/media")
                        .queryParam("media_type", "REELS")
                        .queryParam("video_url", videoURL+videoName)
                        .queryParam("caption", detail)
                        .queryParam("access_token", accessToken)
                        .encode()
                        .build()
                        .toUri();

                req = RequestEntity.post(uri).build();
                result = restTemplate.exchange(req, String.class);

                object = (JSONObject) parser.parse(result.getBody());
                String containerId = object.get("id").toString();

                //업로드 확인하기
                String status_code = "";
                while (!status_code.equals("FINISHED")) {
                    uri = UriComponentsBuilder
                            .fromUriString("https://graph.facebook.com")
                            .path("v15.0/" + containerId)
                            .queryParam("fields", "status_code")
                            .queryParam("access_token", accessToken)
                            .encode()
                            .build()
                            .toUri();
                    req = RequestEntity.get(uri).build();
                    result = restTemplate.exchange(req, String.class);
                    object = (JSONObject) parser.parse(result.getBody());
                    status_code = object.get("status_code").toString();
                    System.out.println(status_code);

                }

                //최종 업로드
                uri = UriComponentsBuilder
                        .fromUriString("https://graph.facebook.com")
                        .path("v14.0/" + instagram_business_account + "/media_publish")
                        .queryParam("creation_id", containerId)
                        .queryParam("access_token", accessToken)
                        .encode()
                        .build()
                        .toUri();

                req = RequestEntity.post(uri).build();
                result = restTemplate.exchange(req, String.class);

                System.out.println(result.getBody());
            }

            if(isFacebook) {

                //페이스북 업로드
                //페이지 토큰 발급
                uri = UriComponentsBuilder
                        .fromUriString("https://graph.facebook.com")
                        .path("v15.0/" + pageId)
                        .queryParam("fields", "access_token")
                        .queryParam("access_token", accessToken)
                        .encode()
                        .build()
                        .toUri();

                req = RequestEntity.get(uri).build();
                result = restTemplate.exchange(req, String.class);

                object = (JSONObject) parser.parse(result.getBody());
                String pageToken = object.get("access_token").toString();

                //video_id 발급
                uri = UriComponentsBuilder
                        .fromUriString("https://graph.facebook.com")
                        .path("v15.0/" + pageId + "/video_reels")
                        .queryParam("upload_phase", "start")
                        .queryParam("access_token", pageToken)
                        .encode()
                        .build()
                        .toUri();

                req = RequestEntity.post(uri).build();
                result = restTemplate.exchange(req, String.class);

                object = (JSONObject) parser.parse(result.getBody());
                String videoId = object.get("video_id").toString();
                System.out.println("videoId: " + videoId);
                //업로드 사전작업
                uri = UriComponentsBuilder
                        .fromUriString("https://rupload.facebook.com")
                        .path("video-upload/v13.0/" + videoId)
                        .encode()
                        .build()
                        .toUri();

                req = RequestEntity.post(uri).header("Authorization", "OAuth " + pageToken).header("file_url", videoURL+videoName).build();
                result = restTemplate.exchange(req, String.class);

                //업로드 체크
                String status_code = "";

                while (!status_code.equals("complete")) {
                    uri = UriComponentsBuilder
                            .fromUriString("https://graph.facebook.com")
                            .path("v15.0/" + videoId)
                            .queryParam("fields", "status")
                            .queryParam("access_token", pageToken)
                            .encode()
                            .build()
                            .toUri();
                    req = RequestEntity.get(uri).build();
                    result = restTemplate.exchange(req, String.class);
                    object = (JSONObject) parser.parse(result.getBody());
                    object = (JSONObject) object.get("status");
                    object = (JSONObject) object.get("uploading_phase");
                    status_code = object.get("status").toString();
                    System.out.println(status_code);
                }

                //최종 업로드
                uri = UriComponentsBuilder
                        .fromUriString("https://graph.facebook.com")
                        .path("v15.0/" + pageId + "/video_reels")
                        .queryParam("video_id", videoId)
                        .queryParam("upload_phase", "finish")
                        .queryParam("video_state", "PUBLISHED")
                        .queryParam("description", detail)
                        .queryParam("access_token", pageToken)
                        .encode()
                        .build()
                        .toUri();

                req = RequestEntity.post(uri).build();
                result = restTemplate.exchange(req, String.class);
                System.out.println(result.getBody());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

