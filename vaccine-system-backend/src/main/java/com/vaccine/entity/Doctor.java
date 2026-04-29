package com.vaccine.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Doctor {
    private Long id;
    private String name;
    private String licenseNumber;
    private String specialization;
    private Long vaccinationPointId;
    private String vaccinationPointName;
    private Boolean isAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
