package com.vaccine.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecallNotice {
    private Long id;
    private String noticeNo;
    private Long vaccineBatchId;
    private String batchNumber;
    private String vaccineName;
    private String recallReason;
    private String recallLevel;
    private LocalDateTime issueTime;
    private List<Long> affectedUserIds;
    private Integer affectedCount;
    private Boolean isNotified;
    private LocalDateTime notifiedAt;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
