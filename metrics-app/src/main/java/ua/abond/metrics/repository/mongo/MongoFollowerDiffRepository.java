package ua.abond.metrics.repository.mongo;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import ua.abond.metrics.domain.MongoFollowerDiff;
import ua.abond.instaret.model.FollowerDiff;
import ua.abond.metrics.repository.FollowerDiffRepository;
import ua.abond.instaret.util.LocalDateTimeInterval;

@Profile("mongo")
public interface MongoFollowerDiffRepository extends
        MongoRepository<MongoFollowerDiff, String>, FollowerDiffRepository {

    List<FollowerDiff> findFirstByUserIdOrderByCreationDateDesc(String userId);

    Set<FollowerDiff> getByUserIdAndCreationDateBetween(String userId, LocalDateTime from, LocalDateTime to);

    @Override
    default void pushDiff(FollowerDiff diff) {
        FollowerDiff toSave = diff;
        if (!(toSave instanceof MongoFollowerDiff)) {
            toSave = new MongoFollowerDiff(diff.getHash(), diff.getUserId(), diff.getCreationDate(),
                    diff.getAdded(), diff.getRemoved());
        }
        this.save((MongoFollowerDiff) toSave);
    }

    @Override
    default FollowerDiff getLastDiff(String userId) {
        List<FollowerDiff> results = findFirstByUserIdOrderByCreationDateDesc(userId);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    @Override
    default Set<FollowerDiff> getDiffBetweenDates(String userId, LocalDateTimeInterval interval) {
        return getByUserIdAndCreationDateBetween(userId, interval.getFrom(), interval.getTo());
    }

}
