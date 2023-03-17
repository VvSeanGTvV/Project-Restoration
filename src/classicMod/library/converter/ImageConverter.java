package classicMod.library.converter;


import javax.imageio.*;
import java.awt.image.*;
import java.io.*;

public class ImageConverter {
    public static String FileIN = "";
    public static String FileOUT = "";
    public static void main(String[] args) throws IOException {
        File inputFile = new File(FileIN); //"path/to/input/file.jpg"
        File outputFile = new File(FileOUT); //"path/to/output/file.png"

        BufferedImage image = ImageIO.read(inputFile);

        if (image == null) {
            System.err.println("Error: Could not read image file.");
            return;
        }

        ImageIO.write(image, "png", outputFile);

        System.out.println("Image conversion complete.");
    }

}