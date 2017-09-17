package ua.abond.instaret.facade;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import ua.abond.instaret.dto.FollowedBy;
import ua.abond.instaret.entity.MongoFollowerSnapshot;
import ua.abond.instaret.model.FollowerDiff;
import ua.abond.instaret.model.FollowerSnapshot;
import ua.abond.instaret.repository.FollowerDiffRepository;
import ua.abond.instaret.repository.FollowerSnapshotRepository;
import ua.abond.instaret.repository.UserRepository;
import ua.abond.instaret.service.PreAuthorizedInstagramAPIService;
import ua.abond.instaret.util.LocalDateTimeInterval;

@Component
@RequiredArgsConstructor
public class InstagramFollowerFacade {

    private final PreAuthorizedInstagramAPIService instagramApi;

    // TODO: it does not look like facade as it is only working with repositories
    private final UserRepository userRepository;
    private final FollowerDiffRepository followerDiffRepository;
    private final FollowerSnapshotRepository followerSnapshotRepository;

    public FollowerSnapshot updateSnapshot(String userName) throws Exception {
        String userId = getUserId(userName);

        Optional<FollowerSnapshot> oldSnapshot = followerSnapshotRepository.getSnapshot(userId);
        // TODO: this initialization should not be here
        FollowerSnapshot newSnapshot = new MongoFollowerSnapshot(userId,
                LocalDateTime.now(), instagramApi.getFollowers(userId));

        // TODO: could possibly optimize replaceSnapshot to return old snapshot
        // TODO: so we will make only 1 query to the database
        if (oldSnapshot.isPresent()) {
            FollowerSnapshot snapshot = oldSnapshot.get();
            FollowerDiff diff = newSnapshot.diff(snapshot);
            followerDiffRepository.pushDiff(diff);
        }
        followerSnapshotRepository.replaceSnapshot(newSnapshot);

        return newSnapshot;
    }

    public FollowerDiff getLastDiff(String userName) {
        String userId = getUserId(userName);
        return followerDiffRepository.getLastDiff(userId);
    }

    public Set<FollowerDiff> getDiffBetween(String userName, LocalDateTimeInterval interval) {
        String userId = getUserId(userName);
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
    public Set<FollowedBy> getRetards(String userName) throws Exception {
        String userId = getUserId(userName);
        return followerDiffRepository.getLastDiff(userId).getRemoved();
    }

    public Set<FollowedBy> getNewFollowers(String userName) throws Exception {
        // TODO: add check if user is in database | Bloom filter
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

}
