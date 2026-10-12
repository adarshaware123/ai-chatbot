
package com.adarsh.backend.controller;

import com.adarsh.backend.dto.ChatMessage;
import com.adarsh.backend.dto.ChatRequest;
import com.adarsh.backend.dto.ChatResponse;
import com.adarsh.backend.service.ConversationService;
import com.adarsh.backend.service.GeminiService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final GeminiService geminiService;
    private final ConversationService conversationService;

    public ChatController(
            GeminiService geminiService,
            ConversationService conversationService) {
        this.geminiService = geminiService;
        this.conversationService = conversationService;
    }

    @PostMapping("/new")
    public Map<String, String> createConversation() {
        String conversationId =
                conversationService.createConversation();

        return Map.of("conversationId", conversationId);
    }

    @PostMapping
    public ChatResponse chat(
            @Valid @RequestBody ChatRequest request,
            @RequestParam String conversationId) {

        List<ChatMessage> history =
                conversationService.getMessages(conversationId);

        if (history == null) {
            throw new IllegalArgumentException("Conversation not found");
        }

        history.add(new ChatMessage("user", request.getMessage()));

        String aiReply = geminiService.getResponse(
                buildPrompt(history));

        history.add(new ChatMessage("assistant", aiReply));

        return new ChatResponse(request.getMessage(), aiReply);
    }

    private String buildPrompt(List<ChatMessage> history) {
        StringBuilder prompt = new StringBuilder();

        for (ChatMessage message : history) {
            prompt.append(message.getRole())
                    .append(": ")
                    .append(message.getContent())
                    .append("\n");
        }

        prompt.append("assistant: ");
        return prompt.toString();
    }

    @GetMapping("/{conversationId}/messages")
    public List<ChatMessage> getConversationHistory(
            @PathVariable String conversationId) {

        List<ChatMessage> history =
                conversationService.getMessages(conversationId);

        if (history == null) {
            throw new IllegalArgumentException("Conversation not found");
        }

        return history;
    }
}
