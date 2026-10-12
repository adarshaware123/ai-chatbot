
package com.adarsh.backend.dto;

public class ChatResponse {

    private String userMessage;
    private String aiResponse;

    public ChatResponse(String userMessage, String aiResponse) {
        this.userMessage = userMessage;
        this.aiResponse = aiResponse;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getAiResponse() {
        return aiResponse;
    }
}
