package com.boot.dbskins.Service;

import com.boot.dbskins.Model.User;
import com.boot.dbskins.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getInfoAboutCurrentUser(Long id) {
        return userRepository.findUserById(id);
    }

    public int updateUserEmail(Long id, String email) {
        return userRepository.updateInfoAboutUserEmail(id, email);
    }

    public boolean deleteUser(Long id) {
        userRepository.deleteById(id);
        return userRepository.findById(id).isEmpty();
    }
}