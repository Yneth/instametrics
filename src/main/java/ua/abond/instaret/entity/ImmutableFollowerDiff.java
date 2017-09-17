package ua.abond.instaret.entity;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.abond.instaret.dto.FollowedBy;
import ua.abond.instaret.model.FollowerDiff;

@Getter
@RequiredArgsConstructor
public class ImmutableFollowerDiff implements FollowerDiff {

    private final String userId;
    private final LocalDateTime creationDate;
    private final Set<FollowedBy> added;
    private final Set<FollowedBy> removed;

}
