package com.minimart.controller;

import com.minimart.model.User;
import com.minimart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/registerUser")
    private User registerUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @GetMapping("/userList")
    private List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
