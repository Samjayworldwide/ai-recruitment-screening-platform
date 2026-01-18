package com.samjay.rehire.langchain4j.store;

import com.samjay.rehire.model.ChatMemory;
import com.samjay.rehire.repository.ChatMemoryRepository;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;

@Component
@RequiredArgsConstructor
@Transactional
public class PostgresChatMemoryStore implements ChatMemoryStore {

    private final ChatMemoryRepository chatMemoryRepository;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {

        return messagesFromJson(chatMemoryRepository.findByMemoryId(memoryId.toString())
                .map(ChatMemory::getMessages)
                .orElse("[]"));
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {

        chatMemoryRepository.findByMemoryId(memoryId.toString())
                .ifPresentOrElse(existing -> {

                            existing.setMessages(messagesToJson(messages));

                            chatMemoryRepository.save(existing);

                        },
                        () -> chatMemoryRepository.save(new ChatMemory(memoryId.toString(), messagesToJson(messages)))
                );

    }

    @Override
    public void deleteMessages(Object memoryId) {

        chatMemoryRepository.deleteByMemoryId(memoryId.toString());

    }
}