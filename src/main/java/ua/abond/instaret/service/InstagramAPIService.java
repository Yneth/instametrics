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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ua.abond.instaret.dto.FollowedBy;

import static io.vavr.API.unchecked;

/**
 * TODO: add logic to swap accounts on 429 error code
 * Also should add some logging of all queries
 * so we would have statistics for each account activity
 *
 *
 * Check if it is possible to reuse cursor for different accounts
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstagramAPIService {

    private static final String INSTAGRAM_URL = "https://www.instagram.com/";
    // TODO: it is better to move it to the prop and later calculate dynamically
    // create min and max props and via binary search fallback to min
    // 4500 as max 1000 as min would be OK
    private static final int BATCH_SIZE = 3500;

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

    public Set<FollowedBy> getFollowersByUserName(Authentication auth, String userName)
            throws Exception {
        String userId = getUserId(auth, userName);
        return getFollowersById(auth, userId);
    }

    public Set<FollowedBy> getFollowersById(Authentication auth, String userId, int batchSize) throws Exception {
        Variables queryVariables = new Variables(userId, batchSize);

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

            log.debug("Querying {}", url);
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
                .collect(Collectors.toSet());
    }

    public Set<FollowedBy> getFollowersById(Authentication auth, String userName) throws Exception {
        return getFollowersById(auth, userName, BATCH_SIZE);
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
        if (authentication.getLogin().equals(userName)) {
            return authentication.getUserId();
        }
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

        @JsonProperty("first")
        private int batchSize;

        @JsonProperty("after")
        private String cursor;

        public Variables(String userId, int count) {
            this.userId = userId;
            this.batchSize = count;
        }

        public Variables next(String after) {
            Variables variables = new Variables();
            variables.setUserId(this.userId);
            variables.setBatchSize(this.batchSize);
            variables.setCursor(after);
            return variables;
        }

    }

}
