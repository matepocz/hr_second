package com.accenture.hr.service;

import com.accenture.hr.model.WorkSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoordinateService {

    private final String fileName;

    @Autowired
    public CoordinateService(String fileName) {
        this.fileName = fileName;
    }

    public List<WorkSpace> getAllowedWorkSpaces() {
        List<WorkSpace> allowedWorkSpaces = new ArrayList<>();
        ImageService imageService = new ImageService();
        Path filePath = Paths.get(fileName);
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null){
                String[] coords = line.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                WorkSpace workSpace = new WorkSpace(x, y, imageService);
                allowedWorkSpaces.add(workSpace);
            }
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }

        return allowedWorkSpaces;
    }
}
