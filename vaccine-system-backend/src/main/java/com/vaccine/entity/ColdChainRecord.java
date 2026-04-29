package com.vaccine.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ColdChainRecord {
    private Long id;
    private Long vaccineBatchId;
    private String batchNumber;
    private Double temperature;
    private String location;
    private String operator;
    private LocalDateTime checkTime;
    private Boolean isNormal;
    private String remark;
}
