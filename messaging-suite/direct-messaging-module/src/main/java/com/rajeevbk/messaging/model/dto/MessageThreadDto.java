package com.rajeevbk.messaging.model.dto;

import lombok.Data;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
public class MessageThreadDto {
    private UUID threadId;
    private Set<ParticipantDto> participants;
    private Instant lastActivityAt;
    private String lastMessagePreview;
    private String lastMessageSender;

}