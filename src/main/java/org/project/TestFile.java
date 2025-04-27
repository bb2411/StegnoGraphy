package org.project;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.IJ;
import java.awt.*;

public class TestFile {
    public static void main(String[] args) {
        System.out.println("LSB Steganography");
        String coverpath="D:\\Projects\\stegnography\\src\\main\\resources\\cover-images\\cover-5.jpg";
        String secretpath="D:\\Projects\\stegnography\\src\\main\\resources\\secret-images\\image_11.png";
        String stegopath="D:\\Projects\\stegnography\\src\\main\\resources\\outputs\\stego_image.jpg";
        String extractedpath = "D:\\Projects\\stegnography\\src\\main\\resources\\outputs\\extracted.jpg";
        ImagePlus coverImage=loadImage(coverpath,"Cover Image");
        ImagePlus secretImage=loadImage(secretpath,"Secret Image");
        if(coverImage==null || secretImage==null) {
            System.err.println("Error Unable to load images");
            return;
        }
        ImagePlus stegoImage=encodeImage(coverImage,secretImage);
        if(stegoImage!=null){
            saveImage(stegoImage,stegopath,"Stego Image");
        }
        ImagePlus extractedImage=decodeImage(stegoImage);
        if(extractedImage!=null) {
            saveImage(extractedImage,extractedpath,"Extracted Secret Image");
        }
    }
    private static ImagePlus loadImage(String path,String imageName) {
        System.out.println("Loading : "+imageName+" from: "+path);
        ImagePlus image=IJ.openImage(path);
        if(image==null){
            System.err.println("Error: "+imageName+" could not be loaded.");
        }
        return image;
    }
    private static void saveImage(ImagePlus image,String filePath,String imageName) {
        if(image!=null) {
            System.out.println(imageName+" saved at: "+filePath);
            IJ.saveAs(image, "jpg", filePath);
        } else {
            System.err.println("Error: "+imageName+" could not be saved.");
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
    private static void storeDimension(ImageProcessor processor, int x, int y, int value) {
        int coverPixel = processor.getPixel(x, y);
        Color coverColor = new Color(coverPixel);
        int newRed = (coverColor.getRed() & 0b00000000) | ((value >> 8) & 0xFF);
        int newGreen = (coverColor.getGreen() & 0b00000000) | (value & 0xFF);
        int newBlue = coverColor.getBlue();
        System.out.println("storedimension :"+value);
        processor.putPixel(x, y, new Color(newRed, newGreen, newBlue).getRGB());
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
