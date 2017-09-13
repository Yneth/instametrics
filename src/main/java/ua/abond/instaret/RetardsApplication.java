package ua.abond.instaret;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import ua.abond.instaret.facade.PreAuthorizedInstagramService;
import ua.abond.instaret.repository.FollowerRepository;
import ua.abond.instaret.service.FollowerDiffService;
import ua.abond.instaret.service.FollowerSnapshotService;
import ua.abond.instaret.service.InstagramPreAuthorizationProperties;
import ua.abond.instaret.service.InstagramService;

@SpringBootApplication
@EnableConfigurationProperties({InstagramPreAuthorizationProperties.class})
public class RetardsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetardsApplication.class);
    }

    @Bean
    public PreAuthorizedInstagramService preAuthorizedInstagramService(
            InstagramService instagramService, FollowerRepository followerRepository,
            FollowerSnapshotService followerSnapshotService, FollowerDiffService followerDiffService,
            InstagramPreAuthorizationProperties properties) {

        return PreAuthorizedInstagramService.create(instagramService, followerRepository,
                followerDiffService, followerSnapshotService, properties);
    }

}
