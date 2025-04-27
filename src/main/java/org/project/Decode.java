package org.project;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.*;

import static org.project.Utils.loadImage;
import static org.project.Utils.saveImage;

public class Decode {
    public static void main(String[] args) {
        System.out.println("LSB Steganography");
        String stegopath="D:\\Projects\\stegnography\\src\\main\\resources\\outputs\\stego_image.png";
        String extractedpath = "D:\\Projects\\stegnography\\src\\main\\resources\\outputs\\extracted.jpg";
        ImagePlus extractedImage=decodeImage(Utils.loadImage(stegopath,"stego image"));
        if(extractedImage!=null) {
            Utils.saveImage(extractedImage,extractedpath,"Extracted Secret Image");
        }
    }
    private static int extractDimension(ImageProcessor processor, int x, int y) {
        int pixel = processor.getPixel(x, y);
        Color color = new Color(pixel);
        int dimension=((color.getRed() & 0xFF) << 8) | (color.getGreen() & 0xFF);
        System.out.println("extractdimension :"+dimension);
        return dimension;
    }
    private static ImagePlus decodeImage(ImagePlus stegoImage) {
        System.out.println("Decoding hidden image...");

        if (stegoImage == null) {
            System.err.println("Error: Stego image not found!");
            return null;
        }
        ImageProcessor stegoProcessor = stegoImage.getProcessor();
        int coverX = 0, coverY = 0;
        int secretWidth = extractDimension(stegoProcessor, coverX++, coverY);
        int secretHeight = extractDimension(stegoProcessor, coverX++, coverY);
        System.out.println("Extracted secret image dimensions: " + secretWidth + "x" + secretHeight);
        if (secretWidth <= 0 || secretHeight <= 0) {
            System.err.println("Error: Invalid extracted dimensions!");
            return null;
        }
        ImageProcessor extractedProcessor = stegoProcessor.createProcessor(secretWidth, secretHeight);
        for (int secretX = 0; secretX < secretWidth; secretX++) {
            for (int secretY = 0; secretY < secretHeight; secretY++) {
                if (coverX >= stegoImage.getWidth()) {
                    coverX = 0;
                    coverY += 1;
                }
                if (coverY >= stegoImage.getHeight()) {
                    System.err.println("Warning: Could not fully extract the image!");
                    return null;
                }
                int stegoPixel = stegoProcessor.getPixel(coverX, coverY);
                Color stegoColor = new Color(stegoPixel);
                int extractedRed = (stegoColor.getRed() & 0b00001111) << 4;
                int extractedGreen = (stegoColor.getGreen() & 0b00001111) << 4;
                int extractedBlue = (stegoColor.getBlue() & 0b00001111) << 4;
                extractedProcessor.putPixel(secretX, secretY, new Color(extractedRed, extractedGreen, extractedBlue).getRGB());
                coverX += 5;
            }
        }
        System.out.println("Secret image extracted successfully!");
        return new ImagePlus("Extracted Secret Image", extractedProcessor);
    }
}
