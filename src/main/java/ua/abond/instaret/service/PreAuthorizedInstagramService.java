package ua.abond.instaret.service;

import ua.abond.instaret.entity.FollowedBy;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class PreAuthorizedInstagramService {

    private static final String LOGIN = "usrnm";
    private static final String PASSWORD = "pwd";

    private final Authentication authentication;
    private final InstagramService instagramService;
    private final FollowerRepository followerRepository;

    public PreAuthorizedInstagramService(InstagramService instagramService)
        throws Exception {
        this.instagramService = instagramService;
        this.followerRepository = new InMemoryFollowerRepository();
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

    public void snapshotFollowers(String user) throws Exception {
        List<FollowedBy> followers = getFollowers(user);
        followerRepository.persist(user, followers);
    }

    public List<FollowedBy> followerDifference(String user) throws Exception {
        List<FollowedBy> recentFollowers = getFollowers(user);
        List<FollowedBy> oldFollowers = followerRepository.getFollowers(user);

        return oldFollowers.stream()
            .filter(follower -> !recentFollowers.contains(follower))
            .collect(Collectors.toList());
    }

}
