package ua.abond.metrics.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import ua.abond.metrics.domain.User;
import ua.abond.metrics.repository.UserRepository;

public interface MongoUserRepository extends
        UserRepository, MongoRepository<User, String> {
}
