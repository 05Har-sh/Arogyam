//package com.arogyam.health.service;
//
//import com.arogyam.health.entity.AlertEntity;
//import com.arogyam.health.entity.UserEntity;
//import com.arogyam.health.entity.VillageEntity;
//import com.arogyam.health.repository.AlertRepository;
//import com.arogyam.health.repository.UserRepository;
//import com.arogyam.health.repository.VillageRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//@Transactional
//public class AlertService {
//
//    @Autowired
//    private AlertRepository alertRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private VillageRepository villageRepository;
//
//    public AlertEntity createAlert(AlertEntity.AlertType type, String title, String message,
//                                   AlertEntity.AlertPriority priority, Long villageId, Long createdById) {
//        VillageEntity village = villageRepository.findById(villageId).orElse(null);
//        UserEntity creator = userRepository.findById(createdById)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        AlertEntity alert = new AlertEntity(type, title, message, priority, village, creator);
//        AlertEntity savedAlert = alertRepository.save(alert);
//
//        // Send notifications asynchronously
//        sendNotifications(savedAlert);
//
//        return savedAlert;
//    }
//
//    public AlertEntity createSystemAlert(AlertEntity.AlertType type, String title, String message,
//                                         AlertEntity.AlertPriority priority, Long village
