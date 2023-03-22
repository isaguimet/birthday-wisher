package com.birthdaywisher.proxy.controller;

import com.birthdaywisher.proxy.model.User;
import com.birthdaywisher.proxy.service.ProxyService;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {
    private ProxyService proxyService;

    public UserController(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllUsers() {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getSingleUser(@PathVariable ObjectId userId) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/byEmail/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authorizeUser(@RequestBody Map<String, String> requestBody) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/friendRequest")
    public ResponseEntity<?> sendFriendRequest(@RequestParam String userEmail,
                                               @RequestParam String friendEmail) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pendingFriendRequests/{userId}")
    public ResponseEntity<?> getPendingFriendRequests(@PathVariable ObjectId userId) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/pendingFriendRequests/accept")
    public ResponseEntity<?> acceptFriendRequest(@RequestParam ObjectId userId,
                                                 @RequestParam String friendEmail) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/pendingFriendRequests/decline")
    public ResponseEntity<?> declineFriendRequest(@RequestParam ObjectId userId,
                                                  @RequestParam String friendEmail) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/friendList/{userId}")
    public ResponseEntity<?> getFriendList(@PathVariable ObjectId userId) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/setProfilePic/{userId}")
    public ResponseEntity<?> setProfilePic(@RequestParam String profilePic, @PathVariable ObjectId userId) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
