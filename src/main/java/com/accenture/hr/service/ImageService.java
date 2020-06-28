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

    private final int currentSafetyDistance;

    public ImageService(int currentSafetyDistance) {
        this.currentSafetyDistance = currentSafetyDistance;
    }

    private static final String CURRENT_LAYOUT = "images/office_layout.jpg";
    private static final String TEMP_LAYOUT = "src/main/resources/images/temp_layout.jpg";
    private static final String IMAGE_PREFIX = "src/main/resources/images/";

    public void drawWorkSpace(int x, int y, Color color, long userId) {
        String tempFilePath = getImgFilePath(CURRENT_LAYOUT);
        BufferedImage newImg = makeBufferedImage(x, y, color, tempFilePath);
        try {
            if (userId != 0) {
                File outputFile = new File(IMAGE_PREFIX + userId + ".jpg");
                ImageIO.write(newImg, "jpg", outputFile);
            }
            drawWorkSpaceOnTempLayout(x, y, color);
        } catch (IOException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    private BufferedImage makeBufferedImage(int x, int y, Color color, String tempFilePath) {
        ImagePlus imagePlus = IJ.openImage(tempFilePath);
        ImageProcessor ip = imagePlus.getProcessor();
        ip.setColor(color);
        int radiusOfCircle = currentSafetyDistance * 10;
        int circlesOffset = (int) Math.ceil(radiusOfCircle / (double) 2);
        ip.drawOval(x - 3, y - 3, 6, 6);
        ip.drawOval(x - circlesOffset, y - circlesOffset, radiusOfCircle, radiusOfCircle);
        log.debug(String.format("Workspace colored to %s", color));

        return imagePlus.getBufferedImage();
    }

    private void drawWorkSpaceOnTempLayout(int x, int y, Color color) throws IOException {
        BufferedImage newImg = makeBufferedImage(x, y, color, TEMP_LAYOUT);
        File tempLayout = new File(TEMP_LAYOUT);
        ImageIO.write(newImg, "jpg", tempLayout);
    }

    public String getImgFilePath(String pathInResources) {
        Resource resource = new ClassPathResource(pathInResources);
        File imgFile = null;
        try {
            imgFile = resource.getFile();
        } catch (IOException e) {
            String warnMessage = "IO error, while loading file from path: '" + pathInResources + "'";
            log.warn(warnMessage);
        }
        return imgFile.getPath();
    }

    public static void deleteImageFileByUserId(long userId) {
        try {
            File file = new File(IMAGE_PREFIX + userId + ".jpg");
            file.delete();
        } catch (SecurityException | NullPointerException e) {
            log.debug(e.toString());
        }
    }
}
