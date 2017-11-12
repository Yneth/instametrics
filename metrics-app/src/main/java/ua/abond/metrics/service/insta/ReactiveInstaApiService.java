package ua.abond.metrics.service.insta;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URLEncoder;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.vavr.API;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.abond.metrics.service.dto.FollowedBy;
import ua.abond.metrics.service.dto.AuthenticationDto;
import ua.abond.instaret.util.reactor.FluxOps;
import ua.abond.metrics.service.dto.QueryVariablesDto;

import static io.vavr.API.unchecked;

@Service
@RequiredArgsConstructor
public class ReactiveInstaApiService {

    private static final String INSTAGRAM_URL = "https://www.instagram.com/";

    private static final int BATCH_SIZE = 3500;

    private final ObjectMapper mapper;
    private final WebClient webClient;

    public Mono<AuthenticationDto> login(String login, String password) {
        MultiValueMap<String, String> loginForm = new LinkedMultiValueMap<>();
        loginForm.add("username", login);
        loginForm.add("password", password);

        Mono<ClientResponse> clientResponseMono = queryForCookies()
            .flatMap(cookies -> webClient.post().uri(INSTAGRAM_URL + "accounts/login/ajax/")
                .body(BodyInserters.fromFormData(loginForm))
                .cookies(requestCookies -> cookies.forEach(requestCookies::add))
                .header("origin", INSTAGRAM_URL)
                .header("referer", INSTAGRAM_URL)
                .header("x-instagram-ajax", "1")
                .header("x-requested-with", "XMLHttpRequest")
                .header("x-csrftoken", cookies.getOrElse("csrftoken", ""))
                .header("content-type", "application/x-www-form-urlencoded")
                .header("content-length", "40")
                .exchange()
            );
        return clientResponseMono
            .map(loginResponse -> mapResponseCookies(loginResponse.cookies()))
            .map(cookies -> new AuthenticationDto(login, cookies.get("ds_user_id").getOrElse((String) null), cookies.toJavaMap()));
    }

    public Flux<FollowedBy> getFollowersById(AuthenticationDto auth, String userId) {
        return getFollowersById(auth, userId, BATCH_SIZE);
    }

    public Flux<FollowedBy> getFollowersById(AuthenticationDto auth, String userId, int batchSize) {
        final JsonNodeFactory factory = JsonNodeFactory.instance;
        JsonNode pageInfoBody = factory.objectNode().
            setAll(HashMap.<String, JsonNode>of("end_cursor", factory.textNode(null))
                .put("has_next_page", factory.booleanNode(true)).toJavaMap());
        JsonNode initialNode = factory.objectNode().set("page_info", pageInfoBody);

        final QueryVariablesDto initialQueryVariables = new QueryVariablesDto(userId, batchSize);
        return getQueryIdForFollowedByScope(auth)
            .flatMapMany(queryId -> {
                final HashMap<String, String> initialParams = HashMap.of("query_id", queryId);
                return Mono.just(initialNode).expand(initialJsonNode -> {
                    JsonNode pageInfoNode = initialJsonNode.get("page_info");
                    if (!pageInfoNode.get("has_next_page").asBoolean(false)) {
                        return Mono.empty();
                    }
                    Mono<QueryVariablesDto> queryVariablesMono = Mono.just(initialJsonNode)
                        .map(jsonNode -> pageInfoNode.get("end_cursor"))
                        .map(endCursorNode -> endCursorNode.asText(""))
                        .map(initialQueryVariables::next);

                    Mono<URI> uriMono = queryVariablesMono
                        .map(vars -> initialParams.put("variables", unchecked(mapper::writeValueAsString).apply(vars)))
                        .map(queryParams -> String.format("%sgraphql/query/?%s", INSTAGRAM_URL, paramsToString(queryParams)))
                        .map(URI::create);

                    Mono<JsonNode> queryResponse = uriMono.flatMap(uri -> webClient.get().uri(uri)
                        .cookies(requestCookies -> auth.getCookies().forEach(requestCookies::add))
                        .header("x-requested-with", "XMLHttpRequest")
                        .header("accept-encoding", "gzip, deflate, br")
                        .exchange()
                        .flatMap(response -> response.bodyToMono(String.class))
                        .map(unchecked(mapper::readTree)));
                    return queryResponse
                        .map(responseTree -> responseTree.get("data"))
                        .map(data -> data.get("user"))
                        .map(user -> user.get("edge_followed_by"));
                });
            })
            .flatMap(edgeFollowedBy -> FluxOps.emptyOrIterable(edgeFollowedBy.get("edges")))
            .flatMap(edges -> Mono.justOrEmpty(edges.get("node")).flux())
            .map(JsonNode::toString)
            .map(nodeString -> unchecked(() -> mapper.readValue(nodeString, FollowedBy.class)).get());
    }

    public Mono<String> getQueryIdForFollowedByScope(AuthenticationDto authentication) {
        final Function<Document, String> findScriptUrl = page -> page.body()
            .getElementsByAttributeValueContaining("src", "en_US_Commons")
            .first()
            .attr("src")
            // why so?
            .substring(1);

        final Function<String, Mono<String>> parseQueryId = scriptBody -> {
            Pattern varNamePattern = Pattern.compile("\\w{1,2}=(?<var>.{1,2}),\\w{1,2}=\"edge_followed_by\"");
            Matcher matcher = varNamePattern.matcher(scriptBody);
            if (matcher.find()) {
                String varName = matcher.group("var");

                Matcher varValueMatcher = Pattern.compile(varName + "=\"(?<id>\\d{17})\"").matcher(scriptBody);
                if (varValueMatcher.find()) {
                    return Mono.just(varValueMatcher.group("id"));
                }
            }
            return Mono.empty();
        };

        return webClient.get().uri(INSTAGRAM_URL)
            .cookies(requestCookies -> authentication.getCookies().forEach(requestCookies::add))
            .exchange()
            .flatMap(pageResponse -> pageResponse.bodyToMono(String.class))
            .map(Jsoup::parse)
            .map(findScriptUrl)
            .flatMap(scriptUrl -> webClient.get().uri(INSTAGRAM_URL + scriptUrl).exchange())
            .flatMap(scriptFile -> scriptFile.bodyToMono(String.class))
            .flatMap(parseQueryId);
    }

    public Mono<String> getUserId(AuthenticationDto authentication, String userName) {
        if (authentication.getLogin().equals(userName)) {
            return Mono.just(authentication.getUserId());
        }
        return getUserId(HashMap.ofAll(authentication.getCookies()), userName);
    }

    private Mono<Map<String, String>> queryForCookies() {
        Mono<ClientResponse> frontPageResponse = webClient.get().uri(INSTAGRAM_URL).exchange();
        return frontPageResponse
            .map(ClientResponse::cookies)
            .map(this::mapResponseCookies);
    }

    private WebClient.RequestHeadersSpec<?> applyCookies
        (WebClient.RequestHeadersSpec<?> spec, java.util.Map<String, String> cookies) {
        return applyCookies(spec, HashMap.ofAll(cookies));
    }

    private WebClient.RequestHeadersSpec<?> applyCookies
        (WebClient.RequestHeadersSpec<?> spec, Map<String, String> cookies) {
        return cookies.foldLeft(spec, (acc, cookie) -> acc.cookie(cookie._1, cookie._2));
    }

    private Map<String, String> mapResponseCookies(MultiValueMap<String, ResponseCookie> responseCookies) {
        return HashMap.ofAll(responseCookies.toSingleValueMap())
            .mapValues(HttpCookie::getValue);
    }

    private Mono<String> getUserId(Map<String, String> cookies, String userName) {
        Mono<ClientResponse> response = webClient.get()
            .uri(String.format("%s%s/?__a=1", INSTAGRAM_URL, userName))
            .cookies(requestCookies -> cookies.forEach(requestCookies::add))
            .exchange();
        return response.flatMap(clientResponse -> clientResponse.bodyToMono(String.class))
            .map(API.unchecked(mapper::readTree))
            .map(tree -> tree.get("user"))
            .map(tree -> tree.get("id"))
            .map(JsonNode::asText);
    }

    private String paramsToString(Map<String, String> params) {
        return params.map(e -> unchecked(() -> e._1 + "=" + URLEncoder.encode(e._2, "UTF-8")).get())
            .collect(Collectors.joining("&"));
    }

}
