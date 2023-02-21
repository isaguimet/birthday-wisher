package com.birthdaywisher.server.repository;

import com.birthdaywisher.server.model.Board;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends MongoRepository<Board, ObjectId> {
    Optional<Board> findBoardByUserIdAndYear(ObjectId userId, String year);

    List<Board> findBoardsByUserId(ObjectId userId);
}
