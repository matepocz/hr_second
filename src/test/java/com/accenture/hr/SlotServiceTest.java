package com.accenture.hr;

import com.accenture.hr.slots.SlotService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class SlotServiceTest {

    private SlotService slotService;
    private final Integer currentLimit = 10;
    private final List<Long> peopleInside = new ArrayList<>();
    private final List<Long> peopleWaiting = new ArrayList<>();

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

    @Test
    public void testStatus_isPeople_waiting() {
        for (int i = 1; i <= currentLimit; i++) {
            slotService.registerRequest((long) i);
        }
        slotService.registerRequest(21L);
        long userId = 21L;
        Assertions.assertEquals(1, slotService.statusRequest(userId));
    }

    @Test
    public void testStatus_isPeople_alreadyInBuilding() {
        long userId = 1L;
        slotService.registerRequest(userId);
        Assertions.assertEquals(-1, slotService.statusRequest(userId));
    }

    @Test
    public void testStatus_isPeople_notRegisteredYet() {
        Assertions.assertEquals(0, slotService.statusRequest(1L));
    }

    @Test
    public void testExit_isPeople_currentlyNotInBuilding() {
        peopleInside.add(1L);
        peopleInside.add(2L);
        slotService.exitRequest(4L);
        Assertions.assertEquals(2, peopleInside.size());
    }

    @Test
    public void testExit_exitUser() {
        peopleInside.add(1L);
        peopleInside.add(2L);
        slotService.exitRequest(1L);
        Assertions.assertEquals(1, peopleInside.size());
    }

    @Test
    public void testEntry_isFull(){
        peopleInside.addAll(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L));
        peopleWaiting.addAll(Arrays.asList(11L, 12L, 13L, 14L));
        Assertions.assertEquals(3, slotService.entryRequest(13L));
    }

    @Test
    public void testEntry_filterFakeEntry(){
        peopleInside.addAll(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L));
        peopleWaiting.addAll(Arrays.asList(9L, 10L, 11L));
        slotService.entryRequest(9L);
        Assertions.assertEquals(2, slotService.entryRequest(11L));
    }

    @Test
    public void testEntry(){
        peopleInside.addAll(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L));
        peopleWaiting.addAll(Arrays.asList(9L, 10L, 11L));
        slotService.entryRequest(10L);
        slotService.entryRequest(9L);
        Assertions.assertEquals(1, slotService.entryRequest(11L));
        Assertions.assertEquals(peopleInside.size(), currentLimit);
    }
}
