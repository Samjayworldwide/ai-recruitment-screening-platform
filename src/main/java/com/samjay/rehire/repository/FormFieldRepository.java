package com.samjay.rehire.repository;

import com.samjay.rehire.model.FormField;
import com.samjay.rehire.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FormFieldRepository extends JpaRepository<FormField, UUID> {

    List<FormField> findByJobOrderByDisplayOrder(Job job);

}