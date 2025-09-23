package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Recommendation;

import java.util.List;

public interface IRecommendationService {
    public List<Recommendation> getUserRecommendation(String userId);
    public Recommendation getActivityRecommendation(String activityId);
}
