package classicMod.library.converter;


import arc.files.*;
import arc.graphics.*;
import arc.util.io.*;

import java.io.*;
import java.nio.file.*;

public class ImageConverter {
    public static String FileIN = "";
    public static String FileOUT = "";
    //private static InputStream is;

    public static void main(String[] args) throws IOException {
        // Load the JPG image file
        byte[] imageData = readBytesFromFile(FileIN);

        // Create a Pixmap object from the image data
        Pixmap pixmap = new Pixmap(imageData, 0, imageData.length);

        // Save the Pixmap as a PNG image file
        savePixmapToFile(pixmap, FileOUT);
    }

    private static byte[] readBytesFromFile(String filePath) throws IOException {
        InputStream is = ImageConverter.class.getResourceAsStream(filePath);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        is.close();
        return buffer.toByteArray();
    }

    private static void savePixmapToFile(Pixmap pixmap, String filePath) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PixmapIO.writePng(new Fi(filePath), pixmap); //write(os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        try {
            // Use the Files class to copy the input stream to the file
            Files.copy(is, Paths.get(filePath));
        } catch (Exception e) {
            // Handle any exceptions that occur during the copy operation
            e.printStackTrace();
        }
        Streams.close(is);
    }

}