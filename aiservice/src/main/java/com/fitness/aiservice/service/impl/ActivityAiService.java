package com.fitness.aiservice.service.impl;

import com.fitness.aiservice.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiService {
    private final GeminiService geminiService;

    public String generateRecommendation(Activity activity){
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getAnswer(prompt);
        log.info("Response From Ai:{}",aiResponse);
        return aiResponse;
    }

    public String createPromptForActivity(Activity activity){
        return String.format(
                """
                 Analyze This Fitness activity and provide detailed recommendation in the following EXACT Json format:
                 {
                    "analysis": {
                        "overall":"overall analysis here",
                        "pace":"pace analysis here",
                        "heartRate":"Heart rate analysis here",
                        "caloriesBurned":"Calories analysis here"
                    },
                    "improvements":[
                        {
                            "area":"Area name",
                            "recommendation":"Detailed recommendation"
                        }
                    ],
                    "suggestions":[
                    {
                        "workout":"workout name",
                        "description":"detailed workout description "
                    }
                    ],
                    "safety":[
                        "safety":[
                        "Safety point 1",
                        "Safety point 2"
                        ]
                    ],
                 }
                 Analyze this activity:
                 Activity Type: %s
                 Duration: %d minutes
                 Calories Burned: %d
                 Additional Metrics: %s
                 
                 provide a detailed analysis focusing on past performance for focusing on performance,
                 improvements, next workout suggestions and safety guidelines ensure that the response follows the EXACT
                 Json format shown above
                 """,
                activity.getActivityType()  ,
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }
}
