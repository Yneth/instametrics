package ua.abond.instaret.controller;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InstagramService {

    private final RestTemplate restTemplate;

    public InstagramService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public FollowedBy getFollowers(String accessToken) {
        String url = "https://api.instagram.com/v1/users/self/followed-by?access_token=" + accessToken;
        return restTemplate.getForEntity(url, FollowedBy.class).getBody();
    }

}
