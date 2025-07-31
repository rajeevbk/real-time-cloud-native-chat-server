package com.rajeevbk.messaging.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresenceStatusDto {
    private String username;
    private boolean online;
}
