package com.rajeev.chat_server.messaging.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageKey implements Serializable {
    private UUID threadId;
    private UUID messageId;
}