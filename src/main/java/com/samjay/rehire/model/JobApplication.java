package com.samjay.rehire.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_applications")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    private String cvContent;

    @Column
    private String cvFilePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    private CandidateCart cart;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime submittedAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private boolean isEmailFinalized = false;

    @Column
    private LocalDateTime emailFinalizedAt;

    @OneToMany(mappedBy = "jobApplication", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FormResponse> responses = new ArrayList<>();

    public void addResponse(FormResponse response) {

        responses.add(response);

        response.setJobApplication(this);

    }

    public void markAsEmailFinalized() {

        this.isEmailFinalized = true;

        this.emailFinalizedAt = LocalDateTime.now();

    }
}