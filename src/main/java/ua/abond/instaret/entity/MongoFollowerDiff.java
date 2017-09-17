package ua.abond.instaret.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ua.abond.instaret.dto.FollowedBy;
import ua.abond.instaret.model.FollowerDiff;

@Getter
@Document
@AllArgsConstructor
public class MongoFollowerDiff implements FollowerDiff {

    @Id
    private String userId;
    private LocalDateTime creationDate;
    private Set<FollowedBy> added;
    private Set<FollowedBy> removed;

}
