package ua.abond.instaret.repository;

import ua.abond.instaret.dto.FollowedBy;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface FollowerRepository {

    List<FollowedBy> getFollowers(String user) throws IOException;

    void persist(String user, List<FollowedBy> followers) throws IOException, URISyntaxException;

}
