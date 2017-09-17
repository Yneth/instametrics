package ua.abond.instaret.repository.mongo;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.Set;

import ua.abond.instaret.entity.MongoFollowerDiff;
import ua.abond.instaret.model.FollowerDiff;
import ua.abond.instaret.repository.FollowerDiffRepository;
import ua.abond.instaret.util.LocalDateTimeInterval;

@Profile("mongo")
public interface MongoFollowerDiffRepository extends
        MongoRepository<MongoFollowerDiff, String>, FollowerDiffRepository {

    FollowerDiff getTopByUserIdOrderByCreationDateDesc(String userId);

    Set<FollowerDiff> getByUserIdAndCreationDateBetween(String userId, LocalDateTime from, LocalDateTime to);

    @Override
    default void pushDiff(FollowerDiff diff) {
        FollowerDiff toSave = diff;
        if (!(toSave instanceof MongoFollowerDiff)) {
            toSave = new MongoFollowerDiff(diff.getUserId(), diff.getCreationDate(),
                    diff.getAdded(), diff.getRemoved());
        }
        this.save((MongoFollowerDiff) toSave);
    }

    @Override
    default FollowerDiff getLastDiff(String userId) {
        return getTopByUserIdOrderByCreationDateDesc(userId);
    }

    @Override
    default Set<FollowerDiff> getDiffBetweenDates(String userId, LocalDateTimeInterval interval) {
        return getByUserIdAndCreationDateBetween(userId, interval.getFrom(), interval.getTo());
    }

}
