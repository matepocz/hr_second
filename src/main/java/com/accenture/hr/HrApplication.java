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
}
