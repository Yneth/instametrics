package ua.abond.metrics.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import lombok.RequiredArgsConstructor;
import ua.abond.metrics.domain.follower.FollowerDiff;
import ua.abond.metrics.domain.follower.FollowerSnapshot;
import ua.abond.metrics.service.insta.dto.FollowedBy;
import ua.abond.metrics.facade.follower.InstaFollowerFacade;
import ua.abond.metrics.service.insta.dto.FollowingDto;

@RestController
@RequiredArgsConstructor
public class RetardController {

    private final InstaFollowerFacade instaFollowerFacade;

    @GetMapping("/followers/")
    public Set<FollowedBy> getFollowers(@RequestParam("userName") String userName)
            throws Exception {
        return instaFollowerFacade.getFollowers(userName);
    }

    @GetMapping("/followers/new/")
    public Set<FollowedBy> getNewFollowers(@RequestParam("userName") String userName) {
        return instaFollowerFacade.getNewFollowers(userName);
    }

    @GetMapping("/followers/save/")
    public FollowerSnapshot saveFollowers(@RequestParam("userName") String userName)
            throws Exception {
        return instaFollowerFacade.updateSnapshot(userName);
    }

    @GetMapping("/retards/")
    public Set<FollowedBy> getRetards(@RequestParam("userName") String userName) {
        return instaFollowerFacade.getRetards(userName);
    }

    @GetMapping("/diffs/all/")
    public Set<FollowerDiff> getDiffs(@RequestParam("userName") String userName) {
        return instaFollowerFacade.getAllDiffs(userName);
    }

    @GetMapping("/following/")
    public Set<FollowingDto> getFollowing(@RequestParam("userName") String userName) {
        return instaFollowerFacade.getFollowing(userName);
    }

}
