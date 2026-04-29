package com.vaccine.controller;

import com.vaccine.dto.ApiResponse;
import com.vaccine.entity.Vaccine;
import com.vaccine.entity.VaccineBatch;
import com.vaccine.service.VaccineService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/vaccines")
public class VaccineController {

    @Resource
    private VaccineService vaccineService;

    @GetMapping
    public ApiResponse<List<Vaccine>> getAllVaccines() {
        return ApiResponse.success(vaccineService.getAllVaccines());
    }

    @GetMapping("/{id}")
    public ApiResponse<Vaccine> getVaccineById(@PathVariable Long id) {
        Vaccine vaccine = vaccineService.getVaccineById(id);
        if (vaccine == null) {
            return ApiResponse.error("疫苗不存在");
        }
        return ApiResponse.success(vaccine);
    }

    @GetMapping("/batches")
    public ApiResponse<List<VaccineBatch>> getAllVaccineBatches() {
        return ApiResponse.success(vaccineService.getAllVaccineBatches());
    }

    @GetMapping("/batches/{id}")
    public ApiResponse<VaccineBatch> getVaccineBatchById(@PathVariable Long id) {
        VaccineBatch batch = vaccineService.getVaccineBatchById(id);
        if (batch == null) {
            return ApiResponse.error("疫苗批次不存在");
        }
        return ApiResponse.success(batch);
    }

    @GetMapping("/{vaccineId}/batches")
    public ApiResponse<List<VaccineBatch>> getBatchesByVaccineId(@PathVariable Long vaccineId) {
        return ApiResponse.success(vaccineService.getVaccineBatchesByVaccineId(vaccineId));
    }

    @GetMapping("/{vaccineId}/available-batches")
    public ApiResponse<List<VaccineBatch>> getAvailableBatches(@PathVariable Long vaccineId) {
        return ApiResponse.success(vaccineService.getAvailableVaccineBatches(vaccineId));
    }

    @GetMapping("/batches/{id}/valid")
    public ApiResponse<Boolean> isBatchValid(@PathVariable Long id) {
        return ApiResponse.success(vaccineService.isBatchValid(id));
    }
}
