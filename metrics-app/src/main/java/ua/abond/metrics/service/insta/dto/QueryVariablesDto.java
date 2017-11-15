package ua.abond.metrics.service.insta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public final class QueryVariablesDto {

    @JsonProperty("id")
    private String userId;

    @JsonProperty("first")
    private int batchSize;

    @JsonProperty("after")
    private String cursor;

    public QueryVariablesDto(String userId, int count) {
        this.userId = userId;
        this.batchSize = count;
    }

    QueryVariablesDto(String userId, int batchSize, String cursor) {
        this.userId = userId;
        this.batchSize = batchSize;
        this.cursor = cursor;
    }

    public QueryVariablesDto next(String cursor) {
        return new QueryVariablesDto(this.userId, this.batchSize, cursor);
    }

}
