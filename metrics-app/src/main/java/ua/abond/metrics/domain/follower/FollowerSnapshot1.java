package ua.abond.metrics.domain.follower;

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
public class FollowerSnapshot1 implements Snapshot<FollowerSnapshot1, User> {

    @Id
    private String ownerId;
    private LocalDateTime creationDate;
    private List<User> sources;

    @Override
    public Diff<User> diff(Snapshot<FollowerSnapshot1, User> that) {
        return new FollowerDiffWrapper(Snapshot.super.diff(that));
    }

}
