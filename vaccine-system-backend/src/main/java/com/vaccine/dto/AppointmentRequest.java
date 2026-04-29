package com.vaccine.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class AppointmentRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @NotNull(message = "疫苗ID不能为空")
    private Long vaccineId;
    
    @NotNull(message = "接种点ID不能为空")
    private Long vaccinationPointId;
    
    @NotNull(message = "预约日期不能为空")
    private LocalDate appointmentDate;
    
    @NotNull(message = "时间段不能为空")
    private String timeSlot;
    
    @NotNull(message = "剂次不能为空")
    private Integer doseNumber;
}
