//package ua.abond.metrics.controller;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Set;
//
//import lombok.RequiredArgsConstructor;
//import ua.abond.metrics.service.insta.dto.FollowedBy;
//import ua.abond.instaret.model.FollowerSnapshot;
//import ua.abond.metrics.service.insta.ReactivePreAuthInstaApiService;
//
//@RestController
//@RequestMapping("/reactive/")
//@RequiredArgsConstructor
//public class ReactiveRetardController {
//
//    private ReactivePreAuthInstaApiService instagramAPIService;
//
//    @GetMapping("/followers/")
//    public Set<FollowedBy> getFollowers(@RequestParam("userName") String userName)
//            throws Exception {
//        return instagramFollowerFacade.getFollowers(userName);
//    }
//
//    @GetMapping("/followers/new/")
//    public Set<FollowedBy> getNewFollowers(@RequestParam("userName") String userName)
//            throws Exception {
//        return instagramFollowerFacade.getNewFollowers(userName);
//    }
//
//    @GetMapping("/followers/save/")
//    public FollowerSnapshot saveFollowers(@RequestParam("userName") String userName)
//            throws Exception {
//        return instagramFollowerFacade.updateSnapshot(userName);
//    }
//
//    @GetMapping("/retards/")
//    public Set<FollowedBy> getRetards(@RequestParam("userName") String userName)
//            throws Exception {
//        return instagramFollowerFacade.getRetards(userName);
//    }
//
//}
