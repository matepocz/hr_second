package com.accenture.hr.service;

import com.accenture.hr.enums.WorkSpaceStatus;
import com.accenture.hr.model.WorkSpace;
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
        ImageService imageService = new ImageService(currentSafetyDistance);
        for (int i = 0; i < X_COORDINATES.length; i++) {
            int xCoordinate = X_COORDINATES[i];
            int yCoordinate = Y_COORDINATES[i];
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

    public WorkSpace getWorkSpaceByUserId(Long userId) {
        for (WorkSpace workSpace : allowedWorkSpaces) {
            if (workSpace.getUserId() == userId) {
                return workSpace;
            }
        }
        return null;
    }
}
