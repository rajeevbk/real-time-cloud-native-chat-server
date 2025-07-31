package com.rajeevbk.messaging.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypingStatusDto {
    private String senderId;
    private String recipientId;

    // RENAMED: from 'isTyping' to 'typing' to avoid serialization ambiguity.
    // The getter will still be isTyping() due to Lombok's behavior with booleans.
    private boolean typing;

    // Custom constructor for creating the outgoing payload
    public TypingStatusDto(String senderId, boolean typing) {
        this.senderId = senderId;
        this.typing = typing;
    }
}