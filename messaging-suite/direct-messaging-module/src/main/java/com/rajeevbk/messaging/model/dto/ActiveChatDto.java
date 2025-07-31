package com.rajeevbk.messaging.model.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ActiveChatDto {
    private UUID threadId;
}