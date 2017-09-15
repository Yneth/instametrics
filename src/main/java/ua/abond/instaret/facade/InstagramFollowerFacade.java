package ua.abond.instaret.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.abond.instaret.dto.FollowedBy;
import ua.abond.instaret.entity.FollowerDiff;
import ua.abond.instaret.entity.FollowerSnapshot;
import ua.abond.instaret.repository.FollowerRepository;
import ua.abond.instaret.service.FollowerDiffService;
import ua.abond.instaret.service.FollowerSnapshotService;
import ua.abond.instaret.service.PreAuthorizedInstagramAPIService;
import ua.abond.instaret.util.LocalDateTimeInterval;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InstagramFollowerFacade {

    private final PreAuthorizedInstagramAPIService instagramApi;

    private final FollowerRepository followerRepository;
    private final FollowerDiffService followerDiffService;
    private final FollowerSnapshotService followerSnapshotService;

    public FollowerSnapshot updateSnapshot(String userName) throws Exception {
        FollowerSnapshot head = followerSnapshotService.getCurrentSnapshot(userName);
        FollowerSnapshot current = null;// followerSnapshotService.loadNewSnapshot(userName);

        FollowerDiff diff = followerDiffService.getDiff(head, current);
        followerDiffService.pushDiff(diff);
        followerSnapshotService.dropPreviousSnapshot(head);

        return current;
    }

    public FollowerDiff getLastDiff(String userName) {
        return followerDiffService.getLastDiff(userName);
    }

    public Set<FollowerDiff> getDiffBetween(String userName, LocalDateTimeInterval interval) {
        return followerDiffService.getDiffBetweenDates(userName, interval);
    }

    public void snapshotFollowers(String userName) throws Exception {
        followerRepository.persist(userName, instagramApi.getFollowers(userName));
    }

    public List<FollowedBy> getFollowers(String userName) throws Exception {
        return instagramApi.getFollowers(userName);
    }

    public List<FollowedBy> getRetards(String userName) throws Exception {
        List<FollowedBy> currentFollowers = instagramApi.getFollowers(userName);
        List<FollowedBy> oldFollowers = followerRepository.getFollowers(userName);

        return subtractLeft(currentFollowers, oldFollowers);
    }

    public List<FollowedBy> getNewFollowers(String userName) throws Exception {
        List<FollowedBy> currentFollowers = instagramApi.getFollowers(userName);
        List<FollowedBy> oldFollowers = followerRepository.getFollowers(userName);

        return subtractLeft(currentFollowers, oldFollowers);
    }

    private <T> List<T> subtractLeft(List<T> left, List<T> right) {
        if (left == null) {
            return Collections.emptyList();
        }
        return left.stream().filter(t -> !right.contains(t))
            .collect(Collectors.toList());
    }

}
