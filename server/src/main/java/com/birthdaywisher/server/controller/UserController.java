package com.birthdaywisher.server.controller;

import com.birthdaywisher.server.model.User;
import com.birthdaywisher.server.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    // Constructor dependency injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getSingleUser(@PathVariable ObjectId userId) {
        Optional<User> optionalUser = userService.getSingleUser(userId);
        try {
            if (optionalUser.isPresent()) {
                return new ResponseEntity<>(optionalUser, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User ID not found: " + userId, HttpStatus.NOT_FOUND);
                }
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        try {
            Boolean newUser = userService.addUser(user);
            if (newUser) {
                return new ResponseEntity<>(user, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("User is already registered under this email: " + user.getEmail(),
                        HttpStatus.BAD_REQUEST);
            }
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authorizeUser(@RequestBody Map<String, String> requestBody) {
        try {
            if (!requestBody.containsKey("email") || !requestBody.containsKey("password")) {
                return new ResponseEntity<>("Must provide \"username\" and \"password\"", HttpStatus.BAD_REQUEST);
            }
            Optional<User> optionalUser = userService.authorizeUser(requestBody.get("email"), requestBody.get("password"));
            if (optionalUser.isPresent()) {
                return new ResponseEntity<>(optionalUser, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found with that username and password", HttpStatus.NOT_FOUND);
            }
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable ObjectId id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>("User has been deleted by id: " + id, HttpStatus.OK);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PatchMapping("/friendRequest")
    public ResponseEntity<?> sendFriendRequest(@RequestParam String userEmail,
                                               @RequestParam String friendEmail) {
        try {
            Optional<User> optionalUser = userService.doesEmailExist(userEmail);
            Optional<User> optionalFriend = userService.doesEmailExist(friendEmail);

            if (optionalUser.isEmpty()) {
                return new ResponseEntity<>("User email does not exist: " + userEmail, HttpStatus.NOT_FOUND);
            }
            if (optionalFriend.isEmpty()) {
                return new ResponseEntity<>("Friend email does not exist: " + friendEmail, HttpStatus.NOT_FOUND);
            }

            userService.sendFriendRequest(optionalUser, userEmail, optionalFriend, friendEmail);

            return new ResponseEntity<>(optionalFriend.get(), HttpStatus.OK);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
