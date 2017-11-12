package ua.abond.metrics.domain;

import java.time.LocalDateTime;
import java.util.Set;

import ua.abond.metrics.service.dto.FollowedBy;

public interface FollowerSnapshot {

    String getUserId();

    LocalDateTime getCreationDate();

    Set<FollowedBy> getFollowers();

    FollowerDiff diff(FollowerSnapshot that);

}
