package com.vaccine.service;

import com.vaccine.entity.*;
import com.vaccine.entity.enums.AppointmentStatus;
import com.vaccine.entity.enums.ColdChainStatus;
import com.vaccine.entity.enums.VaccinationStatus;
import com.vaccine.repository.DataStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VaccinationService {

    @Resource
    private DataStore dataStore;

    @Resource
    private AppointmentService appointmentService;

    @Value("${vaccine.observation.minutes:30}")
    private Integer observationMinutes;

    public VaccinationRecord startVaccination(Long appointmentId) {
        Appointment appointment = dataStore.getAppointments().get(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }

        if (!AppointmentStatus.CHECKED_IN.equals(appointment.getStatus())) {
            throw new RuntimeException("预约状态异常，未签到");
        }

        VaccineBatch batch = dataStore.getVaccineBatches().get(appointment.getVaccineBatchId());
        if (batch == null) {
            throw new RuntimeException("疫苗批次不存在");
        }

        if (batch.getIsRecalled()) {
            throw new RuntimeException("疫苗批次已被召回");
        }

        if (!ColdChainStatus.NORMAL.equals(batch.getColdChainStatus())) {
            throw new RuntimeException("疫苗冷链状态异常");
        }

        if (batch.getAvailableQuantity() <= 0 && batch.getLockedQuantity() <= 0) {
            throw new RuntimeException("疫苗库存不足");
        }

        if (batch.getLockedQuantity() > 0) {
            batch.setLockedQuantity(batch.getLockedQuantity() - 1);
            batch.setUpdatedAt(LocalDateTime.now());
        }

        VaccinationRecord record = new VaccinationRecord();
        record.setId(dataStore.generateVaccinationRecordId());
        record.setRecordNo(generateRecordNo());
        record.setUserId(appointment.getUserId());
        record.setUserName(appointment.getUserName());
        record.setVaccineId(appointment.getVaccineId());
        record.setVaccineName(appointment.getVaccineName());
        record.setVaccineBatchId(appointment.getVaccineBatchId());
        record.setBatchNumber(appointment.getBatchNumber());
        record.setVaccinationPointId(appointment.getVaccinationPointId());
        record.setVaccinationPointName(appointment.getVaccinationPointName());
        record.setDoctorId(appointment.getDoctorId());
        record.setDoctorName(appointment.getDoctorName());
        record.setAppointmentId(appointmentId);
        record.setDoseNumber(appointment.getDoseNumber());
        record.setStatus(VaccinationStatus.OBSERVING);
        record.setVaccinationTime(LocalDateTime.now());
        record.setObservationStartTime(LocalDateTime.now());
        record.setObservationMinutes(observationMinutes);
        record.setObservationEndTime(LocalDateTime.now().plusMinutes(observationMinutes));
        record.setVaccinationSite("上臂三角肌");
        record.setHasAdverseReaction(false);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());

        dataStore.getVaccinationRecords().put(record.getId(), record);

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setUpdatedAt(LocalDateTime.now());

        return record;
    }

    public VaccinationRecord completeVaccination(Long recordId, String vaccinationSite) {
        VaccinationRecord record = dataStore.getVaccinationRecords().get(recordId);
        if (record == null) {
            throw new RuntimeException("接种记录不存在");
        }

        if (!VaccinationStatus.IN_PROGRESS.equals(record.getStatus())) {
            throw new RuntimeException("接种状态异常");
        }

        VaccineBatch batch = dataStore.getVaccineBatches().get(record.getVaccineBatchId());
        if (batch != null && batch.getLockedQuantity() > 0) {
            batch.setLockedQuantity(batch.getLockedQuantity() - 1);
            batch.setUpdatedAt(LocalDateTime.now());
        }

        record.setStatus(VaccinationStatus.OBSERVING);
        record.setVaccinationTime(LocalDateTime.now());
        record.setObservationStartTime(LocalDateTime.now());
        record.setObservationMinutes(observationMinutes);
        record.setObservationEndTime(LocalDateTime.now().plusMinutes(observationMinutes));
        record.setVaccinationSite(vaccinationSite);
        record.setHasAdverseReaction(false);
        record.setUpdatedAt(LocalDateTime.now());

        Appointment appointment = dataStore.getAppointments().get(record.getAppointmentId());
        if (appointment != null) {
            appointment.setStatus(AppointmentStatus.COMPLETED);
            appointment.setUpdatedAt(LocalDateTime.now());
        }

        return record;
    }

    public VaccinationRecord completeObservation(Long recordId) {
        VaccinationRecord record = dataStore.getVaccinationRecords().get(recordId);
        if (record == null) {
            throw new RuntimeException("接种记录不存在");
        }

        if (!VaccinationStatus.OBSERVING.equals(record.getStatus())) {
            throw new RuntimeException("不是留观状态");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(record.getObservationEndTime())) {
            throw new RuntimeException("留观时间未满，还需等待 " + 
                java.time.Duration.between(now, record.getObservationEndTime()).toMinutes() + " 分钟");
        }

        record.setStatus(VaccinationStatus.OBSERVATION_COMPLETED);
        record.setUpdatedAt(LocalDateTime.now());

        return record;
    }

    public List<VaccinationRecord> getVaccinationRecordsByUserId(Long userId) {
        return dataStore.getVaccinationRecords().values().stream()
                .filter(r -> userId.equals(r.getUserId()))
                .sorted((r1, r2) -> r2.getVaccinationTime().compareTo(r1.getVaccinationTime()))
                .collect(Collectors.toList());
    }

    public VaccinationRecord getVaccinationRecordById(Long id) {
        return dataStore.getVaccinationRecords().get(id);
    }

    public List<VaccinationRecord> getObservingRecords() {
        return dataStore.getVaccinationRecords().values().stream()
                .filter(r -> VaccinationStatus.OBSERVING.equals(r.getStatus()))
                .collect(Collectors.toList());
    }

    private String generateRecordNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "REC" + LocalDateTime.now().format(formatter) + 
               String.format("%04d", (int)(Math.random() * 10000));
    }
}
