package ua.abond.metrics.repository.follower;

import java.util.Set;

import ua.abond.metrics.domain.follower.FollowerDiff;
import ua.abond.metrics.util.date.LocalDateTimeInterval;

public interface FollowerDiffRepository {

    FollowerDiff getLastDiff(String userId);

    void pushDiff(FollowerDiff followerDiff);

    Set<FollowerDiff> getDiffBetweenDates(String userId, LocalDateTimeInterval interval);

}
