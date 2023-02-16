package com.birthdaywisher.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "boards")
public class Board {
    @Id
    private ObjectId id;
    private boolean isPublic = true;
    private String year = String.valueOf(LocalDate.now().getYear());
    private ObjectId userId;
    @DocumentReference
    private List<Message> msgIds = new ArrayList<>();
}
