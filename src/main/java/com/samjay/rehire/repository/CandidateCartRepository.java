package com.samjay.rehire.repository;

import com.samjay.rehire.constants.CartType;
import com.samjay.rehire.model.CandidateCart;
import com.samjay.rehire.model.Job;
import com.samjay.rehire.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CandidateCartRepository extends JpaRepository<CandidateCart, UUID> {

    Optional<CandidateCart> findByOrganizationAndJobAndCartType(Organization organization, Job job, CartType cartType);

}