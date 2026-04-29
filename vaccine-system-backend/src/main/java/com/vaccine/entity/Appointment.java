package com.vaccine.entity;

import com.vaccine.entity.enums.AppointmentStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Appointment {
    private Long id;
    private String appointmentNo;
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
    private LocalDate appointmentDate;
    private String timeSlot;
    private Integer doseNumber;
    private AppointmentStatus status;
    private LocalDateTime checkInTime;
    private LocalDateTime lockedAt;
    private Integer timeoutMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
