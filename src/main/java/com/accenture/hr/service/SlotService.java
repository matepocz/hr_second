package com.accenture.hr.service;

import com.accenture.hr.enums.StatusList;
import com.accenture.hr.responses.EntryResponse;
import com.accenture.hr.responses.ExitResponse;
import com.accenture.hr.responses.RegisterResponse;
import com.accenture.hr.responses.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SlotService {

    private static final Logger log = LoggerFactory.getLogger(SlotService.class);

    private final Integer currentLimit;
    private final List<Long> peopleInside;
    private final List<Long> peopleWaiting;

    @Autowired
    public SlotService(Integer currentLimit, List<Long> peopleInside, List<Long> peopleWaiting) {
        this.currentLimit = currentLimit;
        this.peopleInside = peopleInside;
        this.peopleWaiting = peopleWaiting;
    }

    /**
     * Registers the user by the unique user ID
     * </p>
     * Puts the user to a waiting list if there
     * is no free capacity in the building.
     * @param userId the ID of the user
     * @return A RegisterResponse containing an enum with the result
     */
    public RegisterResponse registerRequest(Long userId) {
        RegisterResponse registerResponse = new RegisterResponse();
        if (peopleInside.contains(userId)) {
            log.error("User is already in building! UserId: {}", userId);
            registerResponse.setStatus(StatusList.ALREADY_IN_BUILDING);
        } else if (peopleWaiting.contains(userId)) {
            log.error("User is already on waitinglist! UserId: {}", userId);
            registerResponse.setStatus(StatusList.ALREADY_ON_WAITING_LIST);
        } else {
            putUserToCorrespondingList(userId, registerResponse);
        }
        return registerResponse;
    }

    private void putUserToCorrespondingList(Long userId, RegisterResponse registerResponse) {
        if (peopleInside.size() < currentLimit) {
            peopleInside.add(userId);
            log.debug("User checked into building! UserId: {}", userId);
            registerResponse.setStatus(StatusList.SUCCESS);
        } else {
            peopleWaiting.add(userId);
            log.debug("User placed on waitinglist! UserId: {}", userId);
            registerResponse.setStatus(StatusList.TO_WAITING_LIST);
        }
    }

    /**
     * Get the current status of a user by ID
     * @param userId the ID of the user
     * @return A StatusResponse containing an enum with
     * the result, also the user's position in the queue.
     */
    public StatusResponse statusRequest(long userId) {
        StatusResponse statusResponse = new StatusResponse();
        if (userNotFound(userId)) {
            statusResponse.setStatus(StatusList.NOT_REGISTERED);
            log.error("User is not registered yet! UserId: {}", userId);
            return statusResponse;
        }
        return makeStatusResponse(userId, statusResponse);
    }

    private StatusResponse makeStatusResponse(long userId, StatusResponse statusResponse) {
        if (peopleWaiting.contains(userId)) {
            int positionInQueue = peopleWaiting.indexOf(userId) + 1;
            statusResponse.setStatus(StatusList.ALREADY_ON_WAITING_LIST);
            statusResponse.setPositionInQueue(positionInQueue);
        } else if (peopleInside.contains(userId)) {
            statusResponse.setStatus(StatusList.ALREADY_IN_BUILDING);
            log.error("User is already in building! UserId: {}", userId);
        }
        return statusResponse;
    }

    /**
     * Attempts to check in the user by its ID
     * </p>
     * If there is no free capacity in the building
     * puts the user to the waiting queue
     * @param userId the ID of the user
     * @return An EntryResponse containing an enum with the result
     */
    public EntryResponse entryRequest(long userId) {
        EntryResponse entryResponse = new EntryResponse();
        if (userNotFound(userId)) {
            entryResponse.setStatus(StatusList.NOT_REGISTERED);
            log.error("User is not registered yet! UserId: {}", userId);
            return entryResponse;
        }
        return makeEntryResponse(userId, entryResponse);
    }

    private EntryResponse makeEntryResponse(long userId, EntryResponse entryResponse) {
        int freeCapacity = currentLimit - peopleInside.size();
        int positionInQueue = peopleWaiting.indexOf(userId);
        if (positionInQueue < freeCapacity) {
            peopleInside.add(userId);
            peopleWaiting.remove(userId);
            entryResponse.setStatus(StatusList.SUCCESS);
            log.debug("User entered into building! UserId: {}", userId);
        } else {
            entryResponse.setStatus(StatusList.FAIL);
            log.debug("No free capacity, User stays in waiting list! UserId: {}", userId);
        }
        return entryResponse;
    }

    /**
     * Attempts to check out the user from the building
     * by its user ID
     * @param userId the ID of the user
     * @return An ExitResponse containing an enum with the result
     */
    public ExitResponse exitRequest(long userId) {
        ExitResponse exitResponse = new ExitResponse();
        if (!peopleInside.contains(userId)) {
            log.error("User is currently not in the building! UserId: {}", userId);
            exitResponse.setStatus(StatusList.FAIL);
        } else {
            peopleInside.remove(userId);
            log.debug("User exited the building! UserId: {}", userId);
            exitResponse.setStatus(StatusList.SUCCESS);
        }
        return exitResponse;
    }

    /**
     * Checks whether the given user ID exists
     * @param userId the ID of the user
     * @return A boolean that shows the result
     */
    public boolean userNotFound(long userId) {
        return !peopleWaiting.contains(userId) && !peopleInside.contains(userId);
    }
}
