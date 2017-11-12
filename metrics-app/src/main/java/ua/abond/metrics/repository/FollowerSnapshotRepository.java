package ua.abond.metrics.repository;

import java.util.Optional;

import ua.abond.metrics.domain.follower.FollowerSnapshot;


public interface FollowerSnapshotRepository {

    FollowerSnapshot replaceSnapshot(FollowerSnapshot followerSnapshot);

    Optional<FollowerSnapshot> getSnapshot(String userId);

}
