package ua.abond.metrics.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ua.abond.metrics.service.dto.FollowedBy;

@Getter
@Document
@AllArgsConstructor
public class MongoFollowerSnapshot implements FollowerSnapshot {

    @Id
    private String userId;
    private LocalDateTime creationDate;
    private Set<FollowedBy> followers;

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
