package com.vaccine.controller;

import com.vaccine.dto.ApiResponse;
import com.vaccine.entity.Doctor;
import com.vaccine.repository.DataStore;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Resource
    private DataStore dataStore;

    @GetMapping
    public ApiResponse<List<Doctor>> getAllDoctors() {
        return ApiResponse.success(dataStore.getDoctors().values().stream()
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ApiResponse<Doctor> getDoctorById(@PathVariable Long id) {
        Doctor doctor = dataStore.getDoctors().get(id);
        if (doctor == null) {
            return ApiResponse.error("医生不存在");
        }
        return ApiResponse.success(doctor);
    }

    @GetMapping("/point/{vaccinationPointId}")
    public ApiResponse<List<Doctor>> getDoctorsByPoint(@PathVariable Long vaccinationPointId) {
        return ApiResponse.success(dataStore.getDoctors().values().stream()
                .filter(d -> vaccinationPointId.equals(d.getVaccinationPointId()))
                .collect(Collectors.toList()));
    }
}
