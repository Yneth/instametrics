package ua.abond.instaret;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import ua.abond.instaret.controller.FollowedBy;
import ua.abond.instaret.controller.InstagramService;

import javax.jws.WebParam;

@CrossOrigin(value = "*")
@RestController
@SpringBootApplication
public class RetardsApplication {

    private static final String CLIENT_ID = "";

    @Autowired
    private InstagramService instagramService;

    public static void main(String[] args) {
        SpringApplication.run(RetardsApplication.class);
    }

    @Bean
    public static RestTemplate instagramRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public static InstagramService instagramService() {
        return new InstagramService(instagramRestTemplate());
    }

    @CrossOrigin(value = "*")
    @GetMapping("/redirect")
    public ModelAndView redirect() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index.html");
        return modelAndView;
    }

    @GetMapping("/followers")
    public FollowedBy getFollowers(@RequestParam("access_token") String accessToken) {
        return instagramService.getFollowers(accessToken);
    }

}
