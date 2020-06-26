package com.accenture.hr.service;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ImageService {
    private static final Logger log = LoggerFactory.getLogger(ImageService.class);

   // private static final String CURRENT_LAYOUT = "src/main/resources/images/office_layout.jpg";
    private static final String ABS_CURRENT_LAYOUT = "/home/student/mentoring/students/accenture-contest/src/main/resources/images/office_layout.jpg";
    private static final String CURRENT_LAYOUT = "images/office_layout.jpg";

    public void drawWorkSpace(int x, int y, Color color) {
        String tempFilePath = getImgFile(CURRENT_LAYOUT).getPath();
        ImagePlus imagePlus = IJ.openImage(tempFilePath);
        ImageProcessor ip = imagePlus.getProcessor();
        ip.setColor(color);
        ip.drawOval(x - 3, y - 3, 6, 6);
        ip.drawOval(x - 10, y - 10, 20, 20);

        BufferedImage newImg = imagePlus.getBufferedImage();

        try {
            File outputFile = new File(tempFilePath);
            ImageIO.write(newImg, "jpg", outputFile);
        } catch (IOException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    public File getImgFile(String pathInResources) {
        Resource resource = new ClassPathResource(pathInResources);
        File imgFile = null;
        try (InputStream inputStream = resource.getInputStream()) {
            imgFile = File.createTempFile("tempImg", ".jpg");
            FileUtils.copyInputStreamToFile(inputStream, imgFile);
        } catch (IOException e) {
            String warnMessage = "IO error, while loading file from path: '" + pathInResources + "'";
            log.warn(warnMessage);
        }
        return imgFile;
    }
}
