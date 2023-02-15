package com.birthdaywisher.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "messages")
public class Message {
    @Id
    private ObjectId id;
    private ObjectId fromUser;
    private ObjectId toUser;
    private LocalDate lastUpdatedDate;
    private String msgText;
}
