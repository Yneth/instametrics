package ua.abond.metrics.service.insta;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import ua.abond.metrics.service.dto.FollowedBy;
import ua.abond.metrics.service.insta.exception.PreAuthInstaServiceInitException;
import ua.abond.metrics.service.dto.AuthenticationDto;
import ua.abond.metrics.service.dto.FollowingDto;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PreAuthInstaService {

    private final AuthenticationDto authentication;
    private final InstaApiService instagramService;

    public String getUserId(String userName) throws IOException {
        return instagramService.getUserId(authentication, userName);
    }

    public Set<FollowedBy> getFollowers(String userId) throws Exception {
        return instagramService.getFollowersById(authentication, userId);
    }

    public Set<FollowingDto> getFollowing(String userId) throws Exception {
        return instagramService.getFollowingById(authentication, userId);
    }

    public static PreAuthInstaService create(InstaApiService instagramService,
                                             PreAuthInstaProperties props) {
        if (Stream.of(props.getPassword(), props.getUsername()).allMatch(Objects::isNull)) {
            throw new PreAuthInstaServiceInitException("Login or password is missing");
        }
        AuthenticationDto auth;
        try {
            auth = instagramService.login(props.getUsername(), props.getPassword());
        } catch (Exception e) {
            throw new PreAuthInstaServiceInitException(e);
        }
        return new PreAuthInstaService(auth, instagramService);
    }

}
