package com.vaccine.controller;

import com.vaccine.dto.ApiResponse;
import com.vaccine.entity.VaccinationPoint;
import com.vaccine.repository.DataStore;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vaccination-points")
public class VaccinationPointController {

    @Resource
    private DataStore dataStore;

    @GetMapping
    public ApiResponse<List<VaccinationPoint>> getAllVaccinationPoints() {
        return ApiResponse.success(dataStore.getVaccinationPoints().values().stream()
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ApiResponse<VaccinationPoint> getVaccinationPointById(@PathVariable Long id) {
        VaccinationPoint point = dataStore.getVaccinationPoints().get(id);
        if (point == null) {
            return ApiResponse.error("接种点不存在");
        }
        return ApiResponse.success(point);
    }
}
