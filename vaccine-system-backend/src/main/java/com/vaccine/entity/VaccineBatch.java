package com.vaccine.entity;

import com.vaccine.entity.enums.ColdChainStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VaccineBatch {
    private Long id;
    private String batchNumber;
    private Long vaccineId;
    private String vaccineName;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private Integer lockedQuantity;
    private LocalDate productionDate;
    private LocalDate expirationDate;
    private ColdChainStatus coldChainStatus;
    private LocalDateTime lastTemperatureCheck;
    private Double lastTemperature;
    private Boolean isRecalled;
    private LocalDateTime recalledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ColdChainRecord> coldChainRecords;
}
