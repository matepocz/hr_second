package com.accenture.hr.service;

import com.accenture.hr.enums.Status;
import com.accenture.hr.enums.WorkSpaceStatus;
import com.accenture.hr.model.WorkSpace;
import com.accenture.hr.responses.EntryResponse;
import com.accenture.hr.responses.ExitResponse;
import com.accenture.hr.responses.RegisterResponse;
import com.accenture.hr.responses.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Service
@Transactional
public class SlotService {
    public static final String TOPIC = "stand_in_Waiting_List";

    private static final Logger log = LoggerFactory.getLogger(SlotService.class);
    private static final String LINK_TO_GET_FILE = "/api/v1/slots/get-file/";

    @Value("${server.port}")
    private int port;

    private final int currentLimit;
    private final List<Long> peopleInside;
    private final WaitingList<Long> peopleWaiting;
    private final List<Long> vipPersons;
    private final CoordinateService coordinateService;
    private final int placeInWaitingListToCall;

    private Status status;


    @Autowired
    public SlotService(int currentLimit, List<Long> peopleInside,
                       List<Long> vipPersons, CoordinateService coordinateService, int getPlaceInWaitingListToCall, KafkaTemplate<String, Long> template) {
        this.currentLimit = currentLimit;
        this.peopleInside = peopleInside;
        this.peopleWaiting = new WaitingList<>(this, template);
        this.vipPersons = vipPersons;
        this.coordinateService = coordinateService;
        this.placeInWaitingListToCall = getPlaceInWaitingListToCall;
    }

    /**
     * Registers the user by the unique user ID
     * </p>
     * Puts the user to a waiting list if there
     * is no free capacity in the building.
     *
     * @param userId the ID of the user
     * @return A RegisterResponse containing an enum with the result
     */
    public RegisterResponse registerRequest(long userId) {
        RegisterResponse registerResponse = new RegisterResponse();
        if (peopleInside.contains(userId)) {
            log.error("User is already in building! UserId: {}", userId);
            registerResponse.setStatus(Status.ALREADY_IN_BUILDING);
        } else if (peopleWaiting.contains(userId)) {
            log.error("User is already on waitinglist! UserId: {}", userId);
            registerResponse.setStatus(Status.ALREADY_ON_WAITING_LIST);
        } else {
            putUserToCorrespondingList(userId, registerResponse);
        }
        return registerResponse;
    }

    private void putUserToCorrespondingList(long userId, RegisterResponse registerResponse) {
        if ((peopleInside.size() + peopleWaiting.size()) < currentLimit || vipPersons.contains(userId)) {
            putRegisteredUserToList(userId, registerResponse);
        } else {
            putUserToWaitingQueue(userId, registerResponse);
        }
    }

    private void putUserToWaitingQueue(long userId, RegisterResponse registerResponse) {
        this.status = Status.TO_WAITING_LIST;
        peopleWaiting.add(userId);
        log.debug("User placed on waitinglist! UserId: {}", userId);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        registerResponse.setStatus(this.status);
        int positionInQueue = (peopleInside.size() + peopleWaiting.size()) - currentLimit;
        registerResponse.setPositionInQueue(positionInQueue);
    }

    private void putRegisteredUserToList(long userId, RegisterResponse registerResponse) {
        peopleWaiting.add(userId);
        assignWorkSpaceToUser(userId);
        registerResponse.setStatus(Status.REGISTERED);
        registerResponse.setUrl(generateUrlForLayoutImage(userId));
        registerResponse.setPositionInQueue(0);
        log.debug("User registered to the office! UserId: {}", userId);
    }

    private URL generateUrlForLayoutImage(long userId) {
        String hostName = InetAddress.getLoopbackAddress().getHostAddress();
        String usersImage = LINK_TO_GET_FILE + userId + ".jpg";
        URL url = null;
        try {
            url = new URL("http", hostName, port, usersImage);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private void assignWorkSpaceToUser(long userId) {
        WorkSpace assignedWorkSpace = coordinateService.getNextAvailableWorkSpace();
        assignedWorkSpace.setUserId(userId);
        assignedWorkSpace.setStatus(WorkSpaceStatus.RESERVED);
        log.debug("WorkSpace assigned to UserId: {}", userId);
    }

    /**
     * Get the current status of a user by ID
     *
     * @param userId the ID of the user
     * @return A StatusResponse containing an enum with
     * the result, also the user's position in the queue.
     */
    public StatusResponse statusRequest(long userId) {
        StatusResponse statusResponse = new StatusResponse();
        if (userNotFound(userId)) {
            statusResponse.setStatus(Status.NOT_REGISTERED);
            log.error("User is not registered yet! UserId: {}", userId);
            return statusResponse;
        }
        return makeStatusResponse(userId, statusResponse);
    }

    private StatusResponse makeStatusResponse(long userId, StatusResponse statusResponse) {
        if (peopleWaiting.contains(userId)) {
            int positionInQueue = (peopleInside.size() + peopleWaiting.indexOf(userId) + 1) - currentLimit;
            statusResponse.setStatus(Status.ALREADY_ON_WAITING_LIST);
            statusResponse.setPositionInQueue(positionInQueue);
        } else if (peopleInside.contains(userId)) {
            statusResponse.setStatus(Status.ALREADY_IN_BUILDING);
            statusResponse.setPositionInQueue(0);
            log.error("User is already in building! UserId: {}", userId);
        }
        return statusResponse;
    }

    /**
     * Attempts to check in the user by its ID
     * </p>
     * If there is no free capacity in the building
     * puts the user to the waiting queue
     *
     * @param userId the ID of the user
     * @return An EntryResponse containing an enum with the result
     */
    public EntryResponse entryRequest(long userId) {
        EntryResponse entryResponse = new EntryResponse();
        if (userNotFound(userId)) {
            entryResponse.setStatus(Status.NOT_REGISTERED);
            log.error("User is not registered yet! UserId: {}", userId);
            return entryResponse;
        }
        return makeEntryResponse(userId, entryResponse);
    }

    private EntryResponse makeEntryResponse(Long userId, EntryResponse entryResponse) {
        int positionInQueue = peopleWaiting.indexOf(userId);
        boolean canEnter = (peopleInside.size() + positionInQueue) < currentLimit;

        if (canEnter || vipPersons.contains(userId)) {
            peopleInside.add(userId);
            peopleWaiting.remove(userId);
            WorkSpace workSpace = getWorkSpace(userId);
            workSpace.setStatus(WorkSpaceStatus.OCCUPIED);
            entryResponse.setStatus(Status.SUCCESS);
            entryResponse.setUrl(generateUrlForLayoutImage(userId));
            log.debug("User entered into building! UserId: {}", userId);
        } else {
            entryResponse.setStatus(Status.FAIL);
            log.debug("No free capacity, User stays in waiting list! UserId: {}", userId);
        }
        return entryResponse;
    }

    private WorkSpace getWorkSpace(long userId) {
        WorkSpace workSpace = coordinateService.getWorkSpaceByUserId(userId);
        if (workSpace == null) {
            assignWorkSpaceToUser(userId);
            workSpace = coordinateService.getWorkSpaceByUserId(userId);
        }
        return workSpace;
    }

    /**
     * Attempts to check out the user from the building
     * by its user ID
     *
     * @param userId the ID of the user
     * @return An ExitResponse containing an enum with the result
     */
    public ExitResponse exitRequest(Long userId) {
        ExitResponse exitResponse = new ExitResponse();
        if (!peopleInside.contains(userId)) {
            log.error("User is currently not in the building! UserId: {}", userId);
            exitResponse.setStatus(Status.NOT_REGISTERED);
        } else {
            peopleInside.remove(userId);
            exitResponse.setStatus(Status.SUCCESS);
            deAssignWorkSpace(userId);
            ImageService.deleteImageFileByUserId(userId);
            log.debug("User exited the building! UserId: {}", userId);
        }
        return exitResponse;
    }

    private void deAssignWorkSpace(long userId) {
        WorkSpace workSpace = coordinateService.getWorkSpaceByUserId(userId);
        workSpace.setStatus(WorkSpaceStatus.FREE);
    }

    /**
     * Checks whether the given user ID exists
     *
     * @param userId the ID of the user
     * @return A boolean that shows the result
     */
    public boolean userNotFound(long userId) {
        return !peopleWaiting.contains(userId) && !peopleInside.contains(userId) && !vipPersons.contains(userId);
    }

    @KafkaListener(id = "consumer-group-id-1", topics = TOPIC, groupId = "group-id")
    public void consume(long id) {
        this.status = Status.READY_TO_ENTER;
        String messageToConsumer = "User with id of: " + id + " getPossibility to Enter Into Building";
        log.info(messageToConsumer);
    }


    public List<Long> getPeopleInside() {
        return peopleInside;
    }

    public WaitingList<Long> getPeopleWaiting() {
        return peopleWaiting;
    }

    public List<Long> getVipPersons() {
        return vipPersons;
    }

    public int getCurrentLimit() {
        return currentLimit;
    }

    public int getPlaceInWaitingListToCall() {
        return placeInWaitingListToCall;
    }
}
