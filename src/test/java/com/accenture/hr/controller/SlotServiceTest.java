package com.accenture.hr.controller;

import com.accenture.hr.enums.Status;
import com.accenture.hr.service.SlotService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
public class SlotServiceTest {

    @Autowired
    private SlotService slotService;

    @BeforeEach
    private void init() {
        slotService.getPeopleWaiting().clear();
        slotService.getPeopleInside().clear();
    }

    @Test
    public void testRegister_hasSpace_registers() {
        long userId = 1L;
        slotService.registerRequest(userId);
        Assertions.assertEquals(1, slotService.getPeopleWaiting().size());
    }

    @Test
    public void testRegister_hasSpace_alreadyInside_logsError() {
        long userId = 1L;
        slotService.registerRequest(userId);
        slotService.registerRequest(userId);
        Assertions.assertEquals(1, slotService.getPeopleWaiting().size());
    }

    @Test
    public void testRegister_hasSpace_alreadyInside() {
        long userId = 1L;
        slotService.registerRequest(userId);
        slotService.entryRequest(userId);
        Status actualStatus = slotService.registerRequest(userId).getStatus();
        Assertions.assertEquals(Status.ALREADY_IN_BUILDING, actualStatus);
    }

    @Test
    public void testRegister_noSpace_putsOnWaitingList() {
        for (int i = 0; i < slotService.getCurrentLimit() + 1; i++) {
            slotService.registerRequest(i);
            slotService.entryRequest(i);
        }

        Assertions.assertEquals(slotService.getCurrentLimit(), slotService.getPeopleInside().size());
        Assertions.assertEquals(1, slotService.getPeopleWaiting().size());
    }

    @Test
    public void testRegister_noSpace_vipPersonCanRegister() {
        for (int i = 0; i < slotService.getCurrentLimit() + 1; i++) {
            slotService.registerRequest(i);
        }
        long vipUser = 999L;
        Assertions.assertEquals(Status.REGISTERED, slotService.registerRequest(vipUser).getStatus());
    }

    @Test
    public void testRegister_putsOnWaitingList() {
        for (int i = 0; i < slotService.getCurrentLimit() + 1; i++) {
            slotService.registerRequest(i);
            slotService.entryRequest(i);
        }
        long userId = 22L;
        Assertions.assertEquals(slotService.getCurrentLimit(), slotService.getPeopleInside().size());
        Assertions.assertEquals(1, slotService.getPeopleWaiting().size());
        Status actualStatus = slotService.registerRequest(userId).getStatus();
        Assertions.assertEquals(Status.TO_WAITING_LIST, actualStatus);
    }

    @Test
    public void testRegister_noSpace_alreadyOnWaitingList_logsError() {
        for (int i = 0; i < slotService.getCurrentLimit(); i++) {
            slotService.registerRequest(i);
            slotService.entryRequest(i);
        }
        slotService.registerRequest(20L);
        slotService.registerRequest(20L);

        Assertions.assertEquals(slotService.getCurrentLimit(), slotService.getPeopleInside().size());
        Assertions.assertEquals(1, slotService.getPeopleWaiting().size());
    }

    @Test
    public void testRegister_alreadyOnWaitingList() {
        for (int i = 0; i < slotService.getCurrentLimit(); i++) {
            slotService.registerRequest(i);
            slotService.entryRequest(i);
        }
        long userId = 20L;
        slotService.registerRequest(userId);

        Assertions.assertEquals(slotService.getCurrentLimit(), slotService.getPeopleInside().size());
        Assertions.assertEquals(1, slotService.getPeopleWaiting().size());
        Status actualStatus = slotService.registerRequest(userId).getStatus();
        Assertions.assertEquals(Status.ALREADY_ON_WAITING_LIST, actualStatus);
    }

    @Test
    public void testStatus_isPeople_waiting() {
        for (int i = 1; i <= slotService.getCurrentLimit(); i++) {
            slotService.registerRequest(i);
        }
        long userId = 21L;
        slotService.registerRequest(userId);
        int positionInQueue = slotService.statusRequest(userId).getPositionInQueue();
        Assertions.assertEquals(1, positionInQueue);
    }

    @Test
    public void testStatus_isPeople_alreadyInBuilding() {
        long userId = 1L;
        slotService.registerRequest(userId);
        slotService.entryRequest(userId);
        Assertions.assertEquals(Status.ALREADY_IN_BUILDING, slotService.statusRequest(userId).getStatus());
    }

    @Test
    public void testStatus_personFirstInQueue() {
        for (int i = 1; i <= slotService.getCurrentLimit(); i++) {
            slotService.registerRequest(i);
        }
        long userId = 500L;
        slotService.registerRequest(userId);
        Assertions.assertEquals(Status.ALREADY_ON_WAITING_LIST, slotService.statusRequest(userId).getStatus());
        Assertions.assertEquals(1, slotService.statusRequest(userId).getPositionInQueue());
    }

    @Test
    public void testStatus_isPeople_notRegisteredYet() {
        Assertions.assertEquals(Status.NOT_REGISTERED, slotService.statusRequest(1L).getStatus());
    }

    @Test
    public void testEntry_isFull() {
        slotService.getPeopleInside().addAll(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L));
        slotService.getPeopleWaiting().addAll(Arrays.asList(11L, 12L, 13L, 14L));
        Assertions.assertEquals(Status.FAIL, slotService.entryRequest(13L).getStatus());
    }

    @Test
    public void testEntry_filterFakeEntry() {
        slotService.getPeopleInside().addAll(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L));
        slotService.getPeopleWaiting().addAll(Arrays.asList(9L, 10L, 11L));
        slotService.entryRequest(9L);
        Assertions.assertEquals(Status.FAIL, slotService.entryRequest(11L).getStatus());
    }

    @Test
    public void testEntry() {
        slotService.getPeopleInside().add(1L);
        slotService.getPeopleWaiting().addAll(Arrays.asList(9L, 10L, 11L));
        Assertions.assertEquals(Status.SUCCESS, slotService.entryRequest(9L).getStatus());
        Assertions.assertEquals(Status.FAIL, slotService.entryRequest(10L).getStatus());
        Assertions.assertEquals(slotService.getPeopleInside().size(), slotService.getCurrentLimit());
    }

    @Test
    public void testEntry_vipPersonCanEnter() {
        slotService.getPeopleInside().add(1L);
        slotService.getPeopleWaiting().addAll(Arrays.asList(9L, 10L, 11L));
        long vipUser = 999L;
        Assertions.assertEquals(Status.SUCCESS, slotService.entryRequest(vipUser).getStatus());
    }

    @Test
    public void testExit_isPeople_currentlyNotInBuilding() {
        Assertions.assertEquals(Status.NOT_REGISTERED, slotService.exitRequest(4L).getStatus());
    }

    @Test
    public void testExit_exitUser() {
        slotService.getPeopleInside().add(1L);
        slotService.getPeopleInside().add(2L);
        Assertions.assertEquals(Status.SUCCESS, slotService.exitRequest(1L).getStatus());
        Assertions.assertEquals(1, slotService.getPeopleInside().size());
    }
}
