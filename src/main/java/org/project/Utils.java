package org.project;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.*;

public class Utils {

    static ImagePlus loadImage(String path, String imageName) {
        System.out.println("Loading : "+imageName+" from: "+path);
        ImagePlus image=IJ.openImage(path);
        if(image==null){
            System.err.println("Error: "+imageName+" could not be loaded.");
        }
        return image;
    }
    static void saveImage(ImagePlus image, String filePath, String imageName) {
        if(image!=null) {
            System.out.println(imageName+" saved at: "+filePath);
            IJ.saveAs(image, "png", filePath);
        } else {
            System.err.println("Error: "+imageName+" could not be saved.");
        }
    }
}
