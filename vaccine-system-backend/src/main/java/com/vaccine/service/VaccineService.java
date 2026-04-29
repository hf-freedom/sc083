package com.vaccine.service;

import com.vaccine.entity.Vaccine;
import com.vaccine.entity.VaccineBatch;
import com.vaccine.entity.enums.ColdChainStatus;
import com.vaccine.repository.DataStore;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VaccineService {

    @Resource
    private DataStore dataStore;

    public List<Vaccine> getAllVaccines() {
        return dataStore.getVaccines().values().stream().collect(Collectors.toList());
    }

    public Vaccine getVaccineById(Long id) {
        return dataStore.getVaccines().get(id);
    }

    public List<VaccineBatch> getAllVaccineBatches() {
        return dataStore.getVaccineBatches().values().stream().collect(Collectors.toList());
    }

    public VaccineBatch getVaccineBatchById(Long id) {
        return dataStore.getVaccineBatches().get(id);
    }

    public List<VaccineBatch> getVaccineBatchesByVaccineId(Long vaccineId) {
        return dataStore.getVaccineBatches().values().stream()
                .filter(batch -> vaccineId.equals(batch.getVaccineId()))
                .collect(Collectors.toList());
    }

    public List<VaccineBatch> getAvailableVaccineBatches(Long vaccineId) {
        return dataStore.getVaccineBatches().values().stream()
                .filter(batch -> vaccineId.equals(batch.getVaccineId()))
                .filter(batch -> batch.getAvailableQuantity() > 0)
                .filter(batch -> !batch.getIsRecalled())
                .filter(batch -> ColdChainStatus.NORMAL.equals(batch.getColdChainStatus()))
                .collect(Collectors.toList());
    }

    public boolean isBatchValid(Long batchId) {
        VaccineBatch batch = dataStore.getVaccineBatches().get(batchId);
        if (batch == null) {
            return false;
        }
        if (batch.getIsRecalled()) {
            return false;
        }
        if (!ColdChainStatus.NORMAL.equals(batch.getColdChainStatus())) {
            return false;
        }
        if (batch.getAvailableQuantity() <= 0) {
            return false;
        }
        return true;
    }
}
