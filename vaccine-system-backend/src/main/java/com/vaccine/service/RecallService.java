package com.vaccine.service;

import com.vaccine.entity.*;
import com.vaccine.entity.enums.ColdChainStatus;
import com.vaccine.repository.DataStore;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecallService {

    @Resource
    private DataStore dataStore;

    public RecallNotice recallBatch(Long batchId, String recallReason, String recallLevel) {
        VaccineBatch batch = dataStore.getVaccineBatches().get(batchId);
        if (batch == null) {
            throw new RuntimeException("疫苗批次不存在");
        }

        if (batch.getIsRecalled()) {
            throw new RuntimeException("该批次已被召回");
        }

        List<VaccinationRecord> affectedRecords = dataStore.getVaccinationRecords().values().stream()
                .filter(r -> batchId.equals(r.getVaccineBatchId()))
                .collect(Collectors.toList());

        List<Long> affectedUserIds = affectedRecords.stream()
                .map(VaccinationRecord::getUserId)
                .distinct()
                .collect(Collectors.toList());

        RecallNotice notice = new RecallNotice();
        notice.setId(dataStore.generateRecallNoticeId());
        notice.setNoticeNo(generateNoticeNo());
        notice.setVaccineBatchId(batchId);
        notice.setBatchNumber(batch.getBatchNumber());
        notice.setVaccineName(batch.getVaccineName());
        notice.setRecallReason(recallReason);
        notice.setRecallLevel(recallLevel);
        notice.setIssueTime(LocalDateTime.now());
        notice.setAffectedUserIds(affectedUserIds);
        notice.setAffectedCount(affectedUserIds.size());
        notice.setIsNotified(false);
        notice.setCreatedAt(LocalDateTime.now());
        notice.setUpdatedAt(LocalDateTime.now());

        batch.setIsRecalled(true);
        batch.setRecalledAt(LocalDateTime.now());
        batch.setColdChainStatus(ColdChainStatus.RECALLED);
        batch.setUpdatedAt(LocalDateTime.now());

        dataStore.getRecallNotices().put(notice.getId(), notice);

        return notice;
    }

    public RecallNotice markAsNotified(Long noticeId) {
        RecallNotice notice = dataStore.getRecallNotices().get(noticeId);
        if (notice == null) {
            throw new RuntimeException("召回通知不存在");
        }

        notice.setIsNotified(true);
        notice.setNotifiedAt(LocalDateTime.now());
        notice.setUpdatedAt(LocalDateTime.now());

        return notice;
    }

    public List<RecallNotice> getAllRecallNotices() {
        return dataStore.getRecallNotices().values().stream()
                .sorted((n1, n2) -> n2.getIssueTime().compareTo(n1.getIssueTime()))
                .collect(Collectors.toList());
    }

    public RecallNotice getRecallNoticeById(Long id) {
        return dataStore.getRecallNotices().get(id);
    }

    public List<User> getAffectedUsers(Long batchId) {
        List<VaccinationRecord> affectedRecords = dataStore.getVaccinationRecords().values().stream()
                .filter(r -> batchId.equals(r.getVaccineBatchId()))
                .collect(Collectors.toList());

        return affectedRecords.stream()
                .map(r -> dataStore.getUsers().get(r.getUserId()))
                .distinct()
                .collect(Collectors.toList());
    }

    private String generateNoticeNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "RCL" + LocalDateTime.now().format(formatter) + 
               String.format("%04d", (int)(Math.random() * 10000));
    }
}
