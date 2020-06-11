package com.accenture.hr.slots;

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
    public ResponseEntity<Void> register(@RequestParam Long userId) {
        slotService.registerRequest(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
