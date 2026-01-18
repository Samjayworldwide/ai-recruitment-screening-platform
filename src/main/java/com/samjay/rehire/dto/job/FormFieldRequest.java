package com.samjay.rehire.dto.job;

import com.samjay.rehire.constants.FieldType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormFieldRequest {

    @NotBlank(message = "Field label is required")
    private String label;

    @NotNull(message = "Field type is required")
    private FieldType type;

    private String options;

    @NotNull(message = "Field required status must be specified")
    private Boolean required;

    private Integer displayOrder;

}