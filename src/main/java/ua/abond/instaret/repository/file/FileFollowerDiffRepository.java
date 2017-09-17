package ua.abond.instaret.repository.file;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Set;

import ua.abond.instaret.model.FollowerDiff;
import ua.abond.instaret.repository.FollowerDiffRepository;
import ua.abond.instaret.util.LocalDateTimeInterval;

@Repository
@Profile("file")
public class FileFollowerDiffRepository implements FollowerDiffRepository {

    @Override
    public Set<FollowerDiff> getDiffBetweenDates(String userName, LocalDateTimeInterval interval) {
        return null;
    }

    @Override
    public FollowerDiff getLastDiff(String userName) {
        return null;
    }

    // push diff to stack
    @Override
    public void pushDiff(FollowerDiff diff) {
    }


}
