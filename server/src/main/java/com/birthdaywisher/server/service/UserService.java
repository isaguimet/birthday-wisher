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

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public void sendFriendRequest(Optional<User> optionalUser, Optional<User> optionalFriend) {
        User user = optionalUser.get();
        User friend = optionalFriend.get();

        savePendingFriendRequests(friend, user);
        savePendingFriendRequests(user, friend);
    }

    private void savePendingFriendRequests(User user, User friend) {
        HashMap<ObjectId, Boolean> pendingFriendRequests;
        if (friend.getFriends() == null) {
            pendingFriendRequests = new HashMap<>();
        } else {
            pendingFriendRequests = friend.getFriends();
        }
        // Pending friend requests are set to false. Accepted friends requests are set to true
        pendingFriendRequests.put(user.getId(), false);
        friend.setFriends(pendingFriendRequests);
        userRepository.save(friend);
    }

    public List<User> getPendingFriendRequests(User user) {
        HashMap<ObjectId, Boolean> friendHashMap = user.getFriends();
        List<User> pendingFriendRequests = new ArrayList<>();

        if (friendHashMap == null) {
            return Collections.emptyList();
        }
        else {
            friendHashMap.forEach((key, value) -> {
                // value=false means that this is a pending friend request
                if (!value && userRepository.findById(key).isPresent()) {
                    pendingFriendRequests.add(userRepository.findById(key).get());
                }
            });
            return pendingFriendRequests;
        }
    }

    public void acceptFriendRequest(User user, User friend) {
        HashMap<ObjectId, Boolean> userFriendHashMap = user.getFriends();
        HashMap<ObjectId, Boolean> friendHashMap = friend.getFriends();

        if (userFriendHashMap.containsKey(friend.getId())) {
            userFriendHashMap.put(friend.getId(), true);
            userRepository.save(user);
        }

        if (friendHashMap.containsKey(user.getId())) {
            friendHashMap.put(user.getId(), true);
            userRepository.save(friend);
        }
    }

    public void declineFriendRequest(User user, User friend) {
        HashMap<ObjectId, Boolean> userFriendHashMap = user.getFriends();
        HashMap<ObjectId, Boolean> friendHashMap = friend.getFriends();

        if (userFriendHashMap.containsKey(friend.getId())) {
            userFriendHashMap.remove(friend.getId(), false);
            userRepository.save(user);
        }

        if (friendHashMap.containsKey(user.getId())) {
            friendHashMap.remove(user.getId(), false);
            userRepository.save(friend);
        }
    }

    public List<User> getFriendListByUser(User user) {
        HashMap<ObjectId, Boolean> friendHashMap = user.getFriends();
        List<User> friendList = new ArrayList<>();
        friendHashMap.forEach((userId, value) -> {
            // Reminder: value set to true means friend whereas value set to false means pending friend
            if (value && userRepository.findById(userId).isPresent()) {
                User friend = userRepository.findById(userId).get();
                friendList.add(friend);
            }
        });
        return friendList;
    }

    public User setProfilePic(User user, String profilePic) {
        user.setProfilePic(profilePic);
        return userRepository.save(user);
    }
}
