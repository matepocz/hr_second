package com.accenture.hr.service;

import com.accenture.hr.enums.WorkSpaceStatus;
import com.accenture.hr.model.WorkSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CoordinateService {

    private static final Logger log = LoggerFactory.getLogger(CoordinateService.class);

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
            ProcessBuilder processBuilder = new ProcessBuilder("./startZookeeper.sh");
            processBuilder.inheritIO();
            Process process = processBuilder.start();

        } catch (IOException  e) {
            log.warn(e.getStackTrace().toString());
        }
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("./startKafkaServer.sh");
            processBuilder.inheritIO();
            Process process = processBuilder.start();

        }catch (IOException  e) {
            log.warn(e.getStackTrace().toString());
        }

        Path copied = Paths.get("src/main/resources/images/temp_layout.jpg");
        Path originalPath = Paths.get("src/main/resources/images/office_layout.jpg");
        try {
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO move to another file

        ImageService imageService = new ImageService(currentSafetyDistance);
        for (int i = 0; i < X_COORDINATES.length; i++) {
            int xCoordinate = X_COORDINATES[i];
            int yCoordinate = Y_COORDINATES[i];
            //TODO logic for 5, 4, 3, 2, 1 meters
            WorkSpace workSpace = new WorkSpace(xCoordinate, yCoordinate, imageService);
            allowedWorkSpaces.add(workSpace);
        }
    }


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
