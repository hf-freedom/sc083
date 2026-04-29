package com.vaccine.entity;

import com.vaccine.entity.enums.VaccinationStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VaccinationRecord {
    private Long id;
    private String recordNo;
    private Long userId;
    private String userName;
    private Long vaccineId;
    private String vaccineName;
    private Long vaccineBatchId;
    private String batchNumber;
    private Long vaccinationPointId;
    private String vaccinationPointName;
    private Long doctorId;
    private String doctorName;
    private Long appointmentId;
    private Integer doseNumber;
    private VaccinationStatus status;
    private LocalDateTime vaccinationTime;
    private LocalDateTime observationStartTime;
    private LocalDateTime observationEndTime;
    private Integer observationMinutes;
    private Boolean hasAdverseReaction;
    private String vaccinationSite;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
