package com.birthdaywisher.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "boards")
public class Board {
    @Id
    private ObjectId id;
    private boolean isPublic;
    private String year;
    @DocumentReference
    private ObjectId user;
    @DocumentReference
    private List<Message> msgIds;
}
