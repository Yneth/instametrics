package ua.abond.instaret.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import ua.abond.instaret.entity.FollowerDiff;
import ua.abond.instaret.entity.FollowerSnapshot;
import ua.abond.instaret.util.LocalDateTimeInterval;

@Service
public class FollowerDiffService {

    public Set<FollowerDiff> getDiffBetweenDates(String userName, LocalDateTimeInterval interval) {
        return null;
    }

    public FollowerDiff getLastDiff(String userName) {
        return null;
    }

    public FollowerDiff getDiff(FollowerSnapshot head, FollowerSnapshot current) {
        return null;
    }

    // push diff to stack
    public void pushDiff(FollowerDiff diff) {

    }

}
