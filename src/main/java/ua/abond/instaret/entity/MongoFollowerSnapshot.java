package ua.abond.instaret.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ua.abond.instaret.dto.FollowedBy;
import ua.abond.instaret.model.FollowerDiff;
import ua.abond.instaret.model.FollowerSnapshot;

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

        return new ImmutableFollowerDiff(userId, LocalDateTime.now(), newFollowers, retards);
    }

    private static <T> Predicate<T> notContains(Set<T> set) {
        return ((Predicate<T>) set::contains).negate();
    }

}
