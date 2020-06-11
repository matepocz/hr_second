package com.accenture.hr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class SlotService {

    private final Integer currentLimit;
    private final Map<Long, LocalDateTime> peopleInside;
    private final Map<Long, LocalDateTime> peopleWaiting;

    @Autowired
    public SlotService(Integer currentLimit, Map<Long, LocalDateTime> peopleInside, Map<Long, LocalDateTime> peopleWaiting) {
        this.currentLimit = currentLimit;
        this.peopleInside = peopleInside;
        this.peopleWaiting = peopleWaiting;
    }

    public void registerRequest(Long userId) {
        if (peopleInside.containsKey(userId)) {
            log.error("User is already in building! UserId: " + userId);
        } else if (peopleWaiting.containsKey(userId)) {
            log.error("User is already on waitinglist! UserId: " + userId);
        } else {
            if (peopleInside.size() < currentLimit) {
                peopleInside.put(userId, LocalDateTime.now());
                log.debug("User checked into building! UserId: " + userId);
            } else {
                peopleWaiting.put(userId, LocalDateTime.now());
                log.debug("User placed on waitinglist! UserId: " + userId);
            }
        }
    }

}
