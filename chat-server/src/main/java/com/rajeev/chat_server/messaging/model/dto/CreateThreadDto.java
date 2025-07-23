package com.rajeev.chat_server.messaging.model.dto;

import lombok.Data;

import java.util.Set;
@Data
public class CreateThreadDto {
    private String threadName;
    private Set<String> participantIds;
}
