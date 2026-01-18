package com.samjay.rehire.repository;

import com.samjay.rehire.model.FormResponse;
import com.samjay.rehire.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FormResponseRepository extends JpaRepository<FormResponse, UUID> {

    List<FormResponse> findByJobApplication(JobApplication jobApplication);

}