package com.birthdaywisher.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "messages")
public class Message {
    @Id
    private ObjectId id;
    @DocumentReference
    private String fromUser;
    @DocumentReference
    private String toUser;
    private LocalDate date;
    private String msgText;
    private String msgVisibility;
}
