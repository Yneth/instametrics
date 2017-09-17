package ua.abond.instaret;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import ua.abond.instaret.service.InstagramAPIService;
import ua.abond.instaret.service.InstagramPreAuthorizationProperties;
import ua.abond.instaret.service.PreAuthorizedInstagramAPIService;

@SpringBootApplication
@EnableConfigurationProperties({InstagramPreAuthorizationProperties.class})
public class RetardsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetardsApplication.class);
    }

    @Bean
    public PreAuthorizedInstagramAPIService preAuthorizedInstagramService(
        InstagramAPIService instagramService, InstagramPreAuthorizationProperties properties) {

        return PreAuthorizedInstagramAPIService.create(instagramService, properties);
    }

}
