package com.samjay.rehire.model;

import com.samjay.rehire.constants.CartType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "candidate_carts")
public class CandidateCart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    private Job job;

    @Enumerated(EnumType.STRING)
    private CartType cartType;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    @Builder.Default
    private List<JobApplication> candidates = new ArrayList<>();

    public void addCandidate(JobApplication jobApplication) {

        candidates.add(jobApplication);

        jobApplication.setCart(this);

    }

    public void removeJobApplication(JobApplication application) {

        candidates.remove(application);

        application.setCart(null);

    }
}