package ua.abond.instaret.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("instagram.pre.auth")
public class InstagramPreAuthorizationProperties {

    private String username;

    private String password;

}
