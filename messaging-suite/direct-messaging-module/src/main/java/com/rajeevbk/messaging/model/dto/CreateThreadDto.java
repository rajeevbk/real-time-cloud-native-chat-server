package com.rajeevbk.messaging.model.dto;

import java.util.Set;
import lombok.Data;
@Data
public class CreateThreadDto {
    private String threadName;
    private Set<String> participantIds;
}
