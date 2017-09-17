package ua.abond.instaret.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import lombok.RequiredArgsConstructor;
import ua.abond.instaret.dto.FollowedBy;
import ua.abond.instaret.facade.InstagramFollowerFacade;
import ua.abond.instaret.model.FollowerSnapshot;

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
    public Set<FollowedBy> getNewFollowers(@RequestParam("userName") String userName)
            throws Exception {
        return instagramFollowerFacade.getNewFollowers(userName);
    }

    @GetMapping("/followers/save/")
    public FollowerSnapshot saveFollowers(@RequestParam("userName") String userName)
            throws Exception {
        return instagramFollowerFacade.updateSnapshot(userName);
    }

    @GetMapping("/retards/")
    public Set<FollowedBy> getRetards(@RequestParam("userName") String userName)
            throws Exception {
        return instagramFollowerFacade.getRetards(userName);
    }

}
