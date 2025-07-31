package com.rajeevbk.messaging.model.dto;

import lombok.Data;
import java.time.Instant;

// This DTO will be sent to the client and will always include the names.
@Data
public class ParticipantDto {
    private String userId;
    private Instant joinedAt;
    private int unreadCount;
    private String firstName;
    private String lastName;
}