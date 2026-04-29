package com.vaccine.service;

import com.vaccine.entity.*;
import com.vaccine.entity.enums.AppointmentStatus;
import com.vaccine.entity.enums.ColdChainStatus;
import com.vaccine.repository.DataStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Resource
    private DataStore dataStore;

    @Resource
    private VaccineService vaccineService;

    @Value("${vaccine.appointment.timeout-minutes:30}")
    private Integer timeoutMinutes;

    @Value("${vaccine.appointment.max-capacity-per-time-slot:20}")
    private Integer maxCapacityPerTimeSlot;

    public Appointment createAppointment(Long userId, Long vaccineId, Long vaccinationPointId, 
                                          LocalDate appointmentDate, String timeSlot, Integer doseNumber) {
        User user = dataStore.getUsers().get(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Vaccine vaccine = vaccineService.getVaccineById(vaccineId);
        if (vaccine == null) {
            throw new RuntimeException("疫苗不存在");
        }

        VaccinationPoint point = dataStore.getVaccinationPoints().get(vaccinationPointId);
        if (point == null) {
            throw new RuntimeException("接种点不存在");
        }

        validateAge(user, vaccine);
        validateDuplicateDose(user, vaccine, doseNumber);
        validateInterval(user, vaccine, doseNumber);
        validateTimeSlot(point, appointmentDate, timeSlot);
        validateInventory(vaccineId);

        VaccineBatch availableBatch = findAvailableBatch(vaccineId);
        if (availableBatch == null) {
            throw new RuntimeException("没有可用的疫苗批次");
        }

        Doctor availableDoctor = findAvailableDoctor(vaccinationPointId);
        if (availableDoctor == null) {
            throw new RuntimeException("没有可用的医生");
        }

        Appointment appointment = new Appointment();
        appointment.setId(dataStore.generateAppointmentId());
        appointment.setAppointmentNo(generateAppointmentNo());
        appointment.setUserId(userId);
        appointment.setUserName(user.getName());
        appointment.setVaccineId(vaccineId);
        appointment.setVaccineName(vaccine.getName());
        appointment.setVaccineBatchId(availableBatch.getId());
        appointment.setBatchNumber(availableBatch.getBatchNumber());
        appointment.setVaccinationPointId(vaccinationPointId);
        appointment.setVaccinationPointName(point.getName());
        appointment.setDoctorId(availableDoctor.getId());
        appointment.setDoctorName(availableDoctor.getName());
        appointment.setAppointmentDate(appointmentDate);
        appointment.setTimeSlot(timeSlot);
        appointment.setDoseNumber(doseNumber);
        appointment.setStatus(AppointmentStatus.LOCKED);
        appointment.setLockedAt(LocalDateTime.now());
        appointment.setTimeoutMinutes(timeoutMinutes);
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());

        lockInventory(availableBatch.getId());
        dataStore.getAppointments().put(appointment.getId(), appointment);

        return appointment;
    }

    private void validateAge(User user, Vaccine vaccine) {
        if (user.getAge() < vaccine.getMinAge() || user.getAge() > vaccine.getMaxAge()) {
            throw new RuntimeException("年龄不符合接种要求，要求年龄范围：" + vaccine.getMinAge() + "-" + vaccine.getMaxAge() + "岁");
        }
    }

    private void validateDuplicateDose(User user, Vaccine vaccine, Integer doseNumber) {
        List<Appointment> pendingAppointments = dataStore.getAppointments().values().stream()
                .filter(a -> user.getId().equals(a.getUserId()))
                .filter(a -> vaccine.getId().equals(a.getVaccineId()))
                .filter(a -> doseNumber.equals(a.getDoseNumber()))
                .filter(a -> AppointmentStatus.LOCKED.equals(a.getStatus()) ||
                              AppointmentStatus.CHECKED_IN.equals(a.getStatus()))
                .collect(Collectors.toList());

        if (!pendingAppointments.isEmpty()) {
            throw new RuntimeException("您已预约过第" + doseNumber + "剂" + vaccine.getName() + "，请勿重复预约");
        }

        List<VaccinationRecord> completedRecords = dataStore.getVaccinationRecords().values().stream()
                .filter(r -> user.getId().equals(r.getUserId()))
                .filter(r -> vaccine.getId().equals(r.getVaccineId()))
                .filter(r -> doseNumber.equals(r.getDoseNumber()))
                .collect(Collectors.toList());

        if (!completedRecords.isEmpty()) {
            throw new RuntimeException("您已完成第" + doseNumber + "剂" + vaccine.getName() + "的接种，请勿重复预约");
        }

        if (doseNumber > vaccine.getRequiredDoses()) {
            throw new RuntimeException(vaccine.getName() + "只需接种" + vaccine.getRequiredDoses() + "剂，您选择的第" + doseNumber + "剂超出范围");
        }
    }

    private void validateInterval(User user, Vaccine vaccine, Integer doseNumber) {
        if (doseNumber == 1) {
            return;
        }

        List<VaccinationRecord> records = dataStore.getVaccinationRecords().values().stream()
                .filter(r -> user.getId().equals(r.getUserId()))
                .filter(r -> vaccine.getId().equals(r.getVaccineId()))
                .collect(Collectors.toList());

        if (records.size() < doseNumber - 1) {
            throw new RuntimeException("尚未完成前序接种");
        }

        VaccinationRecord lastRecord = records.stream()
                .filter(r -> r.getDoseNumber().equals(doseNumber - 1))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("未找到上一剂接种记录"));

        long daysSinceLast = ChronoUnit.DAYS.between(lastRecord.getVaccinationTime().toLocalDate(), LocalDate.now());
        if (daysSinceLast < vaccine.getMinIntervalDays()) {
            throw new RuntimeException("接种间隔不足，需间隔至少" + vaccine.getMinIntervalDays() + "天");
        }
    }

    private void validateTimeSlot(VaccinationPoint point, LocalDate appointmentDate, String timeSlot) {
        if (!point.getTimeSlots().containsKey(timeSlot)) {
            throw new RuntimeException("该时间段不可预约");
        }

        long existingCount = dataStore.getAppointments().values().stream()
                .filter(a -> point.getId().equals(a.getVaccinationPointId()))
                .filter(a -> appointmentDate.equals(a.getAppointmentDate()))
                .filter(a -> timeSlot.equals(a.getTimeSlot()))
                .filter(a -> AppointmentStatus.LOCKED.equals(a.getStatus()) || 
                              AppointmentStatus.CHECKED_IN.equals(a.getStatus()))
                .count();

        int maxCapacity = point.getMaxCapacityPerTimeSlot() != null ? 
                point.getMaxCapacityPerTimeSlot() : maxCapacityPerTimeSlot;

        if (existingCount >= maxCapacity) {
            throw new RuntimeException("该时间段已约满");
        }
    }

    private void validateInventory(Long vaccineId) {
        List<VaccineBatch> availableBatches = vaccineService.getAvailableVaccineBatches(vaccineId);
        if (availableBatches.isEmpty()) {
            throw new RuntimeException("该疫苗库存不足");
        }
    }

    private VaccineBatch findAvailableBatch(Long vaccineId) {
        return vaccineService.getAvailableVaccineBatches(vaccineId).stream()
                .filter(batch -> batch.getAvailableQuantity() > 0)
                .findFirst()
                .orElse(null);
    }

    private Doctor findAvailableDoctor(Long vaccinationPointId) {
        return dataStore.getDoctors().values().stream()
                .filter(d -> vaccinationPointId.equals(d.getVaccinationPointId()))
                .filter(Doctor::getIsAvailable)
                .findFirst()
                .orElse(null);
    }

    private void lockInventory(Long batchId) {
        VaccineBatch batch = dataStore.getVaccineBatches().get(batchId);
        if (batch == null) {
            throw new RuntimeException("疫苗批次不存在");
        }
        if (batch.getAvailableQuantity() <= 0) {
            throw new RuntimeException("库存不足");
        }
        batch.setAvailableQuantity(batch.getAvailableQuantity() - 1);
        batch.setLockedQuantity(batch.getLockedQuantity() + 1);
        batch.setUpdatedAt(LocalDateTime.now());
    }

    public void releaseInventory(Long batchId) {
        VaccineBatch batch = dataStore.getVaccineBatches().get(batchId);
        if (batch == null) {
            return;
        }
        batch.setAvailableQuantity(batch.getAvailableQuantity() + 1);
        batch.setLockedQuantity(batch.getLockedQuantity() - 1);
        batch.setUpdatedAt(LocalDateTime.now());
    }

    public Appointment checkIn(Long appointmentId) {
        Appointment appointment = dataStore.getAppointments().get(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }

        if (!AppointmentStatus.LOCKED.equals(appointment.getStatus())) {
            throw new RuntimeException("预约状态异常，无法签到");
        }

        VaccineBatch batch = dataStore.getVaccineBatches().get(appointment.getVaccineBatchId());
        if (batch == null || !ColdChainStatus.NORMAL.equals(batch.getColdChainStatus())) {
            throw new RuntimeException("疫苗批次冷链异常，无法接种");
        }

        if (batch.getIsRecalled()) {
            throw new RuntimeException("疫苗批次已被召回，无法接种");
        }

        appointment.setStatus(AppointmentStatus.CHECKED_IN);
        appointment.setCheckInTime(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());

        return appointment;
    }

    public Appointment cancelAppointment(Long appointmentId) {
        Appointment appointment = dataStore.getAppointments().get(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }

        if (!AppointmentStatus.LOCKED.equals(appointment.getStatus())) {
            throw new RuntimeException("预约状态异常，无法取消");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setUpdatedAt(LocalDateTime.now());
        releaseInventory(appointment.getVaccineBatchId());

        return appointment;
    }

    public List<Appointment> getAppointmentsByUserId(Long userId) {
        return dataStore.getAppointments().values().stream()
                .filter(a -> userId.equals(a.getUserId()))
                .sorted((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public Appointment getAppointmentById(Long id) {
        return dataStore.getAppointments().get(id);
    }

    public List<Appointment> getTimeoutAppointments() {
        LocalDateTime now = LocalDateTime.now();
        return dataStore.getAppointments().values().stream()
                .filter(a -> AppointmentStatus.LOCKED.equals(a.getStatus()))
                .filter(a -> {
                    if (a.getLockedAt() == null) return false;
                    LocalDateTime timeoutTime = a.getLockedAt().plusMinutes(a.getTimeoutMinutes() != null ? a.getTimeoutMinutes() : timeoutMinutes);
                    return now.isAfter(timeoutTime);
                })
                .collect(Collectors.toList());
    }

    public void handleTimeoutAppointment(Long appointmentId) {
        Appointment appointment = dataStore.getAppointments().get(appointmentId);
        if (appointment == null) return;

        if (AppointmentStatus.LOCKED.equals(appointment.getStatus())) {
            appointment.setStatus(AppointmentStatus.TIMEOUT);
            appointment.setUpdatedAt(LocalDateTime.now());
            releaseInventory(appointment.getVaccineBatchId());
        }
    }

    private String generateAppointmentNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "APT" + LocalDateTime.now().format(formatter) + 
               String.format("%04d", (int)(Math.random() * 10000));
    }
}
