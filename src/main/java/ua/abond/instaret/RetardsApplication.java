package ua.abond.instaret;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import ua.abond.instaret.repository.FollowerRepository;
import ua.abond.instaret.service.InstagramPreAuthorizationProperties;
import ua.abond.instaret.service.InstagramService;
import ua.abond.instaret.service.PreAuthorizedInstagramService;

@SpringBootApplication
@EnableConfigurationProperties({InstagramPreAuthorizationProperties.class})
public class RetardsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetardsApplication.class);
    }

    @Bean
    public PreAuthorizedInstagramService preAuthorizedInstagramService(
            InstagramService instagramService, FollowerRepository followerRepository,
            InstagramPreAuthorizationProperties properties) {

        return PreAuthorizedInstagramService.create(instagramService, followerRepository, properties);
    }

}
