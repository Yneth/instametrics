package ua.abond.metrics.repository.user.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import ua.abond.metrics.domain.user.User;
import ua.abond.metrics.repository.user.UserRepository;

public interface MongoUserRepository extends
        UserRepository, MongoRepository<User, String> {
}
