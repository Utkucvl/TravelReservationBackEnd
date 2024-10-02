package com.rezervation.TravelRezervation.service;

import com.rezervation.TravelRezervation.dao.UserRepository;
import com.rezervation.TravelRezervation.dao.entity.User;
import com.rezervation.TravelRezervation.dto.UserDtoWithName;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private UserRepository userRepository;




    public UserService(UserRepository userRepository ) {
        this.userRepository = userRepository;
    }

    public List<UserDtoWithName> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> {
                    UserDtoWithName userDtoWithName = new UserDtoWithName();
                    userDtoWithName.setId(user.getId());
                    userDtoWithName.setUserName(user.getUserName());
                    return userDtoWithName;
                })
                .collect(Collectors.toList());
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User createOneUser(User user) {
        return userRepository.save(user);
    }

    public User getOneUserByUserName(String userName) {
        return (userRepository.findByUserName(userName));
    }
    public User getOneUserByEmail(String email) {
        return (userRepository.findByEmail(email));
    }



}
