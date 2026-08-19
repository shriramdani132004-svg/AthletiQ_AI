package com.athletiq.backend.ai.phase11;

import tools.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiEvaluationProviderConfiguration {

    @Bean
    public JsonMapper athletiqAiJsonMapper() {
        return new JsonMapper();
    }

    @Bean
    public AiEvaluationProvider aiEvaluationProvider(
            JsonMapper objectMapper
    ) {
        return new MockAiEvaluationProvider();
    }

    @Bean
    public AiEvaluationService aiEvaluationService(
            AiEvaluationProvider provider,
            JsonMapper objectMapper
    ) {
        return new AiEvaluationService(
                provider,
                objectMapper
        );
    }
}