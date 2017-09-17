package ua.abond.instaret.service;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import ua.abond.instaret.dto.FollowedBy;
import ua.abond.instaret.exception.PreAuthorizedInstagramServiceInitializationException;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PreAuthorizedInstagramAPIService {

    private final Authentication authentication;
    private final InstagramAPIService instagramService;

    public String getUserId(String userName) throws IOException {
        return instagramService.getUserId(authentication, userName);
    }

    public Set<FollowedBy> getFollowers(String userId) throws Exception {
        return instagramService.getFollowersById(authentication, userId);
    }

    public Set<FollowedBy> getFollowers(String userId, int batchSize) throws Exception {
        return instagramService.getFollowersById(authentication, userId, batchSize);
    }

    public static PreAuthorizedInstagramAPIService create(InstagramAPIService instagramService,
                                                          InstagramPreAuthorizationProperties props) {
        if (Stream.of(props.getPassword(), props.getUsername()).allMatch(Objects::isNull)) {
            throw new PreAuthorizedInstagramServiceInitializationException("Login or password is missing");
        }
        Authentication auth;
        try {
            auth = instagramService.login(props.getUsername(), props.getPassword());
        } catch (Exception e) {
            throw new PreAuthorizedInstagramServiceInitializationException(e);
        }
        return new PreAuthorizedInstagramAPIService(auth, instagramService);
    }

}
