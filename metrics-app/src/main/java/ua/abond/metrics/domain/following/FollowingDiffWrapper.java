package ua.abond.metrics.domain.following;

import ua.abond.metrics.domain.Diff;
import ua.abond.metrics.domain.user.User;

public class FollowingDiffWrapper extends Diff<User> {

    public FollowingDiffWrapper(Diff<User> diff) {
        super(diff.getHash(), diff.getOwnerId(), diff.getCreationDate(),
              diff.getAdded(), diff.getRemoved());
    }

}
