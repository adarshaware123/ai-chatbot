
package com.adarsh.backend.service;

import com.adarsh.backend.dto.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConversationService {

    private static final int MAX_HISTORY_MESSAGES = 20;
    private final Map<String, List<ChatMessage>> conversations =
            new ConcurrentHashMap<>();

    public String createConversation() {
        String conversationId = UUID.randomUUID().toString();
        conversations.put(conversationId,
                java.util.Collections.synchronizedList(new ArrayList<>()));
        return conversationId;
    }

    public List<ChatMessage> getMessages(String conversationId) {
        return conversations.get(conversationId);
    }

    public void addMessage(String conversationId, ChatMessage message) {
        List<ChatMessage> messages = conversations.get(conversationId);

        if (messages == null) {
            throw new IllegalArgumentException("Conversation not found");
        }

        messages.add(message);

        if (messages.size() > MAX_HISTORY_MESSAGES) {
            messages.remove(0);
        }
    }
}
