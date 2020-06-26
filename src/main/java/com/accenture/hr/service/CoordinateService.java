package com.accenture.hr.service;

import com.accenture.hr.enums.WorkSpaceStatus;
import com.accenture.hr.model.WorkSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CoordinateService {

    private final String fileName;
    private final List<WorkSpace> allowedWorkSpaces = new ArrayList<>();

    @Autowired
    public CoordinateService(String fileNameByCurrentSafetyDistance) {
        this.fileName = fileNameByCurrentSafetyDistance;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void getAllowedWorkSpacesFromFile() {
        ImageService imageService = new ImageService();
        Path filePath = Paths.get(fileName);
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                generateWorkSpace(imageService, line);
            }
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    private void generateWorkSpace(ImageService imageService, String line) {
        String[] coords = line.split(",");
        int x = Integer.parseInt(coords[0]);
        int y = Integer.parseInt(coords[1]);
        WorkSpace workSpace = new WorkSpace(x, y, imageService);
        allowedWorkSpaces.add(workSpace);
    }

    public WorkSpace getNextAvailableWorkSpace() {
        for (WorkSpace allowedWorkSpace : allowedWorkSpaces) {
            if (allowedWorkSpace.getStatus().equals(WorkSpaceStatus.FREE)) {
                return allowedWorkSpace;
            }
        }
        return null;
    }
}
