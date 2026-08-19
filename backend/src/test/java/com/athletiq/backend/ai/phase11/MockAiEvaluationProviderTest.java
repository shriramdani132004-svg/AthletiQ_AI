package com.athletiq.backend.ai.phase11;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class MockAiEvaluationProviderTest {

    @Test
    void producesDeterministicStructuredProviderResult() {

        MockAiEvaluationProvider provider =
                new MockAiEvaluationProvider();

        AiEvaluationProviderRequest request =
                new AiEvaluationProviderRequest(
                        "TEST-CANDIDATE-001",
                        "candidate",
                        "requirements",
                        "application",
                        "criteria",
                        "objective",
                        "prompt-v1"
                );

        AiEvaluationProviderResult result =
                provider.evaluate(request);

        assertNotNull(result);
        assertEquals("MOCK", result.provider());
        assertEquals(
                "athletiq-mock-v1",
                result.model()
        );

        assertNotNull(result.rawResponse());

        assertTrue(
                result.rawResponse().contains(
                        "\"candidateReference\": \"TEST-CANDIDATE-001\""
                )
        );

        assertTrue(
        result.rawResponse().contains(
                "\"score\":"
        )
);

assertTrue(
        result.rawResponse().contains(
                "\"recommendation\":"
        )
);
    }
}