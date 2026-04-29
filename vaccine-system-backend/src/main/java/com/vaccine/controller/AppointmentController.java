package com.vaccine.controller;

import com.vaccine.dto.ApiResponse;
import com.vaccine.dto.AppointmentRequest;
import com.vaccine.entity.Appointment;
import com.vaccine.service.AppointmentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Resource
    private AppointmentService appointmentService;

    @PostMapping
    public ApiResponse<Appointment> createAppointment(@Valid @RequestBody AppointmentRequest request) {
        try {
            Appointment appointment = appointmentService.createAppointment(
                    request.getUserId(),
                    request.getVaccineId(),
                    request.getVaccinationPointId(),
                    request.getAppointmentDate(),
                    request.getTimeSlot(),
                    request.getDoseNumber()
            );
            return ApiResponse.success(appointment, "预约成功");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<Appointment> getAppointmentById(@PathVariable Long id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment == null) {
            return ApiResponse.error("预约不存在");
        }
        return ApiResponse.success(appointment);
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Appointment>> getAppointmentsByUserId(@PathVariable Long userId) {
        return ApiResponse.success(appointmentService.getAppointmentsByUserId(userId));
    }

    @PostMapping("/{id}/check-in")
    public ApiResponse<Appointment> checkIn(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.checkIn(id);
            return ApiResponse.success(appointment, "签到成功");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Appointment> cancelAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.cancelAppointment(id);
            return ApiResponse.success(appointment, "取消预约成功");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
