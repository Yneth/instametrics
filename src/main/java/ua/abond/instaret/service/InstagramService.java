package ua.abond.instaret.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ua.abond.instaret.dto.FollowedBy;

import static io.vavr.API.unchecked;

@Service
@RequiredArgsConstructor
public class InstagramService {

    private static final String INSTAGRAM_URL = "https://www.instagram.com/";

    private final ObjectMapper mapper;

    public Authentication login(String login, String password) throws Exception {
        Connection.Response initialPage = Jsoup.connect(INSTAGRAM_URL).execute();

        Connection.Response loginResponse = Jsoup.connect(INSTAGRAM_URL + "accounts/login/ajax/")
                .data("username", login)
                .data("password", password)
                .header("origin", INSTAGRAM_URL)
                .header("referer", INSTAGRAM_URL)
                .header("x-instagram-ajax", "1")
                .header("x-requested-with", "XMLHttpRequest")
                .header("x-csrftoken", initialPage.cookies().get("csrftoken"))
                .header("content-type", "application/x-www-form-urlencoded")
                .header("content-length", "40")
                .cookies(initialPage.cookies())
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .followRedirects(false)
                .method(Connection.Method.POST)
                .execute();

        Map<String, String> cookies = loginResponse.cookies();
        String userId = getUserId(cookies, login);
        return new Authentication(login, userId, cookies);
    }

    public List<FollowedBy> getFollowers(Authentication auth, String userName, int batchSize) throws Exception {
        Variables queryVariables = new Variables(getUserId(auth, userName), batchSize);

        String queryId = getFollowedByQueryId(auth)
                .orElseThrow(() -> new RuntimeException("could not find queryId"));

        Map<String, String> params = new HashMap<>();
        params.put("query_id", queryId);

        JsonNode pageInfoNode = null;
        Stream<JsonNode> nodes = Stream.empty();
        do {
            if (pageInfoNode != null) {
                JsonNode endCursorNode = pageInfoNode.get("end_cursor");
                queryVariables = queryVariables.next(endCursorNode.asText(null));
            }
            params.put("variables", mapper.writeValueAsString(queryVariables));

            String url = String.format("%sgraphql/query/?%s", INSTAGRAM_URL, paramsToString(params));

            Connection.Response followersResponse =
                    Jsoup.connect(url)
                            .header("x-requested-with", "XMLHttpRequest")
                            .header("accept-encoding", "gzip, deflate, br")
                            .cookies(auth.getCookies())
                            .ignoreContentType(true)
                            .execute();

            JsonNode edgeFollowedBy = mapper.readTree(followersResponse.body())
                    .get("data")
                    .get("user")
                    .get("edge_followed_by");

            pageInfoNode = edgeFollowedBy.get("page_info");
            JsonNode followerNodes = edgeFollowedBy.get("edges");
            nodes = Stream.concat(nodes, StreamSupport.stream(followerNodes.spliterator(), false));
        } while (pageInfoNode.get("has_next_page").asBoolean(false));

        return nodes.map(node -> node.get("node"))
                .map(JsonNode::toString)
                .map(node -> unchecked(() -> mapper.readValue(node, FollowedBy.class)).get())
                .collect(Collectors.toList());
    }

    public List<FollowedBy> getFollowers(Authentication auth, String userName) throws Exception {
        return getFollowers(auth, userName, 100);
    }

    public Optional<String> getFollowedByQueryId(Authentication authentication) throws IOException {
        Document originalPage = Jsoup.connect(INSTAGRAM_URL)
                .cookies(authentication.getCookies())
                .ignoreContentType(true)
                .get();

        String scriptUrl = INSTAGRAM_URL +
                (originalPage.body()
                        .getElementsByAttributeValueContaining("src", "en_US_Commons")
                        .first()
                        .attr("src")
                        // why so?
                        .substring(1));
        // get script file
        String input = Jsoup.connect(scriptUrl).execute().body();

        // and retrieve followedBy queryId from this file
        Pattern varNamePattern = Pattern.compile("\\w{1,2}=(?<var>.{1,2}),\\w{1,2}=\"edge_followed_by\"");
        Matcher matcher = varNamePattern.matcher(input);
        if (matcher.find()) {
            String varName = matcher.group("var");

            Matcher varValueMatcher = Pattern.compile(varName + "=\"(?<id>\\d{17})\"").matcher(input);
            if (varValueMatcher.find()) {
                return Optional.of(varValueMatcher.group("id"));
            }
        }
        return Optional.empty();
    }

    public String getUserId(Authentication authentication, String userName) throws IOException {
        return getUserId(authentication.getCookies(), userName);
    }

    private String getUserId(Map<String, String> cookies, String userName) throws IOException {
        Connection.Response response = Jsoup.connect(String.format("%s%s/?__a=1", INSTAGRAM_URL, userName))
                .ignoreContentType(true)
                .cookies(cookies)
                .execute();

        JsonNode jsonNode = mapper.readTree(response.body());
        return jsonNode.get("user").get("id").asText();
    }

    private String paramsToString(Map<String, String> params) {
        return params.entrySet().stream()
                .map(e -> unchecked(() -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), "UTF-8")).get())
                .collect(Collectors.joining("&"));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static final class Variables {

        @JsonProperty("id")
        private String userId;

        @JsonProperty("batchSize")
        private int batchSize;

        @JsonProperty("after")
        private String after;

        public Variables(String userId, int count) {
            this.userId = userId;
            this.batchSize = count;
        }

        public Variables next(String after) {
            Variables variables = new Variables();
            variables.setUserId(this.userId);
            variables.setBatchSize(this.batchSize);
            variables.setAfter(after);
            return variables;
        }

    }

}
