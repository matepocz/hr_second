package com.accenture.hr.service;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ImageService {
    private static final Logger log = LoggerFactory.getLogger(ImageService.class);

    private final int currentSafetyDistance = 1;

    private static final String ABS_CURRENT_LAYOUT = "/home/student/mentoring/students/accenture-contest/src/main/resources/images/office_layout.jpg";
    private static final String CURRENT_LAYOUT = "images/office_layout.jpg";
    private static final String TEMP_LAYOUT = "src/main/resources/images/temp_layout.jpg";

    public void drawWorkSpace(int x, int y, Color color) {
        int radiusOfCircle = currentSafetyDistance * 10;
        int circlesOffset = (int) Math.ceil(radiusOfCircle / (double) 2);
        String tempFilePath = getImgFile(CURRENT_LAYOUT);
        ImagePlus imagePlus = IJ.openImage(tempFilePath);
        ImageProcessor ip = imagePlus.getProcessor();
        ip.setColor(color);
        ip.drawOval(x - 3, y - 3, 6, 6);
        ip.drawOval(x - circlesOffset, y - circlesOffset, radiusOfCircle, radiusOfCircle);
        log.debug(String.format("Workspace colored to %s", color));

        BufferedImage newImg = imagePlus.getBufferedImage();

        try {
            File outputFile = new File(TEMP_LAYOUT);
            ImageIO.write(newImg, "jpg", outputFile);
        } catch (IOException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    public String getImgFile(String pathInResources) {
        Resource resource = new ClassPathResource(pathInResources);
        File imgFile = null;
        try {
            imgFile = resource.getFile();
        } catch (IOException e) {
            String warnMessage = "IO error, while loading file from path: '" + pathInResources + "'";
            e.printStackTrace();
        }
        return imgFile.getPath();
    }
}
