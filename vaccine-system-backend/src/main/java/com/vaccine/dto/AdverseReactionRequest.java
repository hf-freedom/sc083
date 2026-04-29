package com.vaccine.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AdverseReactionRequest {
    @NotNull(message = "接种记录ID不能为空")
    private Long vaccinationRecordId;
    
    @NotBlank(message = "症状描述不能为空")
    private String symptoms;
    
    @NotBlank(message = "严重程度不能为空")
    private String severity;
    
    private String reactionTime;
}
