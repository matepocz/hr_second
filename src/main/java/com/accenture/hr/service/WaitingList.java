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

    private SlotService slotService;

    private KafkaTemplate<String, String> kafkaTemplate;

    public WaitingList(SlotService slotService, KafkaTemplate<String, String> template) {
        this.kafkaTemplate = template;
        this.slotService = slotService;
    }


    @Override
    public boolean add(E e) {
        boolean result = super.add(e);
        callBack(e);
        return result;
    }

    @Override
    public boolean remove(Object index) {
        boolean removed = super.remove(index);
        if (index != null) {
            callBack((E) index);
        }
        return removed;
    }

    public void callBack(E e) {
        ProducerRecord<String, String> message = null;

        int currentLimit = slotService.getCurrentLimit();
        int placeInWaitingListToCall = slotService.getPlaceInWaitingListToCall();
        for (int i = 0; i < this.size(); i++) {
            if (i <= currentLimit || (i - currentLimit) % placeInWaitingListToCall == 0) {
                String messageToConsumer = "User with id of: " + e + " getPossibility to Enter Into Building";
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
