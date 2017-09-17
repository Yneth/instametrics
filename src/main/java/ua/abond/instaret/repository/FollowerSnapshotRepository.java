package ua.abond.instaret.repository;

import java.util.Optional;

import ua.abond.instaret.model.FollowerSnapshot;

public interface FollowerSnapshotRepository {

    FollowerSnapshot replaceSnapshot(FollowerSnapshot followerSnapshot);

    Optional<FollowerSnapshot> getSnapshot(String userId);

}
