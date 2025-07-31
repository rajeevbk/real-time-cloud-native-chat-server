package com.rajeevbk.controller;

import com.rajeevbk.dto.UserDto;
import com.rajeevbk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for user-related operations.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Fetches a list of all users from the local database.
     * @return A list of UserDto objects.
     */
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/search")
    public List<UserDto> searchUsers(@RequestParam("q") String query) {
        return userService.searchUsers(query);
    }

    @PostMapping("/details")
    public List<UserDto> getUsersDetails(@RequestBody List<String> usernames) {
        return userService.findByUsernames(usernames);
    }

}