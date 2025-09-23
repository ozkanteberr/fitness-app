package com.fitness.activityservice.service.impl;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import com.fitness.activityservice.service.IActivityService;
import com.fitness.activityservice.service.IUserValidationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityServiceImpl implements IActivityService {

    private final ActivityRepository activityRepository;
    private final IUserValidationService userValidationService;
    public ActivityServiceImpl(ActivityRepository activityRepository,IUserValidationService userValidationService){
        this.activityRepository=activityRepository;
        this.userValidationService=userValidationService;
    }

    @Override
    public ActivityResponse trackActivity(ActivityRequest request){

        boolean isValidate = userValidationService.validateUser(request.getUserId());
        if(!isValidate){
            throw new RuntimeException("Invalid user:"+request.getUserId());
        }
        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .activityType(request.getActivityType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();

        Activity savedActivity = activityRepository.save(activity);
        return mapToResponse(activity);
    }

    @Override
    public List<ActivityResponse> getUserActivities(String userId){
       List<Activity> activities=activityRepository.findByUserId((userId));
       return activities.stream()
               .map(this::mapToResponse)
               .collect(Collectors.toList());
    }

    public ActivityResponse getActivityById(String activityId){
        return activityRepository.findById(activityId)
                .map(this::mapToResponse)
                .orElseThrow(()->new RuntimeException("Activity not found"));
    }

    private ActivityResponse mapToResponse(Activity activity){
        ActivityResponse response = new ActivityResponse();
        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setActivityType(activity.getActivityType());
        response.setDuration(activity.getDuration());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setStartTime(activity.getStartTime());
        response.setAdditionalMetrics(activity.getAdditionalMetrics());
        response.setCreatedAt(activity.getCreatedAt());
        response.setUpdatedAt(activity.getUpdatedAt());
        return response;
    }
}
