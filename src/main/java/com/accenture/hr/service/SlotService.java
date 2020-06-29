package com.accenture.hr.service;

import com.accenture.hr.enums.StatusList;
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

//    @Autowired
//    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public SlotService(int currentLimit, List<Long> peopleInside, WaitingList<Long> peopleWaiting,
                       List<Long> vipPersons, CoordinateService coordinateService) {
        this.currentLimit = currentLimit;
        this.peopleInside = peopleInside;
        this.peopleWaiting = peopleWaiting;
        this.vipPersons = vipPersons;
        this.coordinateService = coordinateService;
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
            registerResponse.setStatus(StatusList.ALREADY_IN_BUILDING);
        } else if (peopleWaiting.contains(userId)) {
            log.error("User is already on waitinglist! UserId: {}", userId);
            registerResponse.setStatus(StatusList.ALREADY_ON_WAITING_LIST);
        } else {
            putUserToCorrespondingList(userId, registerResponse);
        }
        return registerResponse;
    }

    private void putUserToCorrespondingList(long userId, RegisterResponse registerResponse) {
        if (peopleInside.size() < currentLimit || vipPersons.contains(userId)) {
            peopleInside.add(userId);
            log.debug("User checked into building! UserId: {}", userId);
            assignWorkSpaceToUser(userId);
            registerResponse.setStatus(StatusList.SUCCESS);
            registerResponse.setUrl(generateUrlForLayoutImage(userId));
            registerResponse.setPositionInQueue(0);
        } else {
            peopleWaiting.add(userId);
            log.debug("User placed on waitinglist! UserId: {}", userId);
            registerResponse.setStatus(StatusList.TO_WAITING_LIST);
            registerResponse.setPositionInQueue(peopleWaiting.size());
        }
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
        assignedWorkSpace.setStatus(WorkSpaceStatus.OCCUPIED);
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
     *
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
            assignWorkSpaceToUser(userId);
            entryResponse.setStatus(StatusList.SUCCESS);
            entryResponse.setUrl(generateUrlForLayoutImage(userId));
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
     *
     * @param userId the ID of the user
     * @return An ExitResponse containing an enum with the result
     */
    public ExitResponse exitRequest(long userId) {
        ExitResponse exitResponse = new ExitResponse();
        if (!peopleInside.contains(userId)) {
            log.error("User is currently not in the building! UserId: {}", userId);
            exitResponse.setStatus(StatusList.NOT_REGISTERED);
        } else {
            peopleInside.remove(userId);
            exitResponse.setStatus(StatusList.SUCCESS);
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

//    @KafkaListener(id = "consumer-group-id-1", topics = TOPIC, groupId = "group-id")
//    public void consume(String message) throws IOException {
//        log.info(String.format("#### -> Consumed message -> %s", message));
//    }


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
}
