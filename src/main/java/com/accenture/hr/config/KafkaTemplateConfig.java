package com.accenture.hr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaTemplateConfig {

    private final KafkaTemplate<String, String>template;

    @Autowired
    public KafkaTemplateConfig(KafkaTemplate<String, String> template) {
        this.template = template;
    }

    public KafkaTemplate<String, String> getTemplate() {
        return template;
    }
}
