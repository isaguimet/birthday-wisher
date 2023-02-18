package com.birthdaywisher.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "boards")
public class Board {
    @Id
    private ObjectId id;
    private boolean isPublic = true;
    private boolean isOpen = true;
    private String year = String.valueOf(LocalDate.now().getYear());
    private ObjectId userId;
    private Map<ObjectId, Message> messages = new HashMap<>();

    public Board(ObjectId userId) { this.userId = userId; }
}
