package com.vaccine.repository;

import com.vaccine.entity.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class DataStore {
    private final Map<Long, Vaccine> vaccines = new ConcurrentHashMap<>();
    private final Map<Long, VaccineBatch> vaccineBatches = new ConcurrentHashMap<>();
    private final Map<Long, ColdChainRecord> coldChainRecords = new ConcurrentHashMap<>();
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Map<Long, VaccinationPoint> vaccinationPoints = new ConcurrentHashMap<>();
    private final Map<Long, Doctor> doctors = new ConcurrentHashMap<>();
    private final Map<Long, Appointment> appointments = new ConcurrentHashMap<>();
    private final Map<Long, VaccinationRecord> vaccinationRecords = new ConcurrentHashMap<>();
    private final Map<Long, AdverseReaction> adverseReactions = new ConcurrentHashMap<>();
    private final Map<Long, RecallNotice> recallNotices = new ConcurrentHashMap<>();
    private final Map<Long, DailyReport> dailyReports = new ConcurrentHashMap<>();

    private final AtomicLong vaccineIdGenerator = new AtomicLong(1);
    private final AtomicLong vaccineBatchIdGenerator = new AtomicLong(1);
    private final AtomicLong coldChainRecordIdGenerator = new AtomicLong(1);
    private final AtomicLong userIdGenerator = new AtomicLong(1);
    private final AtomicLong vaccinationPointIdGenerator = new AtomicLong(1);
    private final AtomicLong doctorIdGenerator = new AtomicLong(1);
    private final AtomicLong appointmentIdGenerator = new AtomicLong(1);
    private final AtomicLong vaccinationRecordIdGenerator = new AtomicLong(1);
    private final AtomicLong adverseReactionIdGenerator = new AtomicLong(1);
    private final AtomicLong recallNoticeIdGenerator = new AtomicLong(1);
    private final AtomicLong dailyReportIdGenerator = new AtomicLong(1);

    public Long generateVaccineId() {
        return vaccineIdGenerator.getAndIncrement();
    }

    public Long generateVaccineBatchId() {
        return vaccineBatchIdGenerator.getAndIncrement();
    }

    public Long generateColdChainRecordId() {
        return coldChainRecordIdGenerator.getAndIncrement();
    }

    public Long generateUserId() {
        return userIdGenerator.getAndIncrement();
    }

    public Long generateVaccinationPointId() {
        return vaccinationPointIdGenerator.getAndIncrement();
    }

    public Long generateDoctorId() {
        return doctorIdGenerator.getAndIncrement();
    }

    public Long generateAppointmentId() {
        return appointmentIdGenerator.getAndIncrement();
    }

    public Long generateVaccinationRecordId() {
        return vaccinationRecordIdGenerator.getAndIncrement();
    }

    public Long generateAdverseReactionId() {
        return adverseReactionIdGenerator.getAndIncrement();
    }

    public Long generateRecallNoticeId() {
        return recallNoticeIdGenerator.getAndIncrement();
    }

    public Long generateDailyReportId() {
        return dailyReportIdGenerator.getAndIncrement();
    }

    public Map<Long, Vaccine> getVaccines() {
        return vaccines;
    }

    public Map<Long, VaccineBatch> getVaccineBatches() {
        return vaccineBatches;
    }

    public Map<Long, ColdChainRecord> getColdChainRecords() {
        return coldChainRecords;
    }

    public Map<Long, User> getUsers() {
        return users;
    }

    public Map<Long, VaccinationPoint> getVaccinationPoints() {
        return vaccinationPoints;
    }

    public Map<Long, Doctor> getDoctors() {
        return doctors;
    }

    public Map<Long, Appointment> getAppointments() {
        return appointments;
    }

    public Map<Long, VaccinationRecord> getVaccinationRecords() {
        return vaccinationRecords;
    }

    public Map<Long, AdverseReaction> getAdverseReactions() {
        return adverseReactions;
    }

    public Map<Long, RecallNotice> getRecallNotices() {
        return recallNotices;
    }

    public Map<Long, DailyReport> getDailyReports() {
        return dailyReports;
    }
}
