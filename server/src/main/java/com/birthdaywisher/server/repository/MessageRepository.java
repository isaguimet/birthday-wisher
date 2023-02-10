package com.birthdaywisher.server.repository;

import com.birthdaywisher.server.model.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<Message, ObjectId> {
}
