package org.project;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.*;

import static org.project.Utils.loadImage;
import static org.project.Utils.saveImage;

public class Encode {
    public static void main(String[] args) {
        System.out.println("LSB Steganography");
        String coverpath="D:\\Projects\\stegnography\\src\\main\\resources\\cover-images\\cover-10.jpg";
        String secretpath="D:\\Projects\\stegnography\\src\\main\\resources\\secret-images\\image_11.png";
        String stegopath="D:\\Projects\\stegnography\\src\\main\\resources\\outputs\\stego_image.png";
        ImagePlus coverImage=Utils.loadImage(coverpath,"Cover Image");
        ImagePlus secretImage=Utils.loadImage(secretpath,"Secret Image");
        if(coverImage==null || secretImage==null) {
            System.err.println("Error Unable to load images");
            return;
        }
        ImagePlus stegoImage=encodeImage(coverImage,secretImage);
        if(stegoImage!=null){
            Utils.saveImage(stegoImage,stegopath,"Stego Image");
        }
    }
    private static ImagePlus encodeImage(ImagePlus coverImage,ImagePlus secretImage) {
        System.out.println("Encoding secret image into cover image");
        ImageProcessor coverProcessor = coverImage.getProcessor().convertToRGB();
        ImageProcessor stegoProcessor = coverProcessor.duplicate();
        ImageProcessor secretProcessor = secretImage.getProcessor().convertToRGB();
        int coverWidth = coverImage.getWidth();
        int coverHeight = coverImage.getHeight();
        int secretWidth = secretImage.getWidth();
        int secretHeight = secretImage.getHeight();
        int coverX = 0, coverY = 0;
        storeDimension(stegoProcessor, coverX++, coverY, secretWidth);
        storeDimension(stegoProcessor, coverX++, coverY, secretHeight);
        for (int secretX = 0; secretX < secretWidth; secretX++) {
            for (int secretY = 0; secretY < secretHeight; secretY++) {
                if (coverX >= coverWidth) {
                    coverX = 0;
                    coverY += 1;
                }
                if (coverY >= coverHeight) {
                    System.out.println("cover image: " + coverWidth + "*" + coverHeight +
                            "  |  secret image: " + secretWidth + "*" + secretHeight);
                    System.err.println("Warning: Cover image is too small to hide the entire secret image!");
                    return null;
                }
                int coverPixel = stegoProcessor.getPixel(coverX, coverY);
                int secretPixel = secretProcessor.getPixel(secretX, secretY);
                Color coverColor = new Color(coverPixel);
                Color secretColor = new Color(secretPixel);
                int newRed = (coverColor.getRed() & 0b11110000) | (secretColor.getRed() >> 4);
                int newGreen = (coverColor.getGreen() & 0b11110000) | (secretColor.getGreen() >> 4);
                int newBlue = (coverColor.getBlue() & 0b11110000) | (secretColor.getBlue() >> 4);
                stegoProcessor.putPixel(coverX, coverY, new Color(newRed, newGreen, newBlue).getRGB());
                coverX += 5;
            }
        }
        System.out.println("Secret image embedded successfully!");
        return new ImagePlus("Stego Image", stegoProcessor);
    }
    private static void storeDimension(ImageProcessor processor, int x, int y, int value) {;
        int coverPixel = processor.getPixel(x, y);
        Color coverColor = new Color(coverPixel);
        int newRed = (value >> 8) & 0xFF;
        int newGreen = value & 0xFF;
        int newBlue = coverColor.getBlue();
        System.out.println("value :"+value+"  |  (x,y):("+x+","+y+")  |  (red,green,blue):("+newRed+","+newGreen+","+newBlue+")");
        System.out.println("storedimension :"+value);
        processor.putPixel(x, y, new Color(newRed, newGreen, newBlue).getRGB());
    }
}
