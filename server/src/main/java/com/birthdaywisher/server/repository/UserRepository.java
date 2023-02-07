package com.birthdaywisher.server.repository;

import com.birthdaywisher.server.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // TODO: Add required queries
}
