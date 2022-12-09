package com.example.oneshortsserver.youtube;

import com.example.oneshortsserver.youtube.account.YoutubeAccountInfo;
import com.example.oneshortsserver.youtube.account.YoutubeAccountRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;

@Controller
@RequiredArgsConstructor
public class YoutubeController {

    public static final String CLIENT_ID = "566410170787-i5gvdf2r7ksdi8tkkpbmava3spl2brjq.apps.googleusercontent.com";
    public static final String CLIENT_SECRET = "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";
    private final String REDIRECT_URI = "https://43.201.131.44:8080/regist/youtube";
    private final YoutubeAccountRepository youtubeAccountRepository;

    @GetMapping("/regist/youtube")
    public String callback(HttpServletRequest request) throws IOException, ParseException {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String code = request.getParameter("code");

        URI uri = UriComponentsBuilder
                .fromUriString("https://oauth2.googleapis.com")
                .path("token")
                .queryParam("code", code)
                .queryParam("client_id", CLIENT_ID)
                .queryParam("client_secret", CLIENT_SECRET)
                .queryParam("redirect_uri", REDIRECT_URI)
                .queryParam("grant_type", "authorization_code")
                .encode()
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();

        RequestEntity<Void> req = RequestEntity.post(uri).header("content-type", "application/x-www-form-urlencoded").build();
        ResponseEntity<String> result = restTemplate.exchange(req, String.class);

        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(result.getBody());

        YoutubeAccountInfo youtubeAccountInfo = new YoutubeAccountInfo();
        youtubeAccountInfo.setUsername(userDetails.getUsername());
        youtubeAccountInfo.setAccessToken(object.get("access_token").toString());
        youtubeAccountRepository.save(youtubeAccountInfo);

        System.out.println(object.get("access_token").toString());

        return "redirect:/api/regist";
    }

}
