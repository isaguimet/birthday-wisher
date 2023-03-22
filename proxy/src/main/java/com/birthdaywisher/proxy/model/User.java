package com.birthdaywisher.proxy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private ObjectId id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private LocalDate birthdate;
    private ArrayList<ObjectId> friends = new ArrayList<>();
    // Boolean represents if userId (ObjectId) initiated the friend request
    private HashMap<ObjectId, Boolean> pendingFriends = new HashMap<>();
    private String profilePic;
}
