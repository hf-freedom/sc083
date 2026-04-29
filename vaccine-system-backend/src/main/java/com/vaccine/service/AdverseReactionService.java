package com.vaccine.service;

import com.vaccine.entity.*;
import com.vaccine.entity.enums.AdverseReactionStatus;
import com.vaccine.entity.enums.VaccinationStatus;
import com.vaccine.repository.DataStore;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdverseReactionService {

    @Resource
    private DataStore dataStore;

    public AdverseReaction reportAdverseReaction(Long vaccinationRecordId, String symptoms, 
                                                   String severity, String reactionTimeStr) {
        VaccinationRecord record = dataStore.getVaccinationRecords().get(vaccinationRecordId);
        if (record == null) {
            throw new RuntimeException("接种记录不存在");
        }

        User user = dataStore.getUsers().get(record.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        VaccineBatch batch = dataStore.getVaccineBatches().get(record.getVaccineBatchId());
        if (batch == null) {
            throw new RuntimeException("疫苗批次不存在");
        }

        LocalDateTime reactionTime = reactionTimeStr != null ? 
                LocalDateTime.parse(reactionTimeStr) : LocalDateTime.now();

        AdverseReaction reaction = new AdverseReaction();
        reaction.setId(dataStore.generateAdverseReactionId());
        reaction.setReportNo(generateReportNo());
        reaction.setUserId(user.getId());
        reaction.setUserName(user.getName());
        reaction.setVaccinationRecordId(vaccinationRecordId);
        reaction.setVaccineId(record.getVaccineId());
        reaction.setVaccineName(record.getVaccineName());
        reaction.setVaccineBatchId(batch.getId());
        reaction.setBatchNumber(batch.getBatchNumber());
        reaction.setSymptoms(symptoms);
        reaction.setSeverity(severity);
        reaction.setReactionTime(reactionTime);
        reaction.setReportTime(LocalDateTime.now());
        reaction.setStatus(AdverseReactionStatus.REPORTED);
        reaction.setCreatedAt(LocalDateTime.now());
        reaction.setUpdatedAt(LocalDateTime.now());

        record.setHasAdverseReaction(true);
        record.setStatus(VaccinationStatus.ADVERSE_REACTION_REPORTED);
        record.setUpdatedAt(LocalDateTime.now());

        dataStore.getAdverseReactions().put(reaction.getId(), reaction);

        return reaction;
    }

    public AdverseReaction updateAdverseReaction(Long id, AdverseReactionStatus status, 
                                                   String treatmentMeasures, String handler, String remark) {
        AdverseReaction reaction = dataStore.getAdverseReactions().get(id);
        if (reaction == null) {
            throw new RuntimeException("异常反应记录不存在");
        }

        if (status != null) {
            reaction.setStatus(status);
        }
        if (treatmentMeasures != null) {
            reaction.setTreatmentMeasures(treatmentMeasures);
        }
        if (handler != null) {
            reaction.setHandler(handler);
        }
        if (remark != null) {
            reaction.setRemark(remark);
        }
        if (AdverseReactionStatus.RESOLVED.equals(status)) {
            reaction.setResolveTime(LocalDateTime.now());
        }
        reaction.setUpdatedAt(LocalDateTime.now());

        return reaction;
    }

    public List<AdverseReaction> getAllAdverseReactions() {
        return dataStore.getAdverseReactions().values().stream()
                .sorted((r1, r2) -> r2.getReportTime().compareTo(r1.getReportTime()))
                .collect(Collectors.toList());
    }

    public AdverseReaction getAdverseReactionById(Long id) {
        return dataStore.getAdverseReactions().get(id);
    }

    public List<AdverseReaction> getAdverseReactionsByUserId(Long userId) {
        return dataStore.getAdverseReactions().values().stream()
                .filter(r -> userId.equals(r.getUserId()))
                .sorted((r1, r2) -> r2.getReportTime().compareTo(r1.getReportTime()))
                .collect(Collectors.toList());
    }

    public List<AdverseReaction> getAdverseReactionsByBatchId(Long batchId) {
        return dataStore.getAdverseReactions().values().stream()
                .filter(r -> batchId.equals(r.getVaccineBatchId()))
                .sorted((r1, r2) -> r2.getReportTime().compareTo(r1.getReportTime()))
                .collect(Collectors.toList());
    }

    private String generateReportNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "ADR" + LocalDateTime.now().format(formatter) + 
               String.format("%04d", (int)(Math.random() * 10000));
    }
}
