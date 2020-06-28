package com.accenture.hr.service;

import com.accenture.hr.config.KafkaTemplateConfig;
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

    //    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

   /* @Autowired
    private KafkaTemplateConfig kafkaTemplateConfig;
*/
    public WaitingList(KafkaTemplateConfig kafkaTemplateConfig) {
        this.kafkaTemplate = kafkaTemplateConfig.getTemplate();
    }

    @Override
    public boolean add(E e) {
        boolean result = super.add(e);
        callBack();
        return result;
    }

    @Override
    public boolean remove(Object index) {
        boolean removed = super.remove(index);
        callBack();
        return removed;
    }

    public void callBack() {
        System.out.println("==> List in callback");
        ProducerRecord<String, String> message = new ProducerRecord<>(TOPIC, " => List get call");
        kafkaTemplate.send(message);
    }

    @KafkaListener(id = "consumer-group-id-1", topics = TOPIC, groupId = "group-id")
    public void consume(String message) {
        log.info(String.format("#### -> Consumed message -> %s", message));
    }
}
