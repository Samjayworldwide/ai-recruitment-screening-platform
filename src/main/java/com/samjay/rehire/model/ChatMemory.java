package com.samjay.rehire.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_memory")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "memory_id", unique = true, nullable = false)
    private String memoryId;

    @Column(columnDefinition = "TEXT")
    private String messages;

    @Column(name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();

    public ChatMemory(String memoryId, String messages) {

        this.memoryId = memoryId;

        this.messages = messages;

    }
}