package ua.abond.metrics.facade.follower;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import ua.abond.metrics.domain.follower.FollowerDiff;
import ua.abond.metrics.domain.follower.FollowerSnapshot;
import ua.abond.metrics.domain.follower.ImmutableFollowerSnapshot;
import ua.abond.metrics.service.dto.FollowedBy;
import ua.abond.metrics.repository.FollowerDiffRepository;
import ua.abond.metrics.repository.FollowerSnapshotRepository;
import ua.abond.metrics.service.dto.FollowingDto;
import ua.abond.metrics.service.insta.PreAuthorizedInstaService;
import ua.abond.metrics.util.LocalDateTimeInterval;

@Component
@RequiredArgsConstructor
public class InstaFollowerFacade {

    private final PreAuthorizedInstaService instagramApi;

    // TODO: it does not look like facade as it is only working with repositories
    private final FollowerDiffRepository followerDiffRepository;
    private final FollowerSnapshotRepository followerSnapshotRepository;

    public FollowerSnapshot updateSnapshot(String userName) throws Exception {
        String userId = getUserId(userName);
        FollowerSnapshot newSnapshot =
            new ImmutableFollowerSnapshot(userId, LocalDateTime.now(), instagramApi.getFollowers(userId));

        Optional<FollowerSnapshot> oldSnapshot = followerSnapshotRepository.getSnapshot(userId);
        followerSnapshotRepository.replaceSnapshot(newSnapshot);
        Option.ofOptional(oldSnapshot)
            .map(newSnapshot::diff)
            .peek(followerDiffRepository::pushDiff);

        return newSnapshot;
    }

    public Set<FollowerDiff> getDiffBetween(String userName, LocalDateTimeInterval interval) {
        String userId = getUserId(userName);
        return followerDiffRepository.getDiffBetweenDates(userId, interval);
    }

    public Set<FollowerDiff> getAllDiffs(String userName) {
        String userId = getUserId(userName);
        LocalDateTime min = LocalDateTime.of(1991, 10, 4, 0, 0);
        LocalDateTimeInterval interval = new LocalDateTimeInterval(min, LocalDateTime.now());

        return followerDiffRepository.getDiffBetweenDates(userId, interval);
    }

    /**
     * TODO: possibly could add Bloom filter to verify that user is in database
     */
    public Set<FollowedBy> getFollowers(String userName) throws Exception {
        String userId = getUserId(userName);
        return instagramApi.getFollowers(userId);
    }

    /**
     * TODO: think about retards of all time functionality
     */
    public Set<FollowedBy> getRetards(String userName) {
        String userId = getUserId(userName);
        return followerDiffRepository.getLastDiff(userId).getRemoved();
    }

    public Set<FollowedBy> getNewFollowers(String userName) {
        // TODO: add check if user is in database
        String userId = getUserId(userName);
        return followerDiffRepository.getLastDiff(userId).getAdded();
    }

    private String getUserId(String userName) {
        try {
            return instagramApi.getUserId(userName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch user id from the instagram service", e);
        }
    }

    public Set<FollowingDto> getFollowing(String userName) {
        try {
            return instagramApi.getFollowing(getUserId(userName));
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user following.", e);
        }
    }

}
