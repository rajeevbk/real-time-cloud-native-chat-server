package com.rajeevbk.messaging.model.dto;

import lombok.Data;

@Data
public class PrivateMessageDto {
    private String recipientId;
    private String content;
}
