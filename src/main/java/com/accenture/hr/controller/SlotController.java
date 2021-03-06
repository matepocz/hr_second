package com.accenture.hr.controller;

import com.accenture.hr.enums.Status;
import com.accenture.hr.responses.EntryResponse;
import com.accenture.hr.responses.ExitResponse;
import com.accenture.hr.responses.RegisterResponse;
import com.accenture.hr.responses.StatusResponse;
import com.accenture.hr.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/slots")
public class SlotController {

    private static final String OFFICE_LAYOUT_IMAGES_PATH = "src/main/resources/images/";

    private final SlotService slotService;
    private final int currentSafetyDistance;

    @Autowired
    public SlotController(SlotService slotService, int currentSafetyDistance) {
        this.slotService = slotService;
        this.currentSafetyDistance = currentSafetyDistance;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestParam Long userId) {
        RegisterResponse registerResponse = slotService.registerRequest(userId);
        Status status = registerResponse.getStatus();
        return status.equals(Status.REGISTERED) ?
                new ResponseEntity<>(registerResponse, HttpStatus.OK) :
                new ResponseEntity<>(registerResponse, HttpStatus.FORBIDDEN);
    }

    @GetMapping("/status")
    public ResponseEntity<StatusResponse> status(@RequestParam Long userId) {
        StatusResponse statusResponse = slotService.statusRequest(userId);
        Status status = statusResponse.getStatus();
        return status.equals(Status.NOT_REGISTERED) ?
                new ResponseEntity<>(statusResponse, HttpStatus.NOT_FOUND) :
                new ResponseEntity<>(statusResponse, HttpStatus.OK);
    }

    @PutMapping("/entry")
    public ResponseEntity<EntryResponse> entry(@RequestParam Long userId) {
        EntryResponse entryResponse = slotService.entryRequest(userId);
        Status status = entryResponse.getStatus();
        return status.equals(Status.SUCCESS) ?
                new ResponseEntity<>(entryResponse, HttpStatus.OK) :
                new ResponseEntity<>(entryResponse, HttpStatus.FORBIDDEN);
    }

    @PutMapping("/exit")
    public ResponseEntity<ExitResponse> exit(@RequestParam Long userId) {
        ExitResponse exitResponse = slotService.exitRequest(userId);
        Status status = exitResponse.getStatus();
        return status.equals(Status.NOT_REGISTERED) ?
                new ResponseEntity<>(exitResponse, HttpStatus.NOT_FOUND) :
                new ResponseEntity<>(exitResponse, HttpStatus.OK);
    }

    @GetMapping("/get-file/{fileName}")
    public ResponseEntity<byte[]> downloadFileFromLocal(@PathVariable String fileName) {
        byte[] data = null;
        try {
            File file = new File(OFFICE_LAYOUT_IMAGES_PATH + fileName);
            Path fileLocation = Paths.get(String.valueOf(file));
            data = Files.readAllBytes(fileLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(data);
    }

    @GetMapping("/layout")
    public ResponseEntity<byte[]> getCurrentLayout() {
        byte[] data = null;
        try {
            String tempLayoutPath = "src/main/resources/images/temp_layout_" + currentSafetyDistance + "_meter.jpg";
            File file = new File(tempLayoutPath);
            Path fileLocation = Paths.get(String.valueOf(file));
            data = Files.readAllBytes(fileLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(data);
    }
}
