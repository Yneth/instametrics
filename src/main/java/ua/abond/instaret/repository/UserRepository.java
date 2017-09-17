package ua.abond.instaret.repository;

import ua.abond.instaret.entity.User;

public interface UserRepository {

    User getById(String userId);

    User getByUserName(String userName);

}
