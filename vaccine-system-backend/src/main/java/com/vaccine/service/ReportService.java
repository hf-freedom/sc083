package com.vaccine.service;

import com.vaccine.entity.*;
import com.vaccine.entity.enums.AdverseReactionStatus;
import com.vaccine.entity.enums.AppointmentStatus;
import com.vaccine.repository.DataStore;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Resource
    private DataStore dataStore;

    public DailyReport generateDailyReport(LocalDate reportDate, Long vaccinationPointId) {
        VaccinationPoint point = dataStore.getVaccinationPoints().get(vaccinationPointId);
        if (point == null) {
            throw new RuntimeException("接种点不存在");
        }

        LocalDateTime startOfDay = reportDate.atStartOfDay();
        LocalDateTime endOfDay = reportDate.plusDays(1).atStartOfDay();

        List<Appointment> dayAppointments = dataStore.getAppointments().values().stream()
                .filter(a -> vaccinationPointId.equals(a.getVaccinationPointId()))
                .filter(a -> a.getAppointmentDate().equals(reportDate))
                .collect(Collectors.toList());

        int totalAppointments = dayAppointments.size();
        int checkedInCount = (int) dayAppointments.stream()
                .filter(a -> AppointmentStatus.CHECKED_IN.equals(a.getStatus()) || 
                              AppointmentStatus.COMPLETED.equals(a.getStatus()))
                .count();
        int completedCount = (int) dayAppointments.stream()
                .filter(a -> AppointmentStatus.COMPLETED.equals(a.getStatus()))
                .count();
        int cancelledCount = (int) dayAppointments.stream()
                .filter(a -> AppointmentStatus.CANCELLED.equals(a.getStatus()))
                .count();
        int timeoutCount = (int) dayAppointments.stream()
                .filter(a -> AppointmentStatus.TIMEOUT.equals(a.getStatus()))
                .count();

        List<VaccinationRecord> dayVaccinations = dataStore.getVaccinationRecords().values().stream()
                .filter(r -> vaccinationPointId.equals(r.getVaccinationPointId()))
                .filter(r -> r.getVaccinationTime() != null &&
                        r.getVaccinationTime().isAfter(startOfDay) &&
                        r.getVaccinationTime().isBefore(endOfDay))
                .collect(Collectors.toList());

        Map<String, Integer> vaccinationByVaccine = new LinkedHashMap<>();
        for (VaccinationRecord record : dayVaccinations) {
            String vaccineName = record.getVaccineName();
            vaccinationByVaccine.put(vaccineName, 
                    vaccinationByVaccine.getOrDefault(vaccineName, 0) + 1);
        }

        Map<String, Integer> inventoryByBatch = new LinkedHashMap<>();
        int totalInventory = 0;
        for (VaccineBatch batch : dataStore.getVaccineBatches().values()) {
            if (!batch.getIsRecalled()) {
                inventoryByBatch.put(batch.getBatchNumber() + "(" + batch.getVaccineName() + ")",
                        batch.getAvailableQuantity());
                totalInventory += batch.getAvailableQuantity();
            }
        }

        List<AdverseReaction> dayReactions = dataStore.getAdverseReactions().values().stream()
                .filter(r -> r.getReportTime().isAfter(startOfDay) &&
                        r.getReportTime().isBefore(endOfDay))
                .collect(Collectors.toList());

        int adverseReactionCount = dayReactions.size();
        Map<String, Integer> adverseReactionBySeverity = new LinkedHashMap<>();
        for (AdverseReaction reaction : dayReactions) {
            String severity = reaction.getSeverity();
            adverseReactionBySeverity.put(severity,
                    adverseReactionBySeverity.getOrDefault(severity, 0) + 1);
        }

        DailyReport report = new DailyReport();
        report.setId(dataStore.generateDailyReportId());
        report.setReportDate(reportDate);
        report.setVaccinationPointId(vaccinationPointId);
        report.setVaccinationPointName(point.getName());
        report.setTotalAppointments(totalAppointments);
        report.setCheckedInCount(checkedInCount);
        report.setCompletedCount(completedCount);
        report.setCancelledCount(cancelledCount);
        report.setTimeoutCount(timeoutCount);
        report.setVaccinationByVaccine(vaccinationByVaccine);
        report.setInventoryByBatch(inventoryByBatch);
        report.setTotalInventory(totalInventory);
        report.setAdverseReactionCount(adverseReactionCount);
        report.setAdverseReactionBySeverity(adverseReactionBySeverity);
        report.setGeneratedAt(LocalDateTime.now());

        dataStore.getDailyReports().put(report.getId(), report);

        return report;
    }

    public DailyReport generateDailyReportForToday(Long vaccinationPointId) {
        return generateDailyReport(LocalDate.now(), vaccinationPointId);
    }

    public List<DailyReport> getAllDailyReports() {
        return dataStore.getDailyReports().values().stream()
                .sorted((r1, r2) -> r2.getReportDate().compareTo(r1.getReportDate()))
                .collect(Collectors.toList());
    }

    public DailyReport getDailyReportById(Long id) {
        return dataStore.getDailyReports().get(id);
    }

    public Map<String, Object> getDashboardData(Long vaccinationPointId) {
        Map<String, Object> dashboard = new LinkedHashMap<>();

        int totalUsers = dataStore.getUsers().size();
        int totalVaccines = dataStore.getVaccines().size();
        int totalBatches = (int) dataStore.getVaccineBatches().values().stream()
                .filter(b -> !b.getIsRecalled())
                .count();

        int totalInventory = dataStore.getVaccineBatches().values().stream()
                .filter(b -> !b.getIsRecalled())
                .mapToInt(VaccineBatch::getAvailableQuantity)
                .sum();

        int pendingAppointments = (int) dataStore.getAppointments().values().stream()
                .filter(a -> AppointmentStatus.LOCKED.equals(a.getStatus()))
                .count();

        int todayVaccinations = (int) dataStore.getVaccinationRecords().values().stream()
                .filter(r -> r.getVaccinationTime() != null &&
                        r.getVaccinationTime().toLocalDate().equals(LocalDate.now()))
                .count();

        int pendingAdverseReactions = (int) dataStore.getAdverseReactions().values().stream()
                .filter(r -> AdverseReactionStatus.REPORTED.equals(r.getStatus()) ||
                        AdverseReactionStatus.IN_PROCESS.equals(r.getStatus()))
                .count();

        int observingCount = (int) dataStore.getVaccinationRecords().values().stream()
                .filter(r -> r.getObservationEndTime() != null &&
                        LocalDateTime.now().isBefore(r.getObservationEndTime()))
                .count();

        dashboard.put("totalUsers", totalUsers);
        dashboard.put("totalVaccines", totalVaccines);
        dashboard.put("totalBatches", totalBatches);
        dashboard.put("totalInventory", totalInventory);
        dashboard.put("pendingAppointments", pendingAppointments);
        dashboard.put("todayVaccinations", todayVaccinations);
        dashboard.put("pendingAdverseReactions", pendingAdverseReactions);
        dashboard.put("observingCount", observingCount);

        return dashboard;
    }
}
