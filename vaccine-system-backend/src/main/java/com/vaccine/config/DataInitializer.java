package com.vaccine.config;

import com.vaccine.entity.*;
import com.vaccine.entity.enums.ColdChainStatus;
import com.vaccine.repository.DataStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    @Resource
    private DataStore dataStore;

    @Override
    public void run(String... args) {
        initVaccines();
        initVaccineBatches();
        initUsers();
        initVaccinationPoints();
        initDoctors();
    }

    private void initVaccines() {
        Vaccine vaccine1 = new Vaccine();
        vaccine1.setId(dataStore.generateVaccineId());
        vaccine1.setName("新冠疫苗");
        vaccine1.setManufacturer("国药集团");
        vaccine1.setDescription("新型冠状病毒灭活疫苗");
        vaccine1.setMinAge(18);
        vaccine1.setMaxAge(75);
        vaccine1.setMinIntervalDays(28);
        vaccine1.setRequiredDoses(2);
        vaccine1.setCreatedAt(LocalDateTime.now());
        vaccine1.setUpdatedAt(LocalDateTime.now());

        Vaccine vaccine2 = new Vaccine();
        vaccine2.setId(dataStore.generateVaccineId());
        vaccine2.setName("乙肝疫苗");
        vaccine2.setManufacturer("康泰生物");
        vaccine2.setDescription("重组乙型肝炎疫苗");
        vaccine2.setMinAge(0);
        vaccine2.setMaxAge(100);
        vaccine2.setMinIntervalDays(30);
        vaccine2.setRequiredDoses(3);
        vaccine2.setCreatedAt(LocalDateTime.now());
        vaccine2.setUpdatedAt(LocalDateTime.now());

        Vaccine vaccine3 = new Vaccine();
        vaccine3.setId(dataStore.generateVaccineId());
        vaccine3.setName("流感疫苗");
        vaccine3.setManufacturer("华兰生物");
        vaccine3.setDescription("季节性流感疫苗");
        vaccine3.setMinAge(6);
        vaccine3.setMaxAge(100);
        vaccine3.setMinIntervalDays(365);
        vaccine3.setRequiredDoses(1);
        vaccine3.setCreatedAt(LocalDateTime.now());
        vaccine3.setUpdatedAt(LocalDateTime.now());

        dataStore.getVaccines().put(vaccine1.getId(), vaccine1);
        dataStore.getVaccines().put(vaccine2.getId(), vaccine2);
        dataStore.getVaccines().put(vaccine3.getId(), vaccine3);
    }

    private void initVaccineBatches() {
        VaccineBatch batch1 = new VaccineBatch();
        batch1.setId(dataStore.generateVaccineBatchId());
        batch1.setBatchNumber("COVID-2024-001");
        batch1.setVaccineId(1L);
        batch1.setVaccineName("新冠疫苗");
        batch1.setTotalQuantity(500);
        batch1.setAvailableQuantity(450);
        batch1.setLockedQuantity(0);
        batch1.setProductionDate(LocalDate.of(2024, 1, 1));
        batch1.setExpirationDate(LocalDate.of(2026, 12, 31));
        batch1.setColdChainStatus(ColdChainStatus.NORMAL);
        batch1.setLastTemperatureCheck(LocalDateTime.now());
        batch1.setLastTemperature(3.5);
        batch1.setIsRecalled(false);
        batch1.setCreatedAt(LocalDateTime.now());
        batch1.setUpdatedAt(LocalDateTime.now());

        VaccineBatch batch2 = new VaccineBatch();
        batch2.setId(dataStore.generateVaccineBatchId());
        batch2.setBatchNumber("COVID-2024-002");
        batch2.setVaccineId(1L);
        batch2.setVaccineName("新冠疫苗");
        batch2.setTotalQuantity(300);
        batch2.setAvailableQuantity(280);
        batch2.setLockedQuantity(0);
        batch2.setProductionDate(LocalDate.of(2024, 6, 1));
        batch2.setExpirationDate(LocalDate.of(2027, 6, 30));
        batch2.setColdChainStatus(ColdChainStatus.NORMAL);
        batch2.setLastTemperatureCheck(LocalDateTime.now());
        batch2.setLastTemperature(3.2);
        batch2.setIsRecalled(false);
        batch2.setCreatedAt(LocalDateTime.now());
        batch2.setUpdatedAt(LocalDateTime.now());

        VaccineBatch batch3 = new VaccineBatch();
        batch3.setId(dataStore.generateVaccineBatchId());
        batch3.setBatchNumber("HBV-2024-001");
        batch3.setVaccineId(2L);
        batch3.setVaccineName("乙肝疫苗");
        batch3.setTotalQuantity(1000);
        batch3.setAvailableQuantity(950);
        batch3.setLockedQuantity(0);
        batch3.setProductionDate(LocalDate.of(2024, 3, 1));
        batch3.setExpirationDate(LocalDate.of(2028, 3, 31));
        batch3.setColdChainStatus(ColdChainStatus.NORMAL);
        batch3.setLastTemperatureCheck(LocalDateTime.now());
        batch3.setLastTemperature(3.8);
        batch3.setIsRecalled(false);
        batch3.setCreatedAt(LocalDateTime.now());
        batch3.setUpdatedAt(LocalDateTime.now());

        VaccineBatch batch4 = new VaccineBatch();
        batch4.setId(dataStore.generateVaccineBatchId());
        batch4.setBatchNumber("FLU-2024-001");
        batch4.setVaccineId(3L);
        batch4.setVaccineName("流感疫苗");
        batch4.setTotalQuantity(200);
        batch4.setAvailableQuantity(180);
        batch4.setLockedQuantity(0);
        batch4.setProductionDate(LocalDate.of(2024, 9, 1));
        batch4.setExpirationDate(LocalDate.of(2025, 9, 30));
        batch4.setColdChainStatus(ColdChainStatus.NORMAL);
        batch4.setLastTemperatureCheck(LocalDateTime.now());
        batch4.setLastTemperature(3.0);
        batch4.setIsRecalled(false);
        batch4.setCreatedAt(LocalDateTime.now());
        batch4.setUpdatedAt(LocalDateTime.now());

        dataStore.getVaccineBatches().put(batch1.getId(), batch1);
        dataStore.getVaccineBatches().put(batch2.getId(), batch2);
        dataStore.getVaccineBatches().put(batch3.getId(), batch3);
        dataStore.getVaccineBatches().put(batch4.getId(), batch4);
    }

    private void initUsers() {
        User user1 = new User();
        user1.setId(dataStore.generateUserId());
        user1.setName("张三");
        user1.setIdCard("110101199001011234");
        user1.setPhone("13800138001");
        user1.setBirthDate(LocalDate.of(1990, 1, 1));
        user1.setAge(35);
        user1.setGender("男");
        user1.setAddress("北京市朝阳区某小区");
        user1.setContraindications(Arrays.asList("青霉素过敏"));
        user1.setCreatedAt(LocalDateTime.now());
        user1.setUpdatedAt(LocalDateTime.now());

        User user2 = new User();
        user2.setId(dataStore.generateUserId());
        user2.setName("李四");
        user2.setIdCard("110101198505155678");
        user2.setPhone("13800138002");
        user2.setBirthDate(LocalDate.of(1985, 5, 15));
        user2.setAge(40);
        user2.setGender("女");
        user2.setAddress("北京市海淀区某小区");
        user2.setContraindications(Collections.emptyList());
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());

        User user3 = new User();
        user3.setId(dataStore.generateUserId());
        user3.setName("王五");
        user3.setIdCard("110101200012259012");
        user3.setPhone("13800138003");
        user3.setBirthDate(LocalDate.of(2000, 12, 25));
        user3.setAge(24);
        user3.setGender("男");
        user3.setAddress("北京市西城区某小区");
        user3.setContraindications(Arrays.asList("哮喘", "海鲜过敏"));
        user3.setCreatedAt(LocalDateTime.now());
        user3.setUpdatedAt(LocalDateTime.now());

        dataStore.getUsers().put(user1.getId(), user1);
        dataStore.getUsers().put(user2.getId(), user2);
        dataStore.getUsers().put(user3.getId(), user3);
    }

    private void initVaccinationPoints() {
        Map<String, Integer> timeSlots1 = new LinkedHashMap<>();
        timeSlots1.put("08:00-08:30", 20);
        timeSlots1.put("08:30-09:00", 20);
        timeSlots1.put("09:00-09:30", 20);
        timeSlots1.put("09:30-10:00", 20);
        timeSlots1.put("10:00-10:30", 20);
        timeSlots1.put("10:30-11:00", 20);
        timeSlots1.put("14:00-14:30", 20);
        timeSlots1.put("14:30-15:00", 20);
        timeSlots1.put("15:00-15:30", 20);
        timeSlots1.put("15:30-16:00", 20);
        timeSlots1.put("16:00-16:30", 20);
        timeSlots1.put("16:30-17:00", 20);

        VaccinationPoint point1 = new VaccinationPoint();
        point1.setId(dataStore.generateVaccinationPointId());
        point1.setName("朝阳区社区卫生服务中心");
        point1.setAddress("北京市朝阳区建国路88号");
        point1.setPhone("010-88888801");
        point1.setDescription("朝阳区最大的疫苗接种点");
        point1.setMaxCapacityPerTimeSlot(20);
        point1.setTimeSlots(timeSlots1);
        point1.setCreatedAt(LocalDateTime.now());
        point1.setUpdatedAt(LocalDateTime.now());
        point1.setInventory(Arrays.asList(
            dataStore.getVaccineBatches().get(1L),
            dataStore.getVaccineBatches().get(2L),
            dataStore.getVaccineBatches().get(3L),
            dataStore.getVaccineBatches().get(4L)
        ));

        Map<String, Integer> timeSlots2 = new LinkedHashMap<>();
        timeSlots2.put("08:00-09:00", 30);
        timeSlots2.put("09:00-10:00", 30);
        timeSlots2.put("10:00-11:00", 30);
        timeSlots2.put("14:00-15:00", 30);
        timeSlots2.put("15:00-16:00", 30);
        timeSlots2.put("16:00-17:00", 30);

        VaccinationPoint point2 = new VaccinationPoint();
        point2.setId(dataStore.generateVaccinationPointId());
        point2.setName("海淀区接种中心");
        point2.setAddress("北京市海淀区中关村大街100号");
        point2.setPhone("010-88888802");
        point2.setDescription("海淀区疫苗接种中心");
        point2.setMaxCapacityPerTimeSlot(30);
        point2.setTimeSlots(timeSlots2);
        point2.setCreatedAt(LocalDateTime.now());
        point2.setUpdatedAt(LocalDateTime.now());
        point2.setInventory(Arrays.asList(
            dataStore.getVaccineBatches().get(1L),
            dataStore.getVaccineBatches().get(3L),
            dataStore.getVaccineBatches().get(4L)
        ));

        dataStore.getVaccinationPoints().put(point1.getId(), point1);
        dataStore.getVaccinationPoints().put(point2.getId(), point2);
    }

    private void initDoctors() {
        Doctor doctor1 = new Doctor();
        doctor1.setId(dataStore.generateDoctorId());
        doctor1.setName("张医生");
        doctor1.setLicenseNumber("DOC001");
        doctor1.setSpecialization("预防接种");
        doctor1.setVaccinationPointId(1L);
        doctor1.setVaccinationPointName("朝阳区社区卫生服务中心");
        doctor1.setIsAvailable(true);
        doctor1.setCreatedAt(LocalDateTime.now());
        doctor1.setUpdatedAt(LocalDateTime.now());

        Doctor doctor2 = new Doctor();
        doctor2.setId(dataStore.generateDoctorId());
        doctor2.setName("李医生");
        doctor2.setLicenseNumber("DOC002");
        doctor2.setSpecialization("预防接种");
        doctor2.setVaccinationPointId(1L);
        doctor2.setVaccinationPointName("朝阳区社区卫生服务中心");
        doctor2.setIsAvailable(true);
        doctor2.setCreatedAt(LocalDateTime.now());
        doctor2.setUpdatedAt(LocalDateTime.now());

        Doctor doctor3 = new Doctor();
        doctor3.setId(dataStore.generateDoctorId());
        doctor3.setName("王医生");
        doctor3.setLicenseNumber("DOC003");
        doctor3.setSpecialization("预防接种");
        doctor3.setVaccinationPointId(2L);
        doctor3.setVaccinationPointName("海淀区接种中心");
        doctor3.setIsAvailable(true);
        doctor3.setCreatedAt(LocalDateTime.now());
        doctor3.setUpdatedAt(LocalDateTime.now());

        dataStore.getDoctors().put(doctor1.getId(), doctor1);
        dataStore.getDoctors().put(doctor2.getId(), doctor2);
        dataStore.getDoctors().put(doctor3.getId(), doctor3);
    }
}
