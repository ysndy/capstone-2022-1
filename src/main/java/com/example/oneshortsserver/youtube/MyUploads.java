package com.example.oneshortsserver.youtube;/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;

import com.example.oneshortsserver.youtube.video.YoutubeVideoInfo;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.google.common.collect.Lists;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Prints a list of videos uploaded to the user's YouTube account using OAuth2 for authentication.
 * <p>
 * Details: The app uses Youtube.Channnels.List to get the playlist id associated with all the
 * videos ever uploaded to the user's account. It then gets all the video info using
 * YouTube.PlaylistItems.List. Finally, it prints all the information to the screen.
 *
 * @author Jeremy Walker
 */
public class MyUploads {

    /**
     * Global instance of the HTTP transport.
     */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    /**
     * Global instance of YouTube object to make all API requests.
     */
    private static YouTube youtube;

    public static List<YoutubeVideoInfo> getYoutubeVideoInfoList(String accessToken) {

        // Scope required to upload to YouTube.
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");
        List<YoutubeVideoInfo> youtubeVideoInfoList = new LinkedList<>();
        try {
            // Authorization.
            Credential credential = new GoogleCredential().setAccessToken(accessToken);

            // YouTube object used to make all API requests.
            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
                    "oneshorts").build();

            /*
             * Now that the user is authenticated, the app makes a channel list request to get the
             * authenticated user's channel. Returned with that data is the playlist id for the uploaded
             * videos. https://developers.google.com/youtube/v3/docs/channels/list
             */
            List<String> part = new LinkedList<>();
            part.add("contentDetails");
            YouTube.Channels.List channelRequest = youtube.channels().list(part);
            channelRequest.setMine(true);
            /*
             * Limits the results to only the data we needo which makes things more efficient.
             */
            channelRequest.setFields("items/contentDetails,nextPageToken,pageInfo");
            ChannelListResponse channelResult = channelRequest.execute();

            /*
             * Gets the list of channels associated with the user. This sample only pulls the uploaded
             * videos for the first channel (default channel for user).
             */
            List<Channel> channelsList = channelResult.getItems();

            if (channelsList != null) {
                // Gets user's default channel id (first channel in list).
                String uploadPlaylistId =
                        channelsList.get(0).getContentDetails().getRelatedPlaylists().getUploads();
                System.out.println("uploadPlaylistId=" + uploadPlaylistId);

                // List to store all PlaylistItem items associated with the uploadPlaylistId.
                List<PlaylistItem> playlistItemList = new ArrayList<PlaylistItem>();

                /*
                 * Now that we have the playlist id for your uploads, we will request the playlistItems
                 * associated with that playlist id, so we can get information on each video uploaded. This
                 * is the template for the list call. We call it multiple times in the do while loop below
                 * (only changing the nextToken to get all the videos).
                 * https://developers.google.com/youtube/v3/docs/playlistitems/list
                 */
                List<String> playListItemsList = new LinkedList<>();
                playListItemsList.add("id");
                playListItemsList.add("contentDetails");
                playListItemsList.add("snippet");
                YouTube.PlaylistItems.List playlistItemRequest =
                        youtube.playlistItems().list(playListItemsList);
                playlistItemRequest.setPlaylistId(uploadPlaylistId);

                // This limits the results to only the data we need and makes things more efficient.
                playlistItemRequest.setFields(
                        "items(contentDetails/videoId,snippet/title, snippet/description, snippet/thumbnails/standard/url, snippet/publishedAt),nextPageToken,pageInfo");

                String nextToken = "";

                // Loops over all search page results returned for the uploadPlaylistId.
                do {
                    playlistItemRequest.setPageToken(nextToken);
                    PlaylistItemListResponse playlistItemResult = playlistItemRequest.execute();

                    playlistItemList.addAll(playlistItemResult.getItems());

                    nextToken = playlistItemResult.getNextPageToken();
                } while (nextToken != null);

                // Prints results.

                Iterator<PlaylistItem> playlistEntries = playlistItemList.iterator();


                while (playlistEntries.hasNext()) {
                    PlaylistItem playlistItem = playlistEntries.next();
                    YoutubeVideoInfo youtubeVideoInfo = new YoutubeVideoInfo();

                    try {
                        youtubeVideoInfo.setDetail(playlistItem.getSnippet().getDescription());

                        if(!youtubeVideoInfo.getDetail().contains("#shorts")) throw new Exception("123");

                        youtubeVideoInfo.setId(playlistItem.getContentDetails().getVideoId());
                        youtubeVideoInfo.setTitle(playlistItem.getSnippet().getTitle());
                        youtubeVideoInfo.setDate(playlistItem.getSnippet().getPublishedAt());
                        youtubeVideoInfo.setThumbnailUrl(playlistItem.getSnippet().getThumbnails().getStandard().getUrl());

                        RestTemplate restTemplate = new RestTemplate();

                        URI uri = UriComponentsBuilder
                                .fromUriString("https://www.googleapis.com")
                                .path("youtube/v3/videos")
                                .queryParam("part", "snippet, statistics")
                                        .queryParam("id", youtubeVideoInfo.getId())
                                                .queryParam("maxResults", "50")
                                                        .queryParam("key", "AIzaSyBA5-8aLpt6pcl2ECUMfDxABtVxYV_B3qM")
                                                                .encode()
                                                                        .build()
                                                                                .toUri();


                        RequestEntity<Void> req = RequestEntity.get(uri).build();
                        ResponseEntity<String> result = restTemplate.exchange(req, String.class);
                        JSONParser parser = new JSONParser();
                        JSONObject object = (JSONObject) parser.parse(result.getBody());
                        JSONArray jsonArray = (JSONArray) object.get("items");
                        object = (JSONObject) jsonArray.get(0);
                        object = (JSONObject) object.get("statistics");

                        youtubeVideoInfo.setCommentCount(object.get("commentCount").toString());
                        youtubeVideoInfo.setLikeCount(object.get("likeCount").toString());
                        youtubeVideoInfo.setViewCount(object.get("viewCount").toString());

                        System.out.println(result.getBody());


                        youtubeVideoInfoList.add(youtubeVideoInfo);

                    }catch (HttpClientErrorException e){

                        e.printStackTrace();

                    } catch (Exception e){

                    }

                }

            }

        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return youtubeVideoInfoList;
    }

    /*
     * Method that prints all the PlaylistItems in an Iterator.
     *
     * @param size size of list
     *
     * @param iterator of Playlist Items from uploaded Playlist
     */
    private static void prettyPrint(int size, Iterator<PlaylistItem> playlistEntries) {
        System.out.println("=============================================================");
        System.out.println("\t\tTotal Videos Uploaded: " + size);
        System.out.println("=============================================================\n");

        while (playlistEntries.hasNext()) {
            PlaylistItem playlistItem = playlistEntries.next();
            System.out.println(" video name  = " + playlistItem.getSnippet().getTitle());
            System.out.println(" video id    = " + playlistItem.getContentDetails().getVideoId());
            System.out.println(" video decription= " + playlistItem.getSnippet().getDescription());
            System.out.println(" upload date = " + playlistItem.getSnippet().getPublishedAt());
            System.out.println(" thumbnails = "+playlistItem.getSnippet().getThumbnails().getDefault().getUrl());
            System.out.println("\n-------------------------------------------------------------\n");

        }
    }
}