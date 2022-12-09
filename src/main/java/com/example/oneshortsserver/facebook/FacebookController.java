package com.example.oneshortsserver.facebook;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;

@Controller
@RequiredArgsConstructor
public class FacebookController {

    final String APP_ID = "5933675523361973";
    final String API_SECRET = "c0f7c06c728baf8ebbddad7a290e552b";
    final String REDIRECT_URI = "https://43.201.131.44:8443/facebook/regist";
    final FacebookRepository facebookRepository;


    @GetMapping("/facebook/regist")
    public String callback(HttpServletRequest request, HttpServletResponse response) throws ParseException {
        String code = request.getParameter("code");

        //단기 액세스토큰 받기
        URI uri = UriComponentsBuilder
                .fromUriString("https://graph.facebook.com")
                .path("/oauth/access_token")
                .queryParam("client_id", APP_ID)
                .queryParam("redirect_uri", REDIRECT_URI)
                .queryParam("client_secret", API_SECRET)
                .queryParam("code", code)
                .encode()
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();

        RequestEntity<Void> req = RequestEntity.post(uri).build();
        ResponseEntity<String> result = restTemplate.exchange(req, String.class);
        System.out.println(result.getBody());

        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(result.getBody());
        String shortsAccessToken = object.get("access_token").toString();

        //장기 액세스토큰 받기
        uri = UriComponentsBuilder
                .fromUriString("https://graph.facebook.com")
                .path("/oauth/access_token")
                .queryParam("client_id", APP_ID)
                .queryParam("client_secret", API_SECRET)
                .queryParam("grant_type", "fb_exchange_token")
                .queryParam("fb_exchange_token", shortsAccessToken)
                .encode()
                .build()
                .toUri();

        req = RequestEntity.get(uri).build();
        result = restTemplate.exchange(req, String.class);
        System.out.println(result.getBody());

        object = (JSONObject) parser.parse(result.getBody());
        String longAccessToken = object.get("access_token").toString();

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        FacebookAccountInfo facebookAccountInfo = new FacebookAccountInfo();
        facebookAccountInfo.setUsername(userDetails.getUsername());
        facebookAccountInfo.setAccessToken(longAccessToken);
        facebookRepository.save(facebookAccountInfo);

        return "account_regist";
    }



}
