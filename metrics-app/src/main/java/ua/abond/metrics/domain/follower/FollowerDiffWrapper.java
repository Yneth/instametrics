package ua.abond.metrics.domain.follower;

import ua.abond.metrics.domain.Diff;
import ua.abond.metrics.domain.user.User;

public class FollowerDiffWrapper extends Diff<User> {

    public FollowerDiffWrapper(Diff<User> diff) {
        super(diff.getHash(), diff.getOwnerId(), diff.getCreationDate(),
              diff.getAdded(), diff.getRemoved());
    }

}
