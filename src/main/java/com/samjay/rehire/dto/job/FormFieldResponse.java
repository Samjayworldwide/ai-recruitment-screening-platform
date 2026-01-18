package com.samjay.rehire.dto.job;

import com.samjay.rehire.constants.FieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormFieldResponse {

    private UUID id;

    private String label;

    private FieldType type;

    private String options;

    private boolean required;

    private int displayOrder;

}