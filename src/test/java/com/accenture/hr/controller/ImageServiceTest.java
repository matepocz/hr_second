package com.accenture.hr.controller;

import com.accenture.hr.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

public class ImageServiceTest {

    private ImageService imageService;

    @BeforeEach
    public void init() {
        this.imageService = new ImageService();
    }

    @Test
    public void testImgEditing() {
        imageService.testImgEditing(715, 100, 20, 10, Color.RED);
        imageService.testImgEditing(765, 100, 20, 10, Color.BLACK);
    }
}
