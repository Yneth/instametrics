package ua.abond.metrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import ua.abond.metrics.service.insta.InstaApiService;
import ua.abond.metrics.service.insta.PreAuthInstaProperties;
import ua.abond.metrics.service.insta.PreAuthorizedInstaService;

@SpringBootApplication
@EnableConfigurationProperties({PreAuthInstaProperties.class})
public class RetardsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetardsApplication.class);
    }

    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }

    @Bean
    public PreAuthorizedInstaService preAuthorizedInstagramService(
        InstaApiService instagramService, PreAuthInstaProperties properties) {
        return PreAuthorizedInstaService.create(instagramService, properties);
    }

}
