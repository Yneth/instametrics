package ua.abond.instaret.entity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.abond.instaret.dto.FollowedBy;
import ua.abond.instaret.model.FollowerDiff;
import ua.abond.instaret.model.FollowerSnapshot;

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

        return new ImmutableFollowerDiff(userId, LocalDateTime.now(), newFollowers, retards);
    }

    private static <T> Predicate<T> notContains(Set<T> set) {
        return ((Predicate<T>) set::contains).negate();
    }

}
