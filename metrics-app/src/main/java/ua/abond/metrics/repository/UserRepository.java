package ua.abond.metrics.repository;

import ua.abond.metrics.domain.User;

public interface UserRepository {

    User getById(String userId);

    User getByUserName(String userName);

}
