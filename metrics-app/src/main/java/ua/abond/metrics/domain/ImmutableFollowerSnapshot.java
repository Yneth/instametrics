package ua.abond.metrics.domain;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.abond.metrics.service.dto.FollowedBy;

@Getter
@RequiredArgsConstructor
public class ImmutableFollowerSnapshot implements FollowerSnapshot {

    private final String userId;
    private final LocalDateTime creationDate;
    private final Set<FollowedBy> followers;

    public FollowerDiff diff(FollowerSnapshot that) {
        Set<FollowedBy> newFollowers = this.getFollowers().stream()
                .filter(notContains(that.getFollowers()))
                .collect(Collectors.toSet());

        Set<FollowedBy> retards = that.getFollowers().stream()
                .filter(notContains(this.getFollowers()))
                .collect(Collectors.toSet());

        return new ImmutableFollowerDiff(UUID.randomUUID().toString(), userId,
                LocalDateTime.now(), newFollowers, retards);
    }

    private static <T> Predicate<T> notContains(Set<T> set) {
        return ((Predicate<T>) set::contains).negate();
    }

}
