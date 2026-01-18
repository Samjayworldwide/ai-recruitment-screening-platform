package com.samjay.rehire.langchain4j;

import com.samjay.rehire.langchain4j.store.PostgresChatMemoryStore;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.TokenCountEstimator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChain4jConfig {

    @Bean
    public TokenCountEstimator tokenCountEstimator() {

        return new JtokkitTokenCountEstimator();

    }

    @Bean
    ChatMemoryProvider chatMemoryProvider(TokenCountEstimator tokenCountEstimator, PostgresChatMemoryStore postgresChatMemoryStore) {
        return jobId -> TokenWindowChatMemory.builder()
                .id(jobId)
                .chatMemoryStore(postgresChatMemoryStore)
                .maxTokens(1000, tokenCountEstimator)
                .build();
    }
}