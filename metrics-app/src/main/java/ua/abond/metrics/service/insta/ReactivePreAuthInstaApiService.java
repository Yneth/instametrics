package ua.abond.metrics.service.insta;

import java.util.Objects;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.abond.metrics.service.dto.FollowedBy;
import ua.abond.metrics.service.insta.exception.PreAuthInstaServiceInitException;
import ua.abond.metrics.service.dto.AuthenticationDto;

@RequiredArgsConstructor
public class ReactivePreAuthInstaApiService {

    private final AuthenticationDto authentication;
    private final ReactiveInstaApiService instagramService;

    public Mono<String> getUserId(String userName) {
        return instagramService.getUserId(authentication, userName);
    }

    public Flux<FollowedBy> getFollowers(String userId) {
        return instagramService.getFollowersById(authentication, userId);
    }

    public Flux<FollowedBy> getFollowers(String userId, int batchSize) {
        return instagramService.getFollowersById(authentication, userId, batchSize);
    }

    public static ReactivePreAuthInstaApiService create(ReactiveInstaApiService instagramService,
                                                        PreAuthInstaProperties props) {
        if (Stream.of(props.getPassword(), props.getUsername()).allMatch(Objects::isNull)) {
            throw new PreAuthInstaServiceInitException("Login or password is missing");
        }
        AuthenticationDto auth;
        try {
            auth = instagramService.login(props.getUsername(), props.getPassword()).block();
        } catch (Exception e) {
            throw new PreAuthInstaServiceInitException(e);
        }
        return new ReactivePreAuthInstaApiService(auth, instagramService);
    }

}
