package ua.abond.metrics.service.insta;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("instagram.pre.auth")
public class PreAuthInstaProperties {

    private String username;

    private String password;

}
