package ua.abond.instaret.repository;

import static io.vavr.API.unchecked;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.Tuple2;
import org.springframework.stereotype.Component;
import ua.abond.instaret.dto.FollowedBy;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Component
public class InMemoryFollowerRepository implements FollowerRepository {

    private static final String FILE_NAME = "db.map";

    private final ObjectMapper mapper;
    private final ConcurrentMap<String, List<FollowedBy>> followerMap;

    public InMemoryFollowerRepository(ObjectMapper mapper) throws IOException, URISyntaxException {
        this.mapper = mapper;
        this.followerMap = new ConcurrentHashMap<>();
        URL url = this.getClass().getResource("/");
        File file = new File(new File(new URI(url.toString())), FILE_NAME);
        if (!file.exists()) {
            file.createNewFile();
        }
        Arrays.stream(new String(Files.readAllBytes(file.toPath())).split("\n"))
            .filter(l -> !l.isEmpty())
            .map(line -> {
                String[] split = line.split("=");
                return new Tuple2<>(split[0], split[1]);
            })
            .map(tuple -> {
                List<FollowedBy> followers = Arrays.asList(
                    unchecked(() -> mapper.readValue(tuple._2, FollowedBy[].class)).get());
                return new Tuple2<>(tuple._1, followers);
            })
            .forEach(t -> followerMap.put(t._1, t._2));
    }

    @Override
    public List<FollowedBy> getFollowers(String userName) throws IOException {
        return followerMap.get(userName);
    }

    @Override
    public void persist(String userName, List<FollowedBy> followers) throws IOException, URISyntaxException {
        followerMap.remove(userName);
        followerMap.put(userName, followers);

        List<String> linesToWrite = followerMap.entrySet()
            .stream()
            .map(e -> unchecked(() -> e.getKey() + "=" + mapper.writeValueAsString(e.getValue())).get())
            .collect(Collectors.toList());
        URL url = this.getClass().getResource("/");
        File file = new File(new File(new URI(url.toString())), FILE_NAME);
        if (!file.exists()) {
            file.createNewFile();
        }
        Files.write(file.toPath(), linesToWrite);
    }

}
