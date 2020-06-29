package com.accenture.hr.service;

import com.accenture.hr.enums.WorkSpaceStatus;
import com.accenture.hr.model.WorkSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CoordinateService {

    private static final Logger log = LoggerFactory.getLogger(CoordinateService.class);

    private static final int[] X_COORDINATES = Coordinates.X_COORDINATES_ORDERED;
    private static final int[] Y_COORDINATES = Coordinates.Y_COORDINATES_ORDERED;

    private final int currentSafetyDistance;
    private final List<WorkSpace> allowedWorkSpaces = new ArrayList<>();

    @Autowired
    public CoordinateService(int currentSafetyDistance) {
        this.currentSafetyDistance = currentSafetyDistance;
    }

    /**
     * Loads the workspaces to allowedWorkSpaces list
     * based on currentSafetyDistance
     */
    @EventListener(ApplicationReadyEvent.class)
    public void getAllowedWorkSpaces() {
        ImageService imageService = new ImageService(currentSafetyDistance);
        for (int i = 0; i < X_COORDINATES.length; i++) {
            int xCoordinate = X_COORDINATES[i];
            int yCoordinate = Y_COORDINATES[i];
            //TODO logic for 5, 4, 3, 2, 1 meters
            WorkSpace workSpace = new WorkSpace(xCoordinate, yCoordinate, imageService);
            allowedWorkSpaces.add(workSpace);
        }
    }

    /**
     * Get the next available workspace
     *
     * @return A WorkSpace, null if there is none
     */
    public WorkSpace getNextAvailableWorkSpace() {
        for (WorkSpace allowedWorkSpace : allowedWorkSpaces) {
            if (allowedWorkSpace.getStatus().equals(WorkSpaceStatus.FREE)) {
                return allowedWorkSpace;
            }
        }
        log.debug("No available workspace found!");
        return null;
    }

    /**
     * Get the workspace assigned to this user
     *
     * @param userId The ID of the user
     * @return A WorkSpace, null if there is no workspace assigned to this user
     */
    public WorkSpace getWorkSpaceByUserId(long userId) {
        for (WorkSpace workSpace : allowedWorkSpaces) {
            if (workSpace.getUserId() == userId) {
                return workSpace;
            }
        }
        log.debug("No assigned workspace found to this user! UserId: {}", userId);
        return null;
    }
}