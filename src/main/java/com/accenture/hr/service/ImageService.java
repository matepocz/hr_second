package com.accenture.hr.service;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageService {

    private static final String CURRENT_LAYOUT = "src/main/resources/images/current_layout.jpg";

    public void drawWorkSpace(int x, int y, Color color) {
        ImagePlus imagePlus = IJ.openImage(CURRENT_LAYOUT);
        ImageProcessor ip = imagePlus.getProcessor();
        ip.setColor(color);
        ip.drawOval(x - 3, y - 3, 6, 6);
        ip.drawOval(x - 10, y - 10, 20, 20);

        BufferedImage newImg = imagePlus.getBufferedImage();

        try {
            File outputFile = new File(CURRENT_LAYOUT);
            ImageIO.write(newImg, "jpg", outputFile);
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
    }
}
