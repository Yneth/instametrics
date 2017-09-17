package ua.abond.instaret.repository;

import java.util.Set;

import ua.abond.instaret.model.FollowerDiff;
import ua.abond.instaret.util.LocalDateTimeInterval;

public interface FollowerDiffRepository {

    FollowerDiff getLastDiff(String userId);

    void pushDiff(FollowerDiff followerDiff);

    Set<FollowerDiff> getDiffBetweenDates(String userId, LocalDateTimeInterval interval);

}
