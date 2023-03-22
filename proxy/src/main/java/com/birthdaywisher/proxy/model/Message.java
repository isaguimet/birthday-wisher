package com.birthdaywisher.proxy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    private ObjectId id = new ObjectId();
    private ObjectId fromUserId;
    private ObjectId toUserId;
    private LocalDate lastUpdatedDate = LocalDate.now();
    private String msgText;
}
