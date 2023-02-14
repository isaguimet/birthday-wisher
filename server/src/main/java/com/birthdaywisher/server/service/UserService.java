package com.birthdaywisher.server.service;

import com.birthdaywisher.server.model.User;
import com.birthdaywisher.server.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
}
