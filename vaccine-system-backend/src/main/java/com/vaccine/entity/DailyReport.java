package com.vaccine.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class DailyReport {
    private Long id;
    private LocalDate reportDate;
    private Long vaccinationPointId;
    private String vaccinationPointName;
    
    private Integer totalAppointments;
    private Integer checkedInCount;
    private Integer completedCount;
    private Integer cancelledCount;
    private Integer timeoutCount;
    
    private Map<String, Integer> vaccinationByVaccine;
    private Map<String, Integer> inventoryByBatch;
    private Integer totalInventory;
    
    private Integer adverseReactionCount;
    private Map<String, Integer> adverseReactionBySeverity;
    
    private LocalDateTime generatedAt;
}
