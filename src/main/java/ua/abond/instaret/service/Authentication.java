package ua.abond.instaret.service;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
public class Authentication {

    private final Map<String, String> cookies;

    public Authentication(Map<String, String> cookies) {
        this.cookies = Collections.unmodifiableMap(cookies);
    }

}
