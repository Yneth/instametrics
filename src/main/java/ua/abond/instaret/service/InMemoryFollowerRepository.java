package ua.abond.instaret.service;

import static io.vavr.API.unchecked;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.Tuple2;
import lombok.Getter;
import lombok.Setter;
import ua.abond.instaret.entity.FollowedBy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class InMemoryFollowerRepository implements FollowerRepository {

    private static final String FILE_PATH = "/db.map";

    private final ConcurrentMap<String, List<FollowedBy>> followerMap;

    public InMemoryFollowerRepository() throws IOException {
        this.followerMap = new ConcurrentHashMap<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            file.createNewFile();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Arrays.stream(new String(Files.readAllBytes(Paths.get(FILE_PATH))).split("\n"))
            .map(line -> {
                String[] split = line.split("=");
                return new Tuple2<>(split[0], split[1]);
            })
            .map(tuple -> {
                List<FollowedBy> followers = Arrays.asList(unchecked(() -> objectMapper.readValue(tuple._2, FollowedBy[].class)).get());
                return new Tuple2<>(tuple._1, followers);
            })
            .forEach(t -> followerMap.put(t._1, t._2));
    }

    @Override
    public List<FollowedBy> getFollowers(String userName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> linesToWrite = followerMap.entrySet()
            .stream()
            .map(e -> {
                try {
                    return e.getKey() + "=" + objectMapper.writeValueAsString(e.getValue());
                } catch (JsonProcessingException e1) {
                    e1.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
        Files.write(Paths.get(FILE_PATH), linesToWrite);
        return followerMap.get(userName);
    }

    @Override
    public void persist(String userName, List<FollowedBy> followers) {
        followerMap.remove(userName);
        followerMap.put(userName, followers);
    }

}
