package com.birthdaywisher.server.service;

import com.birthdaywisher.server.model.User;
import com.birthdaywisher.server.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Service
public class UserService {
    private UserRepository userRepository;

    private ServerProperties serverProperties;

    @Autowired
    private RestTemplate restTemplate;

    public UserService(UserRepository userRepository, ServerProperties serverProperties) {
        this.userRepository = userRepository;
        this.serverProperties = serverProperties;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getSingleUser(ObjectId id) {
        return userRepository.findById(id);
    }

    public User addUser(User user) {
        return userRepository.save(user);
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

    public void sendFriendRequest(User fromUser, User toUser) {
        HashMap<ObjectId, Boolean> userFromPendingFriends = fromUser.getPendingFriends();
        HashMap<ObjectId, Boolean> userToPendingFriends = toUser.getPendingFriends();

        // the friend request was not initiated by "toUser", so value=false
        userFromPendingFriends.put(toUser.getId(), false);
        fromUser.setPendingFriends(userFromPendingFriends);
        userRepository.save(fromUser);

        // the friend request was initiated by "fromUser", so value=true
        userToPendingFriends.put(fromUser.getId(), true);
        toUser.setPendingFriends(userToPendingFriends);
        userRepository.save(toUser);
    }

    public Boolean isDuplicatedFriendRequest(User fromUser, User toUser) {
        // Check if a friend request was already sent
        HashMap<ObjectId, Boolean> userToPendingFriends = toUser.getPendingFriends();

        return userToPendingFriends.containsKey(fromUser.getId());
    }

    public Boolean checkIfAlreadyFriends(User fromUser, User toUser) {
        ArrayList<ObjectId> fromUserFriends = fromUser.getFriends();

        for (ObjectId friend : fromUserFriends) {
            if (friend.equals(toUser.getId())) {
                return true;
            }
        }
        return false;
    }

    public List<User> getPendingFriendRequests(User user) {
        HashMap<ObjectId, Boolean> pendingFriends = user.getPendingFriends();
        List<User> pendingFriendRequests = new ArrayList<>();

        if (pendingFriends == null) {
            return Collections.emptyList();
        }
        else {
            pendingFriends.forEach((userId, isInitiator) -> {
                // isInitiator=true means a userId (not the current userId) initiated the friend request
                // isInitiator=false means the current userId sent the friend request
                // client side shows which requests they need to accept/decline or waiting for request to be accepted/declined
                // based on isInitiator value
                if (userRepository.findById(userId).isPresent()) {
                    pendingFriendRequests.add(userRepository.findById(userId).get());
                }
            });
            return pendingFriendRequests;
        }
    }

    public Boolean acceptFriendRequest(User user, User friend) {
        HashMap<ObjectId, Boolean> pendingFriendsOfUser = user.getPendingFriends();
        HashMap<ObjectId, Boolean> pendingFriendsOfFriend = friend.getPendingFriends();

        ArrayList<ObjectId> userFriends = user.getFriends();
        ArrayList<ObjectId> friendFriends = friend.getFriends();

        // We can only accept friend requests if the user is NOT the one who initiated the request
        // If a particular friend id maps to false then user is the one who initiated
        if (!pendingFriendsOfUser.get(friend.getId())) {
            return false;
        } else {
            // Remove each other from each other's pending friends list
            if (pendingFriendsOfUser.containsKey(friend.getId())) {
                pendingFriendsOfUser.remove(friend.getId(), true);
            }
            if (pendingFriendsOfFriend.containsKey(user.getId())) {
                pendingFriendsOfFriend.remove(user.getId(), false);
            }
            // Add each other as friends
            userFriends.add(friend.getId());
            friendFriends.add(user.getId());

            userRepository.save(user);
            userRepository.save(friend);
            return true;
        }
    }

    public Boolean declineFriendRequest(User user, User friend) {
        HashMap<ObjectId, Boolean> pendingFriendsOfUser = user.getPendingFriends();
        HashMap<ObjectId, Boolean> pendingFriendsOfFriend = friend.getPendingFriends();

        // We can only decline friend requests if the user is NOT the one who initiated the request
        // If a particular friend id maps to false then user is the one who initiated
        if (!pendingFriendsOfUser.get(friend.getId())) {
            return false;
        } else {
            if (pendingFriendsOfUser.containsKey(friend.getId())) {
                pendingFriendsOfUser.remove(friend.getId(), true);
                userRepository.save(user);
            }

            if (pendingFriendsOfFriend.containsKey(user.getId())) {
                pendingFriendsOfFriend.remove(user.getId(), false);
                userRepository.save(friend);
            }
            return true;
        }
    }

    public List<User> getFriendListByUser(User user) {
        ArrayList<ObjectId> friends = user.getFriends();
        List<User> friendList = new ArrayList<>();

        for (ObjectId userId : friends) {
            if (userRepository.findById(userId).isPresent()) {
                User friend = userRepository.findById(userId).get();
                friendList.add(friend);
            }
        }
        return friendList;
    }

    public User setProfilePic(User user, String profilePic) {
        user.setProfilePic(profilePic);
        return userRepository.save(user);
    }

    public void checkLeader(User user) {
        // check if this is a leader somehow for now just getting ports
        Integer port = serverProperties.getPort();
        int response = 0;

        // Primary replica
        if (port == 8080) {
            URI uri1 = URI.create("http://localhost/users/signUp");

            if (uri1.getPort() == -1) {
                uri1 = UriComponentsBuilder.fromUri(uri1).port(8081).build().toUri();
            }
            // call post endpoint of other replicas
            ResponseEntity<?> response1 = restTemplate.postForObject(uri1, user, ResponseEntity.class);
            response++;

            // if response == number of replicas (for now), then we get all acks
            if (response == 1) {
                System.out.println(" I have received 1 ACKS from the replicas");
            }
        }
    }
}
