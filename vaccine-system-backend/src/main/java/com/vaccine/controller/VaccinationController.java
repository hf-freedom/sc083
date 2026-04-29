package com.vaccine.controller;

import com.vaccine.dto.ApiResponse;
import com.vaccine.entity.VaccinationRecord;
import com.vaccine.service.VaccinationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/vaccinations")
public class VaccinationController {

    @Resource
    private VaccinationService vaccinationService;

    @PostMapping("/start/{appointmentId}")
    public ApiResponse<VaccinationRecord> startVaccination(@PathVariable Long appointmentId) {
        try {
            VaccinationRecord record = vaccinationService.startVaccination(appointmentId);
            return ApiResponse.success(record, "开始接种");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/complete/{recordId}")
    public ApiResponse<VaccinationRecord> completeVaccination(
            @PathVariable Long recordId,
            @RequestParam(required = false, defaultValue = "上臂三角肌") String vaccinationSite) {
        try {
            VaccinationRecord record = vaccinationService.completeVaccination(recordId, vaccinationSite);
            return ApiResponse.success(record, "接种完成，已进入留观");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/complete-observation/{recordId}")
    public ApiResponse<VaccinationRecord> completeObservation(@PathVariable Long recordId) {
        try {
            VaccinationRecord record = vaccinationService.completeObservation(recordId);
            return ApiResponse.success(record, "留观完成，可以离开");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<VaccinationRecord> getVaccinationRecordById(@PathVariable Long id) {
        VaccinationRecord record = vaccinationService.getVaccinationRecordById(id);
        if (record == null) {
            return ApiResponse.error("接种记录不存在");
        }
        return ApiResponse.success(record);
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<VaccinationRecord>> getVaccinationRecordsByUserId(@PathVariable Long userId) {
        return ApiResponse.success(vaccinationService.getVaccinationRecordsByUserId(userId));
    }

    @GetMapping("/observing")
    public ApiResponse<List<VaccinationRecord>> getObservingRecords() {
        return ApiResponse.success(vaccinationService.getObservingRecords());
    }
}
