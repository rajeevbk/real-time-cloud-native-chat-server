package com.rajeev.chat_server.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatAuthController {

    // Join server POST /chat/join
    @PostMapping("/join")
    public ResponseEntity<String> joinServer(@RequestParam String username) {
        // Here you would typically authenticate the user and register their session
        // For now, just return a success message
        return ResponseEntity.ok("User " + username + " joined the chat server.");
    }

    // exit server

}