package ua.abond.instaret.repository.mongo;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

import ua.abond.instaret.entity.MongoFollowerSnapshot;
import ua.abond.instaret.model.FollowerSnapshot;
import ua.abond.instaret.repository.FollowerSnapshotRepository;

@Profile("mongo")
public interface MongoFollowerSnapshotRepository extends
        MongoRepository<MongoFollowerSnapshot, String>, FollowerSnapshotRepository {

    FollowerSnapshot save(FollowerSnapshot snapshot);

    Optional<FollowerSnapshot> getByUserId(String userId);

    default FollowerSnapshot replaceSnapshot(FollowerSnapshot snapshot) {
        FollowerSnapshot toSave = snapshot;
        if (!(snapshot instanceof MongoFollowerSnapshot)) {
            toSave = new MongoFollowerSnapshot(snapshot.getUserId(), snapshot.getCreationDate(),
                    snapshot.getFollowers());
        }
        return save(toSave);
    }

    default Optional<FollowerSnapshot> getSnapshot(String userId) {
        return getByUserId(userId);
    }

}
