package com.dem_ams.domain.dto;

public record LoginResponse(String message, UserModelResponse user) {
    public static LoginResponse from(String message, UserModelResponse user) {
        return new LoginResponse(message, user);
    }
}
