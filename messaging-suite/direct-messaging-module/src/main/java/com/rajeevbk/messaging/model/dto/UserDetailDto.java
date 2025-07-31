package com.rajeevbk.messaging.model.dto;

import lombok.Data;

// A simple DTO to match the response from the user service's /api/users/details endpoint
@Data
public class UserDetailDto {
    private String firstName;
    private String lastName;
    private String username;
}