package com.samjay.rehire.langchain4j;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import dev.langchain4j.data.message.*;
import dev.langchain4j.model.TokenCountEstimator;

import java.util.stream.Collectors;

public class JtokkitTokenCountEstimator implements TokenCountEstimator {

    private final Encoding encoding;

    public JtokkitTokenCountEstimator() {

        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();

        this.encoding = registry.getEncoding(EncodingType.R50K_BASE);

    }

    @Override
    public int estimateTokenCountInText(String text) {

        return encoding.encode(text).size();

    }

    @Override
    public int estimateTokenCountInMessage(ChatMessage chatMessage) {

        String text = extractText(chatMessage);

        return estimateTokenCountInText(text);

    }

    @Override
    public int estimateTokenCountInMessages(Iterable<ChatMessage> messages) {

        int total = 0;

        for (ChatMessage msg : messages) {

            total += estimateTokenCountInMessage(msg);

        }

        return total;

    }

    private String extractText(ChatMessage message) {

        if (message instanceof AiMessage ai) {

            return ai.text();

        } else if (message instanceof UserMessage user) {

            return user.contents()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(" "));
        } else if (message instanceof SystemMessage system) {

            return system.text();

        } else if (message instanceof ToolExecutionResultMessage toolResult) {

            return toolResult.text();

        } else {

            return message.toString();

        }
    }
}