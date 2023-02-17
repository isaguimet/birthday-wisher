package com.birthdaywisher.server.service;

import com.birthdaywisher.server.model.User;
import com.birthdaywisher.server.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getSingleUser(ObjectId id) {
        return userRepository.findById(id);
    }

    public Boolean addUser(User user) {
        // Check if user already has an account. Emails need to be unique
        Optional<User> optionalUser = userRepository.findUserByEmail(user.getEmail());
        if (optionalUser.isEmpty()) {
            userRepository.save(user);
        }
        return optionalUser.isEmpty();
    }

    public Optional<User> authorizeUser(String email, String password) {
        return userRepository.findUserByEmailAndPassword(email, password);
    }

    public void deleteUser(ObjectId id) {
        userRepository.deleteById(id);
    }

    public Optional<User> doesEmailExist(String email) {
        return userRepository.findUserByEmail(email);
    }

    public void sendFriendRequest(Optional<User> optionalUser, String userEmail,
                                  Optional<User> optionalFriend, String friendEmail) {
        User user = optionalUser.get();
        User friend = optionalFriend.get();

        savePendingFriendRequests(friendEmail, user, user.getFriendHashMap());
        savePendingFriendRequests(userEmail, friend, friend.getFriendHashMap());
    }

    private void savePendingFriendRequests(String friendEmail, User friend, HashMap<String, Boolean> friendHashMap) {
        HashMap<String, Boolean> pendingFriendRequests;
        if (friend.getFriendHashMap() == null) {
            pendingFriendRequests = new HashMap<>();
        } else {
            pendingFriendRequests = friendHashMap;
        }
        // Pending friend requests are set to false. Accepted friends requests are set to true
        pendingFriendRequests.put(friendEmail, false);
        friend.setFriendHashMap(pendingFriendRequests);
        userRepository.save(friend);
    }

    public List<String> getPendingFriendRequests(User user) {
        HashMap<String, Boolean> friendHashMap = user.getFriendHashMap();
        List<String> pendingFriendRequests = new ArrayList<>();

        if (friendHashMap == null) {
            return Collections.emptyList();
        }
        else {
            friendHashMap.forEach((key, value) -> {
                // {email:false} means that this is a pending friend request
                if (friendHashMap.containsValue(false)) {
                    pendingFriendRequests.add(key);
                }
            });
            return pendingFriendRequests;
        }
    }
}
