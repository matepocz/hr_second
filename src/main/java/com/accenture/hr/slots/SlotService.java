package com.accenture.hr.slots;

import com.accenture.hr.enums.StatusList;
import com.accenture.hr.responses.EnterResponse;
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

    public RegisterResponse registerRequest(Long userId) {
        RegisterResponse registerResponse = new RegisterResponse();
        if (peopleInside.contains(userId)) {
            log.error("User is already in building! UserId: {}", userId);
            registerResponse.setRegistrationStatus(StatusList.ALREADY_IN_BUILDING);
        } else if (peopleWaiting.contains(userId)) {
            log.error("User is already on waitinglist! UserId: {}", userId);
            registerResponse.setRegistrationStatus(StatusList.ALREADY_ON_WAITING_LIST);
        } else {
            if (peopleInside.size() < currentLimit) {
                peopleInside.add(userId);
                log.debug("User checked into building! UserId: {}", userId);
                registerResponse.setRegistrationStatus(StatusList.SUCCESS);
            } else {
                peopleWaiting.add(userId);
                log.debug("User placed on waitinglist! UserId: {}", userId);
                registerResponse.setRegistrationStatus(StatusList.TO_WAITING_LIST);
            }
        }
        return registerResponse;
    }

    public StatusResponse statusRequest(long userId) {
        StatusResponse statusResponse = new StatusResponse();
        if (peopleWaiting.contains(userId)) {
            int positionInQueue = peopleWaiting.indexOf(userId) + 1;
            statusResponse.setStatus(StatusList.ALREADY_ON_WAITING_LIST);
            statusResponse.setPositionInQueue(positionInQueue);
        } else if (peopleInside.contains(userId)) {
            statusResponse.setStatus(StatusList.ALREADY_IN_BUILDING);
            log.error("User is already in building! UserId: {}", userId);
        } else {
            statusResponse.setStatus(StatusList.NOT_REGISTERED);
            log.error("User is not registered yet! UserId: {}", userId);
        }
        return statusResponse;
    }

    public EnterResponse entryRequest(long userId) {
        int freeCapacity = currentLimit - peopleInside.size();
        int positionInQueue = peopleWaiting.indexOf(userId);
        if (positionInQueue < freeCapacity) {
            peopleInside.add(userId);
            peopleWaiting.remove(userId);
            log.debug("User entered into building! UserId: {}", userId);
        }
        // return statusRequest(userId);
        return null;

    }

    public void exitRequest(long userId) {
        if (!peopleInside.contains(userId)) {
            log.error("User is currently not in the building! UserId: {}", userId);
        } else {
            peopleInside.remove(userId);
            log.debug("User exited the building! UserId: {}", userId);
        }
    }
}
