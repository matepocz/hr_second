package com.accenture.hr.service;

import com.accenture.hr.enums.WorkSpaceStatus;
import com.accenture.hr.model.WorkSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CoordinateService {

    private static final int[] X_COORDINATES = Coordinates.X_COORDINATES;
    private static final int[] Y_COORDINATES = Coordinates.Y_COORDINATES;

    private final int currentSafetyDistance;
    private final List<WorkSpace> allowedWorkSpaces = new ArrayList<>();

    @Autowired
    public CoordinateService(int currentSafetyDistance) {
        this.currentSafetyDistance = currentSafetyDistance;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void getAllowedWorkSpaces() {
        try {
            Runtime.getRuntime().exec("/home/mtp/IdeaProjects/Accenture/accenture-contest/startZookeeper.sh");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Runtime.getRuntime().exec("/home/mtp/IdeaProjects/Accenture/accenture-contest/startKafkaServer.sh");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageService imageService = new ImageService(currentSafetyDistance);
        for (int i = 0; i < X_COORDINATES.length; i++) {
            int xCoordinate = X_COORDINATES[i];
            int yCoordinate = Y_COORDINATES[i];
            //TODO logic for 5, 4, 3, 2, 1 meters
            WorkSpace workSpace = new WorkSpace(xCoordinate, yCoordinate, imageService);
            allowedWorkSpaces.add(workSpace);
        }
    }

//    @EventListener(ApplicationStartingEvent.class)
//    public void  runKafkaServer(){
//        boolean isWindows = System.getProperty("os.name")
//                .toLowerCase().startsWith("windows");
//        try {
//                    Runtime.getRuntime().exec(new String[]{"kafka/kafka_2.12-2.5.0/bin/zookeeper-server-start.sh", "kafka/kafka_2.12-2.5.0/config/zookeeper.properties"});
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//
//        try {
//                    Runtime.getRuntime().exec(new String[]{"kafka/kafka_2.12-2.5.0/bin/kafka-server-start.sh", "kafka/kafka_2.12-2.5.0/config/server.properties"});
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//    }

    public WorkSpace getNextAvailableWorkSpace() {
        for (WorkSpace allowedWorkSpace : allowedWorkSpaces) {
            if (allowedWorkSpace.getStatus().equals(WorkSpaceStatus.FREE)) {
                return allowedWorkSpace;
            }
        }
        return null;
    }

    public WorkSpace getWorkSpaceByUserId(long userId) {
        for (WorkSpace workSpace : allowedWorkSpaces) {
            if (workSpace.getUserId() == userId) {
                return workSpace;
            }
        }
        return null;
    }
}
