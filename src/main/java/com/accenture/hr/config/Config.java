package com.accenture.hr.config;

import com.accenture.hr.service.WaitingList;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties
public class Config {

    @Value("${maxCapacity}")
    private int maxCapacity;

    @Value("${currentAllowancePercent}")
    private int currentAllowancePercent;

    @Value("${currentSafetyDistance}")
    private int currentSafetyDistance;

    @Value("${vipPersons}")
    private String vipPersons;

    @Value("${placeInWaitingListToCall}")
    private int placeInWaitingListToCall;

    @Value("${bootstrap-servers}")
    private String bootstrapAddress;


    @Bean
    public List<Long> peopleInside() {
        return new ArrayList<>();
    }

    @Bean
    public Integer currentLimit() {
        return (int) (maxCapacity * ((double) currentAllowancePercent / 100));
    }

    @Bean
    public List<Long> vipPersons() {
        List<Long> vipPersonList = new ArrayList<>();
        String[] vipPersonsUserIds = vipPersons.split(",");
        for (String vipPersonId : vipPersonsUserIds) {
            vipPersonList.add(Long.parseLong(vipPersonId));
        }
        return vipPersonList;
    }

    @Bean
    public int currentSafetyDistance() {
        return currentSafetyDistance;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void copyOfficeLayoutToTempLayout() {
        Path copied = Paths.get("src/main/resources/images/temp_layout.jpg");
        Path originalPath = Paths.get("src/main/resources/images/office_layout.jpg");
        try {
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Bean
    public int getPlaceInWaitingListToCall() {
        return placeInWaitingListToCall;
    }

}
