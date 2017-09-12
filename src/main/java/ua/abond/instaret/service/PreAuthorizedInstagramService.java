package ua.abond.instaret.service;

import ua.abond.instaret.entity.FollowedBy;

import java.io.IOException;
import java.util.List;

public class PreAuthorizedInstagramService {

    private static final String LOGIN = "usrnm";
    private static final String PASSWORD = "pwd";

    private final Authentication authentication;
    private final InstagramService instagramService;

    public PreAuthorizedInstagramService(InstagramService instagramService)
        throws Exception {
        this.instagramService = instagramService;
        this.authentication = instagramService.login(LOGIN, PASSWORD);
    }

    public List<FollowedBy> getFollowers(String user, int batchSize) throws Exception {
        return instagramService.getFollowers(authentication, user, batchSize);
    }

    public List<FollowedBy> getFollowers(String user) throws Exception {
        return instagramService.getFollowers(authentication, user);
    }

    public String getUserId(String userName) throws IOException {
        return instagramService.getUserId(authentication, userName);
    }

}
