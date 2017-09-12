package ua.abond.instaret.service;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
public class Authentication {

    private final String login;
    private final String userId;
    private final Map<String, String> cookies;

    public Authentication(String login, String userId, Map<String, String> cookies) {
        this.login = login;
        this.userId = userId;
        this.cookies = Collections.unmodifiableMap(cookies);
    }

}
