package com.accenture.hr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class HrApplication {

    @Value("${maxCapacity}")
    private int maxCapacity;
    @Value("${currentAllowancePercent}")
    private int currentAllowancePercent;
    @Value("${currentSafetyDistance}")
    private int currentSafetyDistance;
    @Value("${vipPersons}")
    private String vipPersons;

    public static void main(String[] args) {
        SpringApplication.run(HrApplication.class, args);
    }

    @Bean
    public List<Long> peopleInside() {
        return new ArrayList<>();
    }

    @Bean
    public List<Long> peopleWaiting() {
        return new ArrayList<>();
    }

    @Bean
    public Integer currentLimit() {
        return (int) (maxCapacity * ((double) currentAllowancePercent / 100));
    }

    @Bean
    public List<Long> vipPersonList() {
        List<Long> vipPersonList = new ArrayList<>();
        String[] vipPersonsUserIds = vipPersons.split(",");
        for (String vipPersonsUserId : vipPersonsUserIds) {
            vipPersonList.add(Long.parseLong(vipPersonsUserId));
        }
        return vipPersonList;
    }

    @Bean
    public String currentSafetyDistance() {
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
