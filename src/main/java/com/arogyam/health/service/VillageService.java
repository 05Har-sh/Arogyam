package com.arogyam.health.service;

import com.arogyam.health.entity.VillageEntity;
import com.arogyam.health.exception.ResourceNotFoundException;
import com.arogyam.health.repository.VillageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VillageService {

    @Autowired
    private VillageRepository villageRepository;

    public VillageEntity createVillage(VillageEntity village) {
        if (villageRepository.existsByNameAndDistrict(village.getName(), village.getDistrict())) {
            throw new IllegalArgumentException("Village already exists in this district");
        }
        return villageRepository.save(village);
    }

    @Transactional(readOnly = true)
    public VillageEntity getById(Long villageId) {
        return villageRepository.findById(villageId)
                .orElseThrow(() -> new ResourceNotFoundException("Village not found with id: " + villageId));
    }

    @Transactional(readOnly = true)
    public List<VillageEntity> getAll() {
        return villageRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<VillageEntity> getByDistrict(String district) {
        return villageRepository.findByDistrictOrderByName(district);
    }

    @Transactional(readOnly = true)
    public List<VillageEntity> getByState(String state) {
        return villageRepository.findByState(state);
    }

    public VillageEntity updateVillage(Long villageId, VillageEntity update) {
        VillageEntity village = getById(villageId);
        village.setName(update.getName());
        village.setDistrict(update.getDistrict());
        village.setState(update.getState());
        village.setLatitude(update.getLatitude());
        village.setLongitude(update.getLongitude());
        village.setPopulation(update.getPopulation());
        village.setPrimaryLanguage(update.getPrimaryLanguage());
        return villageRepository.save(village);
    }

    public void deleteVillage(Long villageId) {
        if (!villageRepository.existsById(villageId)) {
            throw new ResourceNotFoundException("Village not found with id: " + villageId);
        }
        villageRepository.deleteById(villageId);
    }
}
