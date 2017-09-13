package ua.abond.instaret.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.abond.instaret.dto.FollowedBy;
import ua.abond.instaret.exception.PreAuthorizedInstagramServiceInitializationException;
import ua.abond.instaret.repository.FollowerRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PreAuthorizedInstagramService {

    private final Authentication authentication;
    private final InstagramService instagramService;
    private final FollowerRepository followerRepository;

    public String getUserId(String userName) throws IOException {
        return instagramService.getUserId(authentication, userName);
    }

    public List<FollowedBy> getFollowers(String user) throws Exception {
        return instagramService.getFollowers(authentication, user);
    }

    public List<FollowedBy> getFollowers(String user, int batchSize) throws Exception {
        return instagramService.getFollowers(authentication, user, batchSize);
    }

    public void snapshotFollowers(String userName) throws Exception {
        followerRepository.persist(userName, getFollowers(userName));
    }

    public List<FollowedBy> getRetards(String user) throws Exception {
        List<FollowedBy> currentFollowers = getFollowers(user);
        List<FollowedBy> oldFollowers = followerRepository.getFollowers(user);

        return subtractLeft(currentFollowers, oldFollowers);
    }

    public List<FollowedBy> getNewFollowers(String userName) throws Exception {
        List<FollowedBy> currentFollowers = getFollowers(userName);
        List<FollowedBy> oldFollowers = followerRepository.getFollowers(userName);

        return subtractLeft(currentFollowers, oldFollowers);
    }

    private <T> List<T> subtractLeft(List<T> left, List<T> right) {
        if (left == null) {
            return Collections.emptyList();
        }
        return left.stream().filter(t -> !right.contains(t))
            .collect(Collectors.toList());
    }

    public static PreAuthorizedInstagramService create(InstagramService instagramService,
                                                       FollowerRepository followerRepository,
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
        return new PreAuthorizedInstagramService(auth, instagramService, followerRepository);
    }

    public FollowerData diff(String userName) throws Exception {
        FollowerData initial = null; // getFollowerData(userName);
        List<FollowerData> previousDiffs = null;
        List<FollowedBy> currentFollowers = null; // query currentFollowerData

//        return initial.apply(previousDiffs).diff(currentFollowers);
        return null;
    }

    @Getter
    private static class FollowerData {
        private final List<String> retards;
        private final List<String> followers;

        FollowerData(List<String> retards, List<String> followers) {
            this.retards = Collections.unmodifiableList(retards);
            this.followers = Collections.unmodifiableList(followers);
        }
    }
}
