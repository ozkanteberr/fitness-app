package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;

import java.util.List;

public interface IActivityService {
    public ActivityResponse trackActivity(ActivityRequest request);
    public List<ActivityResponse> getUserActivities(String userId);
    public ActivityResponse getActivityById(String activityId);
}
