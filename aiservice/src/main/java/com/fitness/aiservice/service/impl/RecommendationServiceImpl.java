package com.fitness.aiservice.service.impl;

import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import com.fitness.aiservice.service.IRecommendationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationServiceImpl implements IRecommendationService {
    private final RecommendationRepository recommendationRepository;
    public RecommendationServiceImpl(RecommendationRepository recommendationRepository){
        this.recommendationRepository=recommendationRepository;
    }
    @Override
    public List<Recommendation> getUserRecommendation(String userId){
        return recommendationRepository.findByUserId((userId));
    }

    @Override
    public Recommendation getActivityRecommendation(String activityId){
        return (Recommendation) recommendationRepository.findByActivityId((activityId))
                .orElseThrow(() -> new RuntimeException("Recommendation not found"));
    }
}
