package com.example.GamesHubMobileBackend.controller;

import com.example.GamesHubMobileBackend.RequestModels.LoginRequestModel;
import com.example.GamesHubMobileBackend.models.User;
import com.example.GamesHubMobileBackend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(
            path = "/users"
    )
    public ResponseEntity getUsers() {
        var currentUsers = userService.getUsers();
        if (CollectionUtils.isEmpty(currentUsers)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(currentUsers);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);

        if (user == null) {
            return ResponseEntity.badRequest().body("User Not Found.");
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users/username/{username}")
    public ResponseEntity<Object> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found.");
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users")
    public ResponseEntity<Object> addUser(@RequestBody User user) {

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username is required.");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required.");
        }

        if (user.getPassword() == null || user.getPassword().length() < 6) {
            return ResponseEntity.badRequest().body("Password must be at least 6 characters.");
        }

        return ResponseEntity.ok(userService.saveUser(user));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable String id, @RequestBody User user) {

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username is required.");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required.");
        }

        user.setId(id);
        User updated = userService.updateUser(user);

        if (updated == null) {
            return ResponseEntity.badRequest().body("User Not Found.");
        }

        return ResponseEntity.ok(updated);
    }

    //AVATAR
    @PutMapping("/users/{id}/avatar")
    public ResponseEntity<Object> updateAvatar(@PathVariable String id, @RequestBody Map<String, String> body) {
        String avatarUrl = body.get("avatarUrl");

        if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Avatar URL is required.");
        }

        User updated = userService.updateAvatar(id, avatarUrl);

        if (updated == null) {
            return ResponseEntity.badRequest().body("User Not Found.");
        }

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User Have Been Deleted");
    }

    @PostMapping("/users/login")
    // todo: need to use LoginRequestModel (DONE)
    public ResponseEntity<Object> login(@RequestBody LoginRequestModel loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.badRequest().body("Email and password are required.");
        }
        String token = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        if (token == null) {
            return ResponseEntity.badRequest().body("Invalid email or password.");
        }
        User user = userService.getUserByEmail(loginRequest.getEmail());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{id}/logout")
    public ResponseEntity<Object> logout(@PathVariable String id) {
        boolean success = userService.logout(id);
        if (!success) {
            return ResponseEntity.badRequest().body("User Not Found.");
        }
        return ResponseEntity.ok("Logged out successfully.");
    }
}
