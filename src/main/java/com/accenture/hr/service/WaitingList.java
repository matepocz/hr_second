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

    private KafkaTemplate<String, Long> kafkaTemplate;

    public WaitingList(SlotService slotService, KafkaTemplate<String, Long> template) {
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
        return removed;
    }

    public void callBack(E e) {
        ProducerRecord<String, Long> message = null;
        int currentLimit = slotService.getCurrentLimit();
        int placeInWaitingListToCall = slotService.getPlaceInWaitingListToCall();
        int position = 0;
        for (int i = 0; i < this.size(); i++) {
            if (e == this.get(i)) {
                position = i;
            }
        }
        if (position <= currentLimit || (position - currentLimit) % placeInWaitingListToCall == 0) {
            long id = (long) e;
            message = new ProducerRecord<>(TOPIC, id);
            kafkaTemplate.send(message);
        }
    }

}
