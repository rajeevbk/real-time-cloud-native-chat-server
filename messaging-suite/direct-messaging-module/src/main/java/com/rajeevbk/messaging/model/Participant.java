package com.rajeevbk.messaging.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
// This tells the JSON serializer to not include null fields (like firstName) in the Redis cache
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Participant implements Serializable {
    private String userId; // This is the preferred_username
    private Instant joinedAt;
    private int unreadCount;

    // NEW: Transient fields to hold the user's full name for the DTO.
    // 'transient' means these fields will not be saved in Redis.
    private transient String firstName;
    private transient String lastName;
}