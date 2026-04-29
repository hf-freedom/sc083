package com.vaccine.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Vaccine {
    private Long id;
    private String name;
    private String manufacturer;
    private String description;
    private Integer minAge;
    private Integer maxAge;
    private Integer minIntervalDays;
    private Integer requiredDoses;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<VaccineBatch> batches;
}
