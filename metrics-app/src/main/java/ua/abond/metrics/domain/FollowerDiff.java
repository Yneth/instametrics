package ua.abond.metrics.domain;

import java.time.LocalDateTime;
import java.util.Set;

import ua.abond.metrics.service.dto.FollowedBy;

public interface FollowerDiff {

    String getUserId();

    LocalDateTime getCreationDate();

    Set<FollowedBy> getAdded();

    Set<FollowedBy> getRemoved();

    String getHash();

    default boolean isEmpty() {
        return getAdded().isEmpty() && getRemoved().isEmpty();
    }

}
