package com.accenture.hr.controller;

import com.accenture.hr.enums.StatusList;
import com.accenture.hr.service.SlotService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

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
    public void testRegister_hasSpace_alreadyInside() {
        long userId = 1L;
        slotService.registerRequest(userId);
        StatusList actualStatus = slotService.registerRequest(userId).getStatus();
        Assertions.assertEquals(StatusList.ALREADY_IN_BUILDING, actualStatus);
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
    public void testRegister_putsOnWaitingList() {
        for (int i = 0; i < currentLimit + 1; i++) {
            slotService.registerRequest((long) i);
        }
        long userId = 22L;
        Assertions.assertEquals(currentLimit, peopleInside.size());
        Assertions.assertEquals(1, peopleWaiting.size());
        StatusList actualStatus = slotService.registerRequest(userId).getStatus();
        Assertions.assertEquals(StatusList.TO_WAITING_LIST, actualStatus);
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
    public void testRegister_alreadyOnWaitingList() {
        for (int i = 0; i < currentLimit; i++) {
            slotService.registerRequest((long) i);
        }
        long userId = 20L;
        slotService.registerRequest(userId);

        Assertions.assertEquals(currentLimit, peopleInside.size());
        Assertions.assertEquals(1, peopleWaiting.size());
        StatusList actualStatus = slotService.registerRequest(userId).getStatus();
        Assertions.assertEquals(StatusList.ALREADY_ON_WAITING_LIST, actualStatus);
    }

    @Test
    public void testStatus_isPeople_waiting() {
        for (int i = 1; i <= currentLimit; i++) {
            slotService.registerRequest((long) i);
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
        Assertions.assertEquals(StatusList.ALREADY_IN_BUILDING, slotService.statusRequest(userId).getStatus());
    }

    @Test
    public void testStatus_isPeople_notRegisteredYet() {
        Assertions.assertEquals(StatusList.NOT_REGISTERED, slotService.statusRequest(1L).getStatus());
    }

    @Test
    public void testEntry_isFull() {
        peopleInside.addAll(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L));
        peopleWaiting.addAll(Arrays.asList(11L, 12L, 13L, 14L));
        Assertions.assertEquals(StatusList.FAIL, slotService.entryRequest(13L).getStatus());
    }

    @Test
    public void testEntry_filterFakeEntry() {
        peopleInside.addAll(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L));
        peopleWaiting.addAll(Arrays.asList(9L, 10L, 11L));
        slotService.entryRequest(9L);
        Assertions.assertEquals(StatusList.FAIL, slotService.entryRequest(11L).getStatus());
    }

    @Test
    public void testEntry() {
        peopleInside.addAll(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L));
        peopleWaiting.addAll(Arrays.asList(9L, 10L, 11L));
        slotService.entryRequest(9L);
        Assertions.assertEquals(StatusList.SUCCESS, slotService.entryRequest(10L).getStatus());
        Assertions.assertEquals(peopleInside.size(), currentLimit);
    }

    @Test
    public void testExit_isPeople_currentlyNotInBuilding() {
        Assertions.assertEquals(StatusList.FAIL, slotService.exitRequest(4L).getStatus());
    }

    @Test
    public void testExit_exitUser() {
        peopleInside.add(1L);
        peopleInside.add(2L);
        Assertions.assertEquals(StatusList.SUCCESS, slotService.exitRequest(1L).getStatus());
        Assertions.assertEquals(1, peopleInside.size());
    }

    @Test
    public void testImgEditting(){
        slotService.testImgEditting();

    }
}
