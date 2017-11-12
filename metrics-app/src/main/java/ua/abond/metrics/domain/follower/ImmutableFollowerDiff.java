package ua.abond.metrics.domain.follower;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.abond.metrics.service.dto.FollowedBy;

@Getter
@RequiredArgsConstructor
public class ImmutableFollowerDiff implements FollowerDiff {

    private final String hash;
    private final String userId;
    private final LocalDateTime creationDate;
    private final Set<FollowedBy> added;
    private final Set<FollowedBy> removed;

}
