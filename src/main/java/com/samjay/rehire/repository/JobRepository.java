package com.samjay.rehire.repository;

import com.samjay.rehire.model.Job;
import com.samjay.rehire.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {

    List<Job> findByOrganization(Organization organization);

}