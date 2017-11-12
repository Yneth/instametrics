package ua.abond.metrics.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "userId")
public class FollowingDto {

    @JsonProperty("id")
    private String userId;
    @JsonProperty("username")
    private String userName;
    @JsonProperty("profile_pic_url")
    private String profileUrl;

    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("is_verified")
    private boolean isVerified;
    @JsonProperty("requested_by_viewer")
    private boolean requestedByViewer;
    @JsonProperty("followed_by_viewer")
    private boolean followedByViewer;

}
