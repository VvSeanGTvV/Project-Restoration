package classicMod.library.converter;


import javax.imageio.*;
import javax.media.jai.*;
import java.awt.image.RenderedImage;
import java.io.*;

public class ImageConverter {
    public static String FileIN = "";
    public static String FileOUT = "";
    public static void main(String[] args) throws IOException {
        File inputFile = new File(FileIN); //"path/to/input/file.jpg"
        //File outputFile = new File(FileOUT); //"path/to/output/file.png"

        RenderedImage image = JAI.create("fileload", inputFile.getAbsolutePath());
        RenderedImage pngImage = new RenderedImageAdapter(image);
        //BufferedImage image = ImageIO.read(inputFile);

        if (pngImage == null) {
            System.err.println("Error: Could not read image file.");
            return;
        }

        //ImageIO.write(image, "png", outputFile);
        ImageIO.write(pngImage, "png", new File(FileOUT));

        System.out.println("Image conversion complete.");
    }

}