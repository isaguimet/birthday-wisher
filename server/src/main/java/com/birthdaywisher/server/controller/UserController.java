package com.birthdaywisher.server.controller;

import com.birthdaywisher.server.leader.LeaderService;
import com.birthdaywisher.server.model.User;
import com.birthdaywisher.server.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.MonthDay;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    private LeaderService leaderService;

    // Constructor dependency injection
    public UserController(UserService userService, LeaderService leaderService) {
        this.userService = userService;
        this.leaderService = leaderService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllUsers() {
        try {
            return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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

    @GetMapping("/byEmail/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        Optional<User> optionalUser = userService.findUserByEmail(email);
        try {
            if (optionalUser.isPresent()) {
                return new ResponseEntity<>(optionalUser.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Email not found: " + email, HttpStatus.NOT_FOUND);
            }
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        try {
            // Check if user already has an account. Emails need to be unique
            Optional<User> optionalUser = userService.findUserByEmail(user.getEmail());
            if (optionalUser.isPresent()) {
                return new ResponseEntity<>("User is already registered under this email: " + user.getEmail(),
                        HttpStatus.BAD_REQUEST);
            }
            User newUser = userService.addUser(user);
            leaderService.forwardUserReqToBackups(newUser, "signUp");
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/forwarded/signUp")
    public ResponseEntity<?> forwardAddUser(@RequestBody User user) {
        try {
            // Check if user already has an account. Emails need to be unique
            Optional<User> optionalUser = userService.findUserByEmail(user.getEmail());
            if (optionalUser.isPresent()) {
                return new ResponseEntity<>("User is already registered under this email: " + user.getEmail(),
                        HttpStatus.BAD_REQUEST);
            }
            User newUser = userService.addUser(user);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable ObjectId id) {
        try {
            userService.deleteUser(id);
            leaderService.forwardUserReqToBackups(id, "delete");
            return new ResponseEntity<>("User has been deleted by id: " + id, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/forwarded/{id}")
    public ResponseEntity<String> forwardDeleteUser(@PathVariable ObjectId id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>("User has been deleted by id: " + id, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/friendRequest")
    public ResponseEntity<?> sendFriendRequest(@RequestParam String userEmail,
                                               @RequestParam String friendEmail) {
        try {
            Optional<User> optionalUser = userService.findUserByEmail(userEmail);
            Optional<User> optionalFriend = userService.findUserByEmail(friendEmail);

            if (optionalUser.isEmpty()) {
                return new ResponseEntity<>("User email does not exist: " + userEmail, HttpStatus.NOT_FOUND);
            }
            if (optionalFriend.isEmpty()) {
                return new ResponseEntity<>("Friend email does not exist: " + friendEmail, HttpStatus.NOT_FOUND);
            }

            Boolean areFriendsAlready = userService.checkIfAlreadyFriends(optionalUser.get(), optionalFriend.get());
            if (areFriendsAlready) {
                return new ResponseEntity<>("User email " + userEmail + " and friend email " + friendEmail + " are already friends",
                        HttpStatus.BAD_REQUEST);
            }
            Boolean isDupFriendRequest = userService.isDuplicatedFriendRequest(optionalUser.get(), optionalFriend.get());
            if (isDupFriendRequest) {
                return new ResponseEntity<>("User email " + userEmail + " already sent a friend request to " + friendEmail,
                        HttpStatus.BAD_REQUEST);
            }
            userService.sendFriendRequest(optionalUser.get(), optionalFriend.get());

            leaderService.forwardUserReqToBackups(userEmail, friendEmail, "friendRequest");
            return new ResponseEntity<>(userService.getPendingFriendRequests(optionalUser.get()), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/forwarded/friendRequest")
    public ResponseEntity<?> forwardSendFriendRequest(@RequestParam String userEmail,
                                                      @RequestParam String friendEmail) {
        try {
            Optional<User> optionalUser = userService.findUserByEmail(userEmail);
            Optional<User> optionalFriend = userService.findUserByEmail(friendEmail);

            if (optionalUser.isEmpty()) {
                return new ResponseEntity<>("User email does not exist: " + userEmail, HttpStatus.NOT_FOUND);
            }
            if (optionalFriend.isEmpty()) {
                return new ResponseEntity<>("Friend email does not exist: " + friendEmail, HttpStatus.NOT_FOUND);
            }

            Boolean areFriendsAlready = userService.checkIfAlreadyFriends(optionalUser.get(), optionalFriend.get());
            if (areFriendsAlready) {
                return new ResponseEntity<>("User email " + userEmail + " and friend email " + friendEmail + " are already friends",
                        HttpStatus.BAD_REQUEST);
            }
            Boolean isDupFriendRequest = userService.isDuplicatedFriendRequest(optionalUser.get(), optionalFriend.get());
            if (isDupFriendRequest) {
                return new ResponseEntity<>("User email " + userEmail + " already sent a friend request to " + friendEmail,
                        HttpStatus.BAD_REQUEST);
            }
            userService.sendFriendRequest(optionalUser.get(), optionalFriend.get());
            return new ResponseEntity<>(optionalFriend.get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pendingFriendRequests/{userId}")
    public ResponseEntity<?> getPendingFriendRequests(@PathVariable ObjectId userId) {
        try {
            Optional<User> optionalUser = userService.getSingleUser(userId);
            if (optionalUser.isEmpty()) {
                return new ResponseEntity<>("User id given does not exist: " + userId, HttpStatus.NOT_FOUND);
            } else {
                List<User> pendingFriendRequests = userService.getPendingFriendRequests(optionalUser.get());
                return new ResponseEntity<>(pendingFriendRequests, HttpStatus.OK);
            }
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PatchMapping("/pendingFriendRequests/accept")
    public ResponseEntity<?> acceptFriendRequest(@RequestParam ObjectId userId,
                                                 @RequestParam String friendEmail) {
        try {
            Optional<User> optionalUser = userService.getSingleUser(userId);
            Optional<User> optionalFriend = userService.findUserByEmail(friendEmail);

            if (optionalUser.isEmpty()) {
                return new ResponseEntity<>("User id given does not exist: " + userId, HttpStatus.NOT_FOUND);
            }
            if (optionalFriend.isEmpty()) {
                return new ResponseEntity<>("Friend email does not exist: " + friendEmail, HttpStatus.NOT_FOUND);
            }

            Boolean isAccepted = userService.acceptFriendRequest(optionalUser.get(), optionalFriend.get());
            if (!isAccepted) {
                return new ResponseEntity<>("User id given, " + userId +  ", sent the friend request. " +
                        "This userId cannot accept the request ", HttpStatus.BAD_REQUEST);
            }

            leaderService.forwardUserReqToBackups(userId, friendEmail, "acceptFriendRequest");

            List<User> updatedPendingFriends = userService.getPendingFriendRequests(optionalUser.get());

            List<User> updatedFriendList = userService.getFriendListByUser(optionalUser.get());
            updatedFriendList.sort(Comparator.comparing(friend -> MonthDay.from(friend.getBirthdate())));

            return new ResponseEntity<>(Arrays.asList(updatedPendingFriends, updatedFriendList), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/forwarded/pendingFriendRequests/accept")
    public ResponseEntity<?> forwardAcceptFriendRequest(@RequestParam ObjectId userId,
                                                        @RequestParam String friendEmail) {
        try {
            Optional<User> optionalUser = userService.getSingleUser(userId);
            Optional<User> optionalFriend = userService.findUserByEmail(friendEmail);

            if (optionalUser.isEmpty()) {
                return new ResponseEntity<>("User id given does not exist: " + userId, HttpStatus.NOT_FOUND);
            }
            if (optionalFriend.isEmpty()) {
                return new ResponseEntity<>("Friend email does not exist: " + friendEmail, HttpStatus.NOT_FOUND);
            }

            Boolean isAccepted = userService.acceptFriendRequest(optionalUser.get(), optionalFriend.get());
            if (!isAccepted) {
                return new ResponseEntity<>("User id given, " + userId +  ", sent the friend request. " +
                        "This userId cannot accept the request ", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(optionalUser.get(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/pendingFriendRequests/decline")
    public ResponseEntity<?> declineFriendRequest(@RequestParam ObjectId userId,
                                                  @RequestParam String friendEmail) {
        try {
            Optional<User> optionalUser = userService.getSingleUser(userId);
            Optional<User> optionalFriend = userService.findUserByEmail(friendEmail);

            if (optionalUser.isEmpty()) {
                return new ResponseEntity<>("User id given does not exist: " + userId, HttpStatus.NOT_FOUND);
            }
            if (optionalFriend.isEmpty()) {
                return new ResponseEntity<>("Friend email does not exist: " + friendEmail, HttpStatus.NOT_FOUND);
            }

            Boolean isDeclined = userService.declineFriendRequest(optionalUser.get(), optionalFriend.get());
            if (!isDeclined) {
                return new ResponseEntity<>("User id given, " + userId +  ", sent the friend request. " +
                        "This userId cannot decline the request ", HttpStatus.BAD_REQUEST);
            }
            leaderService.forwardUserReqToBackups(userId, friendEmail, "declineFriendRequest");
            return new ResponseEntity<>(userService.getPendingFriendRequests(optionalUser.get()), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/forwarded/pendingFriendRequests/decline")
    public ResponseEntity<?> forwardDeclineFriendRequest(@RequestParam ObjectId userId,
                                                         @RequestParam String friendEmail) {
        try {
            Optional<User> optionalUser = userService.getSingleUser(userId);
            Optional<User> optionalFriend = userService.findUserByEmail(friendEmail);

            if (optionalUser.isEmpty()) {
                return new ResponseEntity<>("User id given does not exist: " + userId, HttpStatus.NOT_FOUND);
            }
            if (optionalFriend.isEmpty()) {
                return new ResponseEntity<>("Friend email does not exist: " + friendEmail, HttpStatus.NOT_FOUND);
            }

            Boolean isDeclined = userService.declineFriendRequest(optionalUser.get(), optionalFriend.get());
            if (!isDeclined) {
                return new ResponseEntity<>("User id given, " + userId +  ", sent the friend request. " +
                        "This userId cannot decline the request ", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(optionalUser.get(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/friendList/{userId}")
    public ResponseEntity<?> getFriendList(@PathVariable ObjectId userId) {
        try {
            Optional<User> optionalUser = userService.getSingleUser(userId);
            if (optionalUser.isEmpty()) {
                return new ResponseEntity<>("User id given does not exist: " + userId, HttpStatus.NOT_FOUND);
            }
            List<User> friendList = userService.getFriendListByUser(optionalUser.get());

            // Sorts friends of this user by their birthdate (ignores the year so sorting by month and day)
            friendList.sort(Comparator.comparing(friend -> MonthDay.from(friend.getBirthdate())));

            return new ResponseEntity<>(friendList, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PatchMapping("/setProfilePic/{userId}")
    public ResponseEntity<?> setProfilePic(@RequestParam String profilePic, @PathVariable ObjectId userId) {
        try {
            Optional<User> optionalUser = userService.getSingleUser(userId);
            if (optionalUser.isEmpty()) {
                return new ResponseEntity<>("User id given does not exist: " + userId, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(userService.setProfilePic(optionalUser.get(), profilePic), HttpStatus.OK);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
