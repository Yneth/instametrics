package ua.abond.instaret.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import lombok.RequiredArgsConstructor;
import ua.abond.instaret.dto.FollowedBy;
import ua.abond.instaret.facade.InstagramFollowerFacade;

@RestController
@RequiredArgsConstructor
public class RetardController {

    private InstagramFollowerFacade instagramFollowerFacade;

    @GetMapping("/followers/")
    public List<FollowedBy> getFollowers(@RequestParam("userName") String userName)
            throws Exception {
        return instagramFollowerFacade.getFollowers(userName);
    }

    @GetMapping("/followers/new/")
    public List<FollowedBy> getNewFollowers(@RequestParam("userName") String userName)
            throws Exception {
        return instagramFollowerFacade.getNewFollowers(userName);
    }

    @GetMapping("/followers/save/")
    public String saveFollowers(@RequestParam("userName") String userName)
            throws Exception {
        instagramFollowerFacade.snapshotFollowers(userName);
        return "success";
    }

    @GetMapping("/retards/")
    public List<FollowedBy> getRetards(@RequestParam("userName") String userName)
            throws Exception {
        return instagramFollowerFacade.getRetards(userName);
    }

}
