package ua.abond.instaret.service;

import org.springframework.stereotype.Service;

import ua.abond.instaret.entity.FollowerSnapshot;

@Service
public class FollowerSnapshotService {

    public void dropPreviousSnapshot(FollowerSnapshot head) {
    }

    public FollowerSnapshot loadNewSnapshot(String userName) {
        return null;
    }

    public FollowerSnapshot getCurrentSnapshot(String userName) {
        return null;
    }

}
