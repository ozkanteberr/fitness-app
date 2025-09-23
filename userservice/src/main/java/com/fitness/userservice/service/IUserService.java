package com.fitness.userservice.service;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;

public interface IUserService {
    public UserResponse register(RegisterRequest request);
    public UserResponse getUserProfile(String userId);
    public Boolean existByUserId(String userId);
}
