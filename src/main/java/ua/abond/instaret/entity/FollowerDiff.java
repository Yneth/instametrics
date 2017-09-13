package ua.abond.instaret.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.abond.instaret.dto.FollowedBy;

@Getter
@RequiredArgsConstructor
public class FollowerDiff {

    private final String userId;
    private final LocalDateTime creationDate;
    private final Set<FollowedBy> added;
    private final Set<FollowedBy> removed;

}
