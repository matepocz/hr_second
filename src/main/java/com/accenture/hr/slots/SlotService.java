package com.accenture.hr.slots;

import com.accenture.hr.enums.RegistrationStatus;
import com.accenture.hr.responses.RegisterResponse;
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
            registerResponse.setRegistrationStatus(RegistrationStatus.ALREADY_IN_BUILDING);
        } else if (peopleWaiting.contains(userId)) {
            log.error("User is already on waitinglist! UserId: {}", userId);
            registerResponse.setRegistrationStatus(RegistrationStatus.ALREADY_ON_WAITING_LIST);
        } else {
            if (peopleInside.size() < currentLimit) {
                peopleInside.add(userId);
                log.debug("User checked into building! UserId: {}", userId);
                registerResponse.setRegistrationStatus(RegistrationStatus.SUCCESS);
            } else {
                peopleWaiting.add(userId);
                log.debug("User placed on waitinglist! UserId: {}", userId);
                registerResponse.setRegistrationStatus(RegistrationStatus.TO_WAITING_LIST);
            }
        }
        return registerResponse;
    }

    public int statusRequest(long userId) {
        int positionInQueue = 0;
        if (peopleWaiting.contains(userId)) {
            positionInQueue = peopleWaiting.indexOf(userId) + 1;
        } else if (peopleInside.contains(userId)) {
            positionInQueue = -1;
            log.error("User is already in building! UserId: {}", userId);
        } else {
            log.error("User is not registered yet! UserId: {}", userId);
        }
        return positionInQueue;
    }

    public int entryRequest(long userId) {
        int freeCapacity = currentLimit - peopleInside.size();
        int positionInQueue = peopleWaiting.indexOf(userId);
        if (positionInQueue < freeCapacity) {
            peopleInside.add(userId);
            peopleWaiting.remove(userId);
            log.debug("User entered into building! UserId: {}", userId);
        }
        return statusRequest(userId);

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
