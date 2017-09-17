package ua.abond.instaret.repository.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import sun.nio.ch.DirectBuffer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import ua.abond.instaret.model.FollowerSnapshot;
import ua.abond.instaret.repository.FollowerSnapshotRepository;
import ua.abond.instaret.repository.file.serialization.FollowerSnapshotSerializer;
import ua.abond.instaret.util.io.IOAction;
import ua.abond.instaret.util.io.IOSupplier;

@Repository
@Profile("file")
public class FileFollowerSnapshotRepository implements FollowerSnapshotRepository {

    private final Path snapshotRoot;
    private FollowerSnapshotSerializer followerSnapshotSerializer;

    public FileFollowerSnapshotRepository(
            @Value("follower.snapshot.root")
                    String path) {
        this.snapshotRoot = Paths.get(path);
        if (this.snapshotRoot.toFile().exists() && !snapshotRoot.toFile().isDirectory()) {
            throw new RuntimeException("FollowerSnapshotService initialization error");
        }
    }

    public FollowerSnapshot replaceSnapshot(FollowerSnapshot newSnapshot) {
        Path userSnapshotPath = getUserSnapshotPath(newSnapshot.getUserId());
        if (!userSnapshotPath.toFile().delete()) {
            throw new RuntimeException("Failed to delete snapshot: " + userSnapshotPath.toString());
        }
        saveSnapshot(newSnapshot);
        return newSnapshot;
    }

    private void saveSnapshot(FollowerSnapshot snapshot) {
        Path userSnapshotPath = getUserSnapshotPath(snapshot.getUserId());
        doIO(() -> {
            FileChannel channel = FileChannel.open(userSnapshotPath, StandardOpenOption.CREATE_NEW);

            FileLock lock = channel.tryLock();
            try {
                byte[] bytes = followerSnapshotSerializer.serialize(snapshot);
                channel.write(ByteBuffer.allocateDirect(bytes.length).put(bytes));
            } finally {
                channel.close();
                lock.release();
            }
        });
    }

    public Optional<FollowerSnapshot> getSnapshot(String userId) {
        Path userSnapshotPath = getUserSnapshotPath(userId);
        doIO(() -> {
            File file = userSnapshotPath.toFile();
            FileChannel channel = FileChannel.open(userSnapshotPath, StandardOpenOption.READ);

            FileLock lock = channel.tryLock();
            ByteBuffer buffer = null;
            try {
                // TODO: beware if size of the file would be longer than 2^31
                buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
                Charset.forName("UTF-8").decode(buffer);
            } finally {
                if (buffer != null && buffer instanceof DirectBuffer) {
                    ((DirectBuffer) buffer).cleaner().clean();
                }
                channel.close();
                lock.release();
            }
        });
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
