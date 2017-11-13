package ua.abond.metrics.repository.user;

import ua.abond.metrics.domain.user.User;

public interface UserRepository {

    User getById(String userId);

    User getByUserName(String userName);

}
