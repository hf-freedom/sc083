package com.vaccine.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RecallRequest {
    @NotNull(message = "疫苗批次ID不能为空")
    private Long batchId;
    
    @NotBlank(message = "召回原因不能为空")
    private String recallReason;
    
    @NotBlank(message = "召回级别不能为空")
    private String recallLevel;
}
