package com.fitness.aiservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiService {
    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity){
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getAnswer(prompt);
        log.info("Response From Ai:{}",aiResponse);
        return processAiResponse(activity,aiResponse);
    }

    public Recommendation processAiResponse(Activity activity,String aiResponse){
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);

            JsonNode textNode = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            String jsonContent = textNode.asText()
                    .replaceAll("```json\\n","")
                    .replaceAll("\\n```","")
                    .trim();

            log.info("Parsed response from ai:{} " ,jsonContent);

            JsonNode analysisJson = mapper.readTree(jsonContent);

            JsonNode analysisNode = analysisJson.path("analysis");

            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis,analysisNode,"overall","Overall:");
            addAnalysisSection(fullAnalysis,analysisNode,"pace","Pace:");
            addAnalysisSection(fullAnalysis,analysisNode,"heartRate","Heart Rate:");
            addAnalysisSection(fullAnalysis,analysisNode,"caloriesBurned","Calories Burned:");


            List<String> improvements = extractImprovements(analysisJson.path("improvements"));
            List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));
            List<String> safety = extractSafetyGuidelines(analysisJson.path("safety"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getActivityType())
                    .recommendations(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();
        }catch(Exception e){
            e.printStackTrace();
            return createDefaultRecommendation(activity);
        }
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getActivityType())
                .recommendations("Unable to generate detailed analysis.")
                .improvements(Collections.singletonList("Continue with your current routine."))
                .suggestions(Collections.singletonList("Consider consulting a fitness professional trainer."))
                .safety(Arrays.asList("Always warm up before exercise","Stay hydrated","Listen to your body"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafetyGuidelines(JsonNode safetyNode) {
        List<String> safetysList = new ArrayList<>();
        if(safetyNode.isArray()){
            safetyNode.forEach(item->safetysList.add(item.asText()));
        }
        return safetysList.isEmpty() ? Collections.singletonList("No specific safety provided.") : safetysList;
    }

    private List<String> extractSuggestions(JsonNode suggestionsNode) {
        List<String> suggestions = new ArrayList<>();
        if(suggestionsNode.isArray()){
            suggestionsNode.forEach(suggestion->{
                String workout = suggestion.path("workout").asText() ;
                String description = suggestion.path("description").asText();
                suggestions.add(String.format("%s:%s",workout,description));
            });
        }
        return suggestions.isEmpty() ? Collections.singletonList("Follow general safety guidelines.") : suggestions;
    }

    private List<String> extractImprovements(JsonNode improvementsNode) {
        List<String> improvements = new ArrayList<>();

        if(improvementsNode.isArray()){
            improvementsNode.forEach(improvement->{
                String area = improvement.path("area").asText();
                String detail= improvement.path("recommendation").asText();
                improvements.add(String.format(" %s : %s",area,detail));
            });
        }

        return improvements.isEmpty() ? Collections.singletonList("No specific improvements provided") : improvements;

    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if(!analysisNode.path(key).isMissingNode()){
            fullAnalysis.append(key)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
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
