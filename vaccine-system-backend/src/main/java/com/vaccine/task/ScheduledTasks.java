package com.vaccine.task;

import com.vaccine.entity.Appointment;
import com.vaccine.service.AppointmentService;
import com.vaccine.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Resource
    private AppointmentService appointmentService;

    @Resource
    private ReportService reportService;

    @Scheduled(fixedRate = 60000)
    public void checkTimeoutAppointments() {
        logger.info("开始检查超时预约...");
        
        List<Appointment> timeoutAppointments = appointmentService.getTimeoutAppointments();
        for (Appointment appointment : timeoutAppointments) {
            try {
                appointmentService.handleTimeoutAppointment(appointment.getId());
                logger.info("处理超时预约: {}, 预约号: {}", appointment.getId(), appointment.getAppointmentNo());
            } catch (Exception e) {
                logger.error("处理超时预约失败: {}", appointment.getId(), e);
            }
        }
        
        logger.info("超时预约检查完成，共处理 {} 个超时预约", timeoutAppointments.size());
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void generateDailyReport() {
        logger.info("开始生成每日报表...");
        
        try {
            reportService.generateDailyReportForToday(1L);
            logger.info("每日报表生成完成");
        } catch (Exception e) {
            logger.error("生成每日报表失败", e);
        }
    }
}
