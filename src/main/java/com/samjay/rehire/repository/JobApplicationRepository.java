package com.samjay.rehire.repository;

import com.samjay.rehire.model.Job;
import com.samjay.rehire.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {

    List<JobApplication> findByJob(Job job);

}