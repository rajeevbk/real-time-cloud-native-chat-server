package com.rajeev.chat_server.messaging.adapter;

import com.rajeev.chat_server.messaging.model.dto.PrivateMessageDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@SecurityRequirement(name = "Keycloak")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/test/method")
     public ResponseEntity<?> sendMessage(@RequestBody PrivateMessageDto request) {
         // Logic to send a message
         return ResponseEntity.ok("Success");
     }

    @MessageMapping("/chat/private")
    public void sendPrivateMessage(@Payload PrivateMessageDto message, Principal principal) {
        String senderId = principal.getName();
        //Message savedMessage = chatService.sendPrivateMessage(senderId, message.getRecipientId(), message.getContent());

        // Send the message to the recipient's private queue
        messagingTemplate.convertAndSendToUser(
                message.getRecipientId(),
                "/queue/private",
                message.getContent()
        );

        // Send a copy back to the sender for UI updates
        messagingTemplate.convertAndSendToUser(
                senderId,
                "/queue/private",
                message.getContent()
        );
    }

}
