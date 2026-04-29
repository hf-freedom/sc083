package com.vaccine.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class VaccinationPoint {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String description;
    private Integer maxCapacityPerTimeSlot;
    private Map<String, Integer> timeSlots;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Doctor> doctors;
    private List<VaccineBatch> inventory;
}
