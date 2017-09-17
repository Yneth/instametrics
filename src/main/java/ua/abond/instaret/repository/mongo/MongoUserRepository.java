package ua.abond.instaret.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import ua.abond.instaret.entity.User;
import ua.abond.instaret.repository.UserRepository;

public interface MongoUserRepository extends
        UserRepository, MongoRepository<User, String> {
}
