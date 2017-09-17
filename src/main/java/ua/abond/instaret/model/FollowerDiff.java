package ua.abond.instaret.model;

import java.time.LocalDateTime;
import java.util.Set;

import ua.abond.instaret.dto.FollowedBy;

public interface FollowerDiff {

    String getUserId();

    LocalDateTime getCreationDate();

    Set<FollowedBy> getAdded();

    Set<FollowedBy> getRemoved();

}
