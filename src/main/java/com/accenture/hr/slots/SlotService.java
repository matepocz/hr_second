package com.accenture.hr.slots;

import com.accenture.hr.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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

    public void registerRequest(Long userId) {
        if (peopleInside.contains(userId)) {
            log.error("User is already in building! UserId: {}", userId);
        } else if (peopleWaiting.contains(userId)) {
            log.error("User is already on waitinglist! UserId: {}", userId);
        } else {
            if (peopleInside.size() < currentLimit) {
                peopleInside.add(userId);
                log.debug("User checked into building! UserId: {}", userId);
            } else {
                peopleWaiting.add(userId);
                log.debug("User placed on waitinglist! UserId: {}", userId);
            }
        }
    }

    public int statusRequest(long userId) {
        int positionInQueue = 0;
        if (peopleWaiting.contains(userId)) {
            positionInQueue = peopleWaiting.indexOf(userId);
        } else if (peopleInside.contains(userId)) {
            log.error("User is already in building! UserId: {}", userId);
        } else {
            log.error("User is not registered yet! UserId: {}", userId);
        }
        return positionInQueue;
    }

}
