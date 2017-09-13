package ua.abond.instaret.facade;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import ua.abond.instaret.dto.FollowedBy;
import ua.abond.instaret.entity.FollowerDiff;
import ua.abond.instaret.entity.FollowerSnapshot;
import ua.abond.instaret.exception.PreAuthorizedInstagramServiceInitializationException;
import ua.abond.instaret.repository.FollowerRepository;
import ua.abond.instaret.service.Authentication;
import ua.abond.instaret.service.FollowerDiffService;
import ua.abond.instaret.service.FollowerSnapshotService;
import ua.abond.instaret.service.InstagramPreAuthorizationProperties;
import ua.abond.instaret.service.InstagramService;
import ua.abond.instaret.util.LocalDateTimeInterval;

@Service
@RequiredArgsConstructor
public class PreAuthorizedInstagramService {

    private final Authentication authentication;
    private final InstagramService instagramService;

    // move to facade
    private final FollowerRepository followerRepository;
    private final FollowerDiffService followerDiffService;
    private final FollowerSnapshotService followerSnapshotService;

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
                                                       FollowerDiffService followerDiffService,
                                                       FollowerSnapshotService followerSnapshotService,
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
        return new PreAuthorizedInstagramService(auth, instagramService,
                followerRepository, followerDiffService, followerSnapshotService);
    }

    public FollowerSnapshot updateSnapshot(String userName) throws Exception {
        FollowerSnapshot head = followerSnapshotService.getCurrentSnapshot(userName);
        FollowerSnapshot current = followerSnapshotService.loadNewSnapshot(userName);

        FollowerDiff diff = followerDiffService.getDiff(head, current);
        followerDiffService.pushDiff(diff);
        followerSnapshotService.dropPreviousSnapshot(head);

        return current;
    }

    public FollowerDiff getLastDiff(String userName) {
        return followerDiffService.getLastDiff(userName);
    }

    public List<FollowerDiff> getDiffBetween(String userName, LocalDateTimeInterval interval) {
        return followerDiffService.getDiffBetweenDates(userName, interval);
    }

}
