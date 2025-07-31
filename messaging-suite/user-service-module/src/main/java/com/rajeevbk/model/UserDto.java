package com.rajeevbk.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    // This is the crucial field for sending messages
    private String username;
}