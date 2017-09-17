package ua.abond.instaret.model;

import java.time.LocalDateTime;
import java.util.Set;

import ua.abond.instaret.dto.FollowedBy;

public interface FollowerSnapshot {

    String getUserId();

    LocalDateTime getCreationDate();

    Set<FollowedBy> getFollowers();

    FollowerDiff diff(FollowerSnapshot that);

}
