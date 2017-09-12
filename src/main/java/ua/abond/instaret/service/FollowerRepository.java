package ua.abond.instaret.service;

import ua.abond.instaret.entity.FollowedBy;

import java.io.IOException;
import java.util.List;

public interface FollowerRepository {

    List<FollowedBy> getFollowers(String user) throws IOException;

    void persist(String user, List<FollowedBy> followers);

}
