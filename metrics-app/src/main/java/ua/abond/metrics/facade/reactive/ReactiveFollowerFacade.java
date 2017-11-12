//package ua.abond.metrics.facade.reactive;
//
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.Optional;
//import java.util.Set;
//
//import lombok.RequiredArgsConstructor;
//import ua.abond.metrics.service.dto.FollowedBy;
//import ua.abond.metrics.entity.ImmutableFollowerSnapshot;
//import ua.abond.metrics.entity.MongoFollowerSnapshot;
//import ua.abond.metrics.facade.InstagramFollowerFacade;
//import ua.abond.instaret.model.FollowerDiff;
//import ua.abond.instaret.model.FollowerSnapshot;
//import ua.abond.metrics.service.insta.ReactivePreAuthInstaApiService;
//import ua.abond.instaret.util.LocalDateTimeInterval;
//
//@Service
//@RequiredArgsConstructor
//public class ReactiveFollowerFacade {
//
//    private ReactivePreAuthInstaApiService instagramService;
//    private InstagramFollowerFacade instagramFollowerFacade;
//
//    public FollowerSnapshot updateSnapshot(String userName) throws Exception {
//        instagramService.getUserId(userName)
//                .
//
//        Optional<FollowerSnapshot> oldSnapshot = followerSnapshotRepository.getSnapshot(userId);
//        // TODO: this initialization should not be here
//        FollowerSnapshot newSnapshot = new ImmutableFollowerSnapshot(userId,
//                LocalDateTime.now(), instagramApi.getFollowers(userId));
//
//        // TODO: could possibly optimize replaceSnapshot to return old snapshot
//        // TODO: so we will make only 1 query to the database
//        if (oldSnapshot.isPresent()) {
//            FollowerSnapshot snapshot = oldSnapshot.get();
//            FollowerDiff diff = newSnapshot.diff(snapshot);
//            followerDiffRepository.pushDiff(diff);
//        }
//        followerSnapshotRepository.replaceSnapshot(newSnapshot);
//
//        return newSnapshot;
//    }
//
//    public FollowerDiff getLastDiff(String userName) {
//        String userId = getUserId(userName);
//        return followerDiffRepository.getLastDiff(userId);
//    }
//
//    public Set<FollowerDiff> getDiffBetween(String userName, LocalDateTimeInterval interval) {
//        String userId = getUserId(userName);
//        return followerDiffRepository.getDiffBetweenDates(userId, interval);
//    }
//
//    /**
//     * TODO: possibly could add Bloom filter to verify that user is in database
//     */
//    public Set<FollowedBy> getFollowers(String userName) throws Exception {
//        String userId = getUserId(userName);
//        return instagramApi.getFollowers(userId);
//    }
//
//    /**
//     * TODO: think about retards of all time functionality
//     */
//    public Set<FollowedBy> getRetards(String userName) throws Exception {
//        String userId = getUserId(userName);
//        return followerDiffRepository.getLastDiff(userId).getRemoved();
//    }
//
//    public Set<FollowedBy> getNewFollowers(String userName) throws Exception {
//        // TODO: add check if user is in database | Bloom filter
//        String userId = getUserId(userName);
//        return followerDiffRepository.getLastDiff(userId).getAdded();
//    }
//
//    private String getUserId(String userName) {
//        try {
//            return instagramApi.getUserId(userName);
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to fetch user id from the instagram service", e);
//        }
//    }
//
//}
