package ua.abond.metrics.service.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
@JsonIgnoreProperties(ignoreUnknown = true)
// TODO: rename to follow dto convention
public class FollowedBy {

    @JsonProperty("id")
    private String id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("profile_pic_url")
    private String profileImage;

}
