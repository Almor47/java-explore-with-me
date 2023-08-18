package ru.practicum.stats_server.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiError {

    private String message;
    private String reason;
    private String status;
    private String timestamp;
    //private String errors;

}