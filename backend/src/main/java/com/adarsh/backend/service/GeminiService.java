
package com.adarsh.backend.service;

import com.adarsh.backend.exception.AiServiceException;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final Client client;

    public GeminiService() {
        this.client = Client.builder()
                .apiKey(System.getenv("GEMINI_API_KEY"))
                .build();
    }


    public String getResponse(String message) {
        try {
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-3.8-flash",
                    message,
                    null
            );

            return response.text();

        } catch (Exception exception) {
            throw new AiServiceException(
                    "Unable to generate an AI response right now. Please try again later.",
                    exception
            );
        }

    }

}
