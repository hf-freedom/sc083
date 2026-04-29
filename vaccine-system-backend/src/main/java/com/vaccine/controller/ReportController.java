package com.vaccine.controller;

import com.vaccine.dto.ApiResponse;
import com.vaccine.entity.DailyReport;
import com.vaccine.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Resource
    private ReportService reportService;

    @PostMapping("/generate")
    public ApiResponse<DailyReport> generateDailyReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "1") Long vaccinationPointId) {
        try {
            LocalDate reportDate = (date != null) ? date : LocalDate.now();
            DailyReport report = reportService.generateDailyReport(reportDate, vaccinationPointId);
            return ApiResponse.success(report, "报表生成成功");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse<List<DailyReport>> getAllDailyReports() {
        return ApiResponse.success(reportService.getAllDailyReports());
    }

    @GetMapping("/{id}")
    public ApiResponse<DailyReport> getDailyReportById(@PathVariable Long id) {
        DailyReport report = reportService.getDailyReportById(id);
        if (report == null) {
            return ApiResponse.error("日报表不存在");
        }
        return ApiResponse.success(report);
    }

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> getDashboardData(
            @RequestParam(defaultValue = "1") Long vaccinationPointId) {
        return ApiResponse.success(reportService.getDashboardData(vaccinationPointId));
    }
}
