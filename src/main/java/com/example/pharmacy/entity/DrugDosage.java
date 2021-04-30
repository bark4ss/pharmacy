package com.example.pharmacy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DrugDosage implements Entity {
    private Long dosageId;
    private Long drugId;
    private String dosage;

}
