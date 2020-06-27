package com.accenture.hr.config;

import com.accenture.hr.service.WaitingList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

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

    @Bean
    public List<Long> peopleInside() {
        return new ArrayList<>();
    }

    @Bean
    public WaitingList<Long> peopleWaiting() {
        return new WaitingList<>();
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

    @Bean
    public String fileNameByCurrentSafetyDistance() {
        String fileName = "src/main/resources/coordinates/";
        switch (currentSafetyDistance) {
            case 5:
                fileName += "coordinates_five_meters.txt";
                break;
            case 4:
                fileName += "coordinates_four_meters.txt";
                break;
            case 3:
                fileName += "coordinates_three_meters.txt";
                break;
            case 2:
                fileName += "coordinates_two_meters.txt";
                break;
            case 1:
                fileName += "coordinates_one_meter.txt";
                break;
            default:
        }
        return fileName;
    }
}
