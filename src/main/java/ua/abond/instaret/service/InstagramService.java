package ua.abond.instaret.service;

import static io.vavr.API.unchecked;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import ua.abond.instaret.entity.FollowedBy;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
public class InstagramService {

    private static final String LOGIN = "usrnm";
    private static final String PASSWORD = "pwd";
    private static final String INSTAGRAM_URL = "https://www.instagram.com/";

    private final ObjectMapper mapper = new ObjectMapper();

    public Authentication login(String login, String password) throws Exception {
        Connection.Response initialPage = Jsoup.connect(INSTAGRAM_URL)
            .method(Connection.Method.GET)
            .execute();
        System.out.println(initialPage.body());

        Connection.Response loginResponse = Jsoup.connect(INSTAGRAM_URL + "accounts/login/ajax/")
            .data("username", login)
            .data("password", password)
            .header("origin", "https://www.instagram.com/")
            .header("referer", "https://www.instagram.com/")
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
        return new Authentication(loginResponse.cookies());
    }

    // https://www.instagram.com/graphql/query/?query_id=17851374694183129&variables=%7B%22id%22%3A%221519061797%22%2C%22first%22%3A20%7D
    public List<FollowedBy> getFollowers() throws Exception {
        Authentication authentication = login(LOGIN, PASSWORD);

        int count = 30;
        Variables queryVariables = getQueryVariables(count, authentication);

        Map<String, String> params = new HashMap<>();
        params.put("variables", mapper.writeValueAsString(queryVariables));
        params.put("query_id", "17851374694183129");

        String url = String.format("%sgraphql/query/?%s", INSTAGRAM_URL, paramsToString(params));
        Connection.Response followersResponse =
            Jsoup.connect(url)
                .header("x-requested-with", "XMLHttpRequest")
                .header("accept-encoding", "gzip, deflate, br")
                .cookies(authentication.getCookies())
                .ignoreContentType(true)
                .execute();

        JsonNode edgeFollowedBy = mapper.readTree(followersResponse.body())
            .get("data")
            .get("user")
            .get("edge_followed_by");

        JsonNode pageInfoNode = edgeFollowedBy.get("page_info");
        JsonNode followerNodes = edgeFollowedBy.get("edges");
        Stream<JsonNode> nodes = StreamSupport.stream(followerNodes.spliterator(), false);
        while (pageInfoNode.get("has_next_page").asBoolean(false)) {
            JsonNode endCursorNode = pageInfoNode.get("end_cursor");
            queryVariables.next(endCursorNode.toString());

            followersResponse =
                Jsoup.connect(url)
                    .header("x-requested-with", "XMLHttpRequest")
                    .header("accept-encoding", "gzip, deflate, br")
                    .cookies(authentication.getCookies())
                    .ignoreContentType(true)
                    .execute();

            edgeFollowedBy = mapper.readTree(followersResponse.body())
                .get("data")
                .get("user")
                .get("edge_followed_by");

            pageInfoNode = edgeFollowedBy.get("page_info");

            followerNodes = edgeFollowedBy.get("edges");
            nodes = Stream.concat(nodes, StreamSupport.stream(followerNodes.spliterator(), false));
        }

        return nodes.map(node -> node.get("node"))
            .map(JsonNode::toString)
            .map(node -> unchecked(() -> mapper.readValue(node, FollowedBy.class)).get())
            .collect(Collectors.toList());
    }

    private String paramsToString(Map<String, String> params) {
        return params.entrySet().stream()
            .map(e -> unchecked(() -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), "UTF-8")).get())
            .collect(Collectors.joining("&"));
    }

    private Variables getQueryVariables(int count, Authentication authentication) throws IOException {
        Variables variables = new Variables();
        variables.setFirst(count);
        variables.setUserId(getUserId(authentication));
        return variables;
    }

    private String getUserId(Authentication authentication) throws IOException {
        Connection.Response response = Jsoup.connect("https://www.instagram.com/usrnm/?__a=1")
            .ignoreContentType(true)
            .cookies(authentication.getCookies())
            .execute();

        JsonNode jsonNode = mapper.readTree(response.body());
        return jsonNode.get("user").get("id").asText();
    }

    @Getter
    @Setter
    private static final class Variables {

        @JsonProperty("id")
        private String userId;

        @JsonProperty("first")
        private int first;

        @JsonProperty("after")
        private String after;

        public Variables next(String after) {
            Variables variables = new Variables();
            variables.setUserId(this.userId);
            variables.setFirst(this.first);
            variables.setAfter(after);
            return variables;
        }

    }

}
