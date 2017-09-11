package ua.abond.instaret.entity;



import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowedBy {

    private List<User> data;

    @Getter
    @Setter
    public static class User {

        private String id;

        private String username;

        private String fullName;

        private String profileImage;

    }

}
