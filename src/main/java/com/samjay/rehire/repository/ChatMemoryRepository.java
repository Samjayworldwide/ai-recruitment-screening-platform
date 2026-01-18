package com.samjay.rehire.repository;

import com.samjay.rehire.model.ChatMemory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatMemoryRepository extends JpaRepository<ChatMemory, UUID> {

    Optional<ChatMemory> findByMemoryId(String memoryId);

    void deleteByMemoryId(String memoryId);

}