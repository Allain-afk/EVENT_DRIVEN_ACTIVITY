package com.example.legaspi_eventdriven_activity.network;

public class LoginResponse {
    private String message;
    private String token;
    private String error;

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public String getError() {
        return error;
    }
}
