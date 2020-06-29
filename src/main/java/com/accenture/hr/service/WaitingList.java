package com.accenture.hr.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;

import static com.accenture.hr.service.SlotService.TOPIC;

public class WaitingList<E> extends ArrayList<E> {

    private static final Logger log = LoggerFactory.getLogger(WaitingList.class);

    @Autowired
    private SlotService slotService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private int currentLimit;
    private int placeInWaitingListToCall;

    public WaitingList() {
        this.currentLimit = slotService.getCurrentLimit();
        this.placeInWaitingListToCall = slotService.getPlaceInWaitingListToCall();
    }

    @Override
    public boolean add(E e) {
        boolean result = super.add(e);
        callBack(e);
        return result;
    }

   /* @Override
    public boolean remove(Object index) {
        boolean removed = super.remove(index);
        return removed;
    }*/

    public void callBack(E e) {
        ProducerRecord<String, String> message = null;
        for (int i = 0; i < this.size(); i++) {
            if (i <= this.currentLimit || (i - this.currentLimit) % this.placeInWaitingListToCall == 0) {
                String messageToConsumer = "User with id : " + e + " getPossibilityTo Enter IntoBuilding";
                message = new ProducerRecord<>(TOPIC, messageToConsumer);
                kafkaTemplate.send(message);
            }
        }
    }

    @KafkaListener(id = "consumer-group-id-1", topics = TOPIC, groupId = "group-id")
    public void consume(String message) {
        log.info(String.format("#### -> Consumed message -> %s", message));
    }
}
