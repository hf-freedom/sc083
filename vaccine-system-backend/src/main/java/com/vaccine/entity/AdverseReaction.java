package com.vaccine.entity;

import com.vaccine.entity.enums.AdverseReactionStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdverseReaction {
    private Long id;
    private String reportNo;
    private Long userId;
    private String userName;
    private Long vaccinationRecordId;
    private Long vaccineId;
    private String vaccineName;
    private Long vaccineBatchId;
    private String batchNumber;
    private String symptoms;
    private String severity;
    private LocalDateTime reactionTime;
    private LocalDateTime reportTime;
    private AdverseReactionStatus status;
    private String treatmentMeasures;
    private String handler;
    private LocalDateTime resolveTime;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
