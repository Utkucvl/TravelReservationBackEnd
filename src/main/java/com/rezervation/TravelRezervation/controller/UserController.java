package com.rezervation.TravelRezervation.controller;

import com.rezervation.TravelRezervation.dao.entity.User;
import com.rezervation.TravelRezervation.dto.UserDto;
import com.rezervation.TravelRezervation.dto.UserDtoWithName;
import com.rezervation.TravelRezervation.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getOneUser(@PathVariable Long userId){
        User user = userService.findById(userId);
        UserDto userDto = new UserDto();
        userDto.setUserName(user.getUserName());
        userDto.setId(user.getId());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
    @GetMapping()
    public ResponseEntity<List<UserDtoWithName>> getAll() {
        List<UserDtoWithName> data= new ArrayList<>();
        data = userService.getAllUsers();
        return ResponseEntity.ok(data);
    }

}