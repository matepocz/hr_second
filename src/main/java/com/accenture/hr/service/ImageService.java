package com.accenture.hr.service;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.process.ImageProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
@Component
public class ImageService {

    private static final String ORIGINAL_IMAGE_SOURCE = "src/main/resources/images/users_chair.jpg";

    public void testImgEditing(int x, int y, int width, int height, Color color) {
        ImagePlus imagePlus = IJ.openImage(ORIGINAL_IMAGE_SOURCE);
        ImageProcessor ip = imagePlus.getProcessor();
        ip.setColor(color);
//        ip.drawString("Booked chair", 760, 80);
        Roi roi = new Roi(x, y, width, height);
        ip.fill(roi);

        BufferedImage newImg = imagePlus.getBufferedImage();

        try {
            File outputFile = new File(ORIGINAL_IMAGE_SOURCE);
            ImageIO.write(newImg, "jpg", outputFile);
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }


    }
}
