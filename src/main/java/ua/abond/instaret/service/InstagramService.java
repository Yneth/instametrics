package ua.abond.instaret.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import ua.abond.instaret.entity.FollowedBy;

@RequiredArgsConstructor
public class InstagramService {

    private static final String INSTAGRAM_URL = "https://www.instagram.com";

    public FollowedBy getFollowers() throws Exception {

        Connection.Response initialPage = Jsoup.connect(INSTAGRAM_URL)
                .method(Connection.Method.GET)
                .execute();

        String cookie = initialPage.cookies().entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("; "));
        Connection.Response execute = Jsoup.connect(INSTAGRAM_URL + "/accounts/login/ajax/")
                .data("username", "usrnm")
                .data("password", "pwd")
                .header("accept", "*/*")
                .header("origin", "https://www.instagram.com/")
                .header("referer", "https://www.instagram.com/")
                .header("cookie", cookie)
                .header("x-instagram-ajax", "1")
                .header("x-requested-with", "XMLHttpRequest")
                .header("x-csrftoken", initialPage.cookies().get("csrftoken"))
                .header("content-type", "application/x-www-form-urlencoded")
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36")
                .header("content-length", "40")
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .followRedirects(false)
                .method(Connection.Method.POST)
                .execute();
        Map<String, String> loginCookies = execute.cookies();


        Document document = Jsoup.connect(INSTAGRAM_URL + "/username")
                .cookies(loginCookies)
                .get();

        // https://gist.github.com/tomkdickinson/a093d30523dd77ae970f3ffcf26e1344
        return new FollowedBy();
    }

}
