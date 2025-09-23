package com.fitness.userservice.service.impl;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import com.fitness.userservice.service.IUserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @Override
    public UserResponse getUserProfile(String userId){
       User user = userRepository.findById(userId).orElseThrow(()->new RuntimeException("User Not Found"));
       UserResponse userResponse = new UserResponse();
       userResponse.setId(user.getId());
       userResponse.setEmail(user.getEmail());
       userResponse.setPassword(user.getPassword());
       userResponse.setFirstName(user.getFirstName());
       userResponse.setLastName(user.getLastName());
       userResponse.setCreatedAt(user.getCreatedAt());
       userResponse.setUpdatedAt(user.getUpdatedAt());
       return userResponse;
    }

    @Override
    public UserResponse register(RegisterRequest request){

        if(userRepository.existsByEmail((request.getEmail()))){
            throw new RuntimeException("Email already exist!");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User savedUser = userRepository.save(user);
        UserResponse userResponse = new UserResponse();

        userResponse.setId(savedUser.getId());
        userResponse.setEmail(savedUser.getEmail());
        userResponse.setPassword(savedUser.getPassword());
        userResponse.setFirstName(savedUser.getFirstName());
        userResponse.setLastName(savedUser.getLastName());
        userResponse.setCreatedAt(savedUser.getCreatedAt());
        userResponse.setUpdatedAt(savedUser.getUpdatedAt());

        return userResponse;
    }

    @Override
    public Boolean existByUserId(String userId){
        return userRepository.existsById(userId);
    }

}
