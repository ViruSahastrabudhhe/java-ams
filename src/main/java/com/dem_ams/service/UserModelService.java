package com.dem_ams.service;

import com.dem_ams.domain.exception.ResourceNotFoundException;
import com.dem_ams.domain.model.CustomUserDetails;
import com.dem_ams.domain.model.UserModel;
import com.dem_ams.repository.UserModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserModelService {
    private final UserModelRepository userModelRepository;

    public UserModelService(UserModelRepository userModelRepository) {
        this.userModelRepository = userModelRepository;
    }

    public UserModel getUserModelForUser(Long id, CustomUserDetails principal) {
        if (principal.isAdmin()) {
            return getUserModelById(id);
        }

        return getUserModelByUserId(principal.getId());
    }

    private UserModel getUserModelByUserId(Long userId) {
        return userModelRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Could not find user with id", userId));
    }

    private UserModel getUserModelById(Long id) {
        return userModelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Could not find user with id", id));
    }

    public void updateUserModel(UserModel userModelToUpdate, UserModel userModelRequest) {
        userModelToUpdate.setEmail(userModelRequest.getEmail());
        userModelToUpdate.setUsername(userModelRequest.getUsername());
        userModelToUpdate.setPassword(userModelRequest.getPassword());
        userModelRepository.saveAndFlush(userModelToUpdate);
    }
}
