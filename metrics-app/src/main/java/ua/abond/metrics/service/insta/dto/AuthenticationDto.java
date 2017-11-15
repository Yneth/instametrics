package ua.abond.metrics.service.insta.dto;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import lombok.Getter;

@Getter
public class AuthenticationDto {

    private final String login;
    private final String userId;
    private final Map<String, String> cookies;

    public AuthenticationDto(String login, String userId, Map<String, String> cookies) {
        this.login = Objects.requireNonNull(login);
        this.userId = Objects.requireNonNull(userId);
        this.cookies = Collections.unmodifiableMap(Objects.requireNonNull(cookies));
    }

}
