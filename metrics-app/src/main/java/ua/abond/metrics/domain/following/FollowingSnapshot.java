package ua.abond.metrics.domain.following;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import ua.abond.metrics.domain.Diff;
import ua.abond.metrics.domain.Snapshot;
import ua.abond.metrics.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class FollowingSnapshot implements Snapshot<FollowingSnapshot, User> {

    @Id
    private String ownerId;
    private LocalDateTime creationDate;
    private List<User> sources;

    @Override
    public Diff<User> diff(Snapshot<FollowingSnapshot, User> that) {
        return new FollowingDiffWrapper(Snapshot.super.diff(that));
    }

}
