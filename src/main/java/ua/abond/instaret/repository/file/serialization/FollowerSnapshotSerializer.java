package ua.abond.instaret.repository.file.serialization;

import ua.abond.instaret.model.FollowerSnapshot;

public interface FollowerSnapshotSerializer {

    byte[] serialize(FollowerSnapshot snapshot);

    FollowerSnapshot deserialize(byte[] bytes);

}
