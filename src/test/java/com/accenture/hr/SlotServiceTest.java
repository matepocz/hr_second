package com.accenture.hr;

import com.accenture.hr.slots.SlotService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class SlotServiceTest {

    private SlotService slotService;
    private Integer currentLimit = 10;
    private Map<Long, LocalDateTime> peopleInside = new HashMap<>();
    private Map<Long, LocalDateTime> peopleWaiting = new HashMap<>();

    @BeforeEach
    private void init() {
        slotService = new SlotService(currentLimit, peopleInside, peopleWaiting);
    }

    @Test
    public void testRegister_hasSpace_registers() {
        long userId = 1L;
        slotService.registerRequest(userId);

        Assertions.assertEquals(1, peopleInside.size());
    }

    @Test
    public void testRegister_hasSpace_alreadyInside_logsError() {
        long userId = 1L;
        slotService.registerRequest(userId);
        slotService.registerRequest(userId);

        Assertions.assertEquals(1, peopleInside.size());
    }

    @Test
    public void testRegister_noSpace_putsOnWaitingList() {
        for (int i = 0; i < currentLimit + 1; i++) {
            slotService.registerRequest((long) i);
        }

        Assertions.assertEquals(currentLimit, peopleInside.size());
        Assertions.assertEquals(1, peopleWaiting.size());
    }

    @Test
    public void testRegister_noSpace_alreadyOnWaitingList_logsError() {
        for (int i = 0; i < currentLimit; i++) {
            slotService.registerRequest((long) i);
        }
        slotService.registerRequest(20L);
        slotService.registerRequest(20L);

        Assertions.assertEquals(currentLimit, peopleInside.size());
        Assertions.assertEquals(1, peopleWaiting.size());
    }

}
