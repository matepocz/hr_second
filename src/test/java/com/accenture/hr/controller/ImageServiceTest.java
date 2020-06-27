package com.accenture.hr.controller;

import com.accenture.hr.service.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;

@SpringBootTest
public class ImageServiceTest {

    private ImageService imageService = new ImageService(1);

    @Test
    public void testImgEditing() {

        imageService.drawWorkSpace(715, 100, Color.BLACK);
        // imageService.testImgEditing(715, 100, 20, 10, Color.RED);
        // imageService.testImgEditing(765, 100, 20, 10, Color.BLACK);
    }
}
