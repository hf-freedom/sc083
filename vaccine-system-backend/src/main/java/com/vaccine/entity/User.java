package com.vaccine.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class User {
    private Long id;
    private String name;
    private String idCard;
    private String phone;
    private LocalDate birthDate;
    private Integer age;
    private String gender;
    private String address;
    private List<String> contraindications;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<VaccinationRecord> vaccinationRecords;
}
