package com.accenture.hr.slots;

import com.accenture.hr.responses.EntryResponse;
import com.accenture.hr.responses.ExitResponse;
import com.accenture.hr.responses.RegisterResponse;
import com.accenture.hr.responses.StatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/slots")
public class SlotController {

    private SlotService slotService;

    @Autowired
    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @RequestMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestParam Long userId) {
        RegisterResponse registerResponse = slotService.registerRequest(userId);
        return new ResponseEntity<>(registerResponse, HttpStatus.OK);
    }

    @RequestMapping("/status")
    public ResponseEntity<StatusResponse> status(@RequestParam Long userId) {
        StatusResponse statusResponse = slotService.statusRequest(userId);
        return new ResponseEntity<>(statusResponse, HttpStatus.OK);
    }

    @RequestMapping("/entry")
    public ResponseEntity<EntryResponse> entry(@RequestParam Long userId) {
        EntryResponse entryResponse = slotService.entryRequest(userId);
        return new ResponseEntity<>(entryResponse, HttpStatus.OK);
    }

    @RequestMapping("/exit")
    public ResponseEntity<ExitResponse> exit(@RequestParam Long userId) {
        ExitResponse exitResponse = slotService.exitRequest(userId);
        return new ResponseEntity<>(exitResponse, HttpStatus.OK);
    }
}
