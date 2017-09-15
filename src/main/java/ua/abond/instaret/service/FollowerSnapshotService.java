package ua.abond.instaret.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.abond.instaret.entity.FollowerSnapshot;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

interface IOAction {

    void run() throws IOException;
}

interface IOSupplier<A> {

    A get() throws IOException;
}

@Service
public class FollowerSnapshotService {

    private final Path snapshotRoot;

    public FollowerSnapshotService(
        @Value("follower.snapshot.root")
            String path) {
        this.snapshotRoot = Paths.get(path);
        if (!snapshotRoot.toFile().isDirectory()) {
            throw new RuntimeException("FollowerSnapshotService initialization error");
        }
    }

    public void dropPreviousSnapshot(FollowerSnapshot head) {
        Path userSnapshotPath = getUserSnapshotPath(head.getUserId());
        if (!userSnapshotPath.toFile().delete()) {
            throw new RuntimeException("Failed to delete snapshot: " + userSnapshotPath.toString());
        }
    }

    public void saveSnapshot(FollowerSnapshot snapshot) {
        Path userSnapshotPath = getUserSnapshotPath(snapshot.getUserId());
        doIO(() -> {
            FileChannel channel = FileChannel.open(userSnapshotPath, StandardOpenOption.CREATE_NEW);

            FileLock lock = channel.tryLock();
            try {
                MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, 8196);
                channel.write(map);
            } finally {
                lock.release();
            }
        });
    }

    public FollowerSnapshot getCurrentSnapshot(String userName) {
        return null;
    }

    private Path getUserSnapshotPath(String userId) {
        String separator = System.lineSeparator();
        return Paths.get(snapshotRoot.toString() + separator + userId + separator + "snapshot");
    }

    private void doIO(IOAction ioAction) {
        runIO(() -> {
            ioAction.run();
            return null;
        });
    }

    private <A> A runIO(IOSupplier<A> ioSupplier) {
        try {
            return ioSupplier.get();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

}
