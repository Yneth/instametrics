package ua.abond.metrics.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import lombok.RequiredArgsConstructor;
import ua.abond.metrics.domain.FollowerDiff;
import ua.abond.metrics.domain.FollowerSnapshot;
import ua.abond.metrics.service.dto.FollowedBy;
import ua.abond.metrics.facade.InstagramFollowerFacade;
import ua.abond.metrics.service.dto.FollowingDto;

@RestController
@RequiredArgsConstructor
public class RetardController {

    private final InstagramFollowerFacade instagramFollowerFacade;

    @GetMapping("/followers/")
    public Set<FollowedBy> getFollowers(@RequestParam("userName") String userName)
            throws Exception {
        return instagramFollowerFacade.getFollowers(userName);
    }

    @GetMapping("/followers/new/")
    public Set<FollowedBy> getNewFollowers(@RequestParam("userName") String userName) {
        return instagramFollowerFacade.getNewFollowers(userName);
    }

    @GetMapping("/followers/save/")
    public FollowerSnapshot saveFollowers(@RequestParam("userName") String userName)
            throws Exception {
        return instagramFollowerFacade.updateSnapshot(userName);
    }

    @GetMapping("/retards/")
    public Set<FollowedBy> getRetards(@RequestParam("userName") String userName) {
        return instagramFollowerFacade.getRetards(userName);
    }

    @GetMapping("/diffs/all/")
    public Set<FollowerDiff> getDiffs(@RequestParam("userName") String userName) {
        return instagramFollowerFacade.getAllDiffs(userName);
    }

    @GetMapping("/following/")
    public Set<FollowingDto> getFollowing(@RequestParam("userName") String userName) {
        return instagramFollowerFacade.getFollowing(userName);
    }

}
