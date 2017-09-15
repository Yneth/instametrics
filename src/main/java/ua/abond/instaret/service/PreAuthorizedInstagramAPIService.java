package ua.abond.instaret.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.abond.instaret.dto.FollowedBy;
import ua.abond.instaret.exception.PreAuthorizedInstagramServiceInitializationException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PreAuthorizedInstagramAPIService {

    private final Authentication authentication;
    private final InstagramAPIService instagramService;

    public String getUserId(String userName) throws IOException {
        return instagramService.getUserId(authentication, userName);
    }

    public List<FollowedBy> getFollowers(String user) throws Exception {
        return instagramService.getFollowers(authentication, user);
    }

    public List<FollowedBy> getFollowers(String user, int batchSize) throws Exception {
        return instagramService.getFollowers(authentication, user, batchSize);
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
