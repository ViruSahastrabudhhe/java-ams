package com.dem_ams.domain.dto;

import com.dem_ams.constant.ApplicationConstants;
import com.dem_ams.domain.model.Role;
import com.dem_ams.domain.model.UserModel;

public record UserModelResponse(
        Long user_id,
        String username,
        String role
) {
    public static UserModelResponse from(UserModel userModel) {
        String roleName = userModel.getRoles().stream()
                .map(Role::getName)
                .filter(ApplicationConstants.USER_ROLES::contains)
                .toList().getFirst();

        return new UserModelResponse(userModel.getId(), userModel.getUsername(), roleName);
    }
}
