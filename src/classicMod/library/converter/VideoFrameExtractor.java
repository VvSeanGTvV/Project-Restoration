package classicMod.library.converter;

import arc.*;
import mindustry.mod.Mods.*;

import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

import static mindustry.Vars.mods;

public class VideoFrameExtractor {
    public static void main(String[] args) throws Exception {
        LoadedMod mod = mods.locateMod("restored-mind");
        //mod.file.absolutePath();
        String videoFilePath = Core.files.internal("mods/"+mod.file.name()+"/cutscene/").absolutePath();
        int frameCount = 0;

        // Open video file as input stream
        FileInputStream fis = new FileInputStream(videoFilePath);
        BufferedInputStream bis = new BufferedInputStream(fis);

        // Read video file as byte array
        //byte[] videoData = new byte[bis.available()];
        InputStream inputStream = bis; // your input stream
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        byte[] videoData = baos.toByteArray();
        bis.close();
        fis.close();

        // Extract frames from video data
        for (int i = 0; i < videoData.length; i++) {
            if (videoData[i] == 0x00 && videoData[i+1] == 0x00 && videoData[i+2] == 0x01 && videoData[i+3] == 0xB3) {
                // Start of a frame
                i += 4;

                ByteArrayOutputStream frameData = new ByteArrayOutputStream();
                while (i < videoData.length && !(videoData[i] == 0x00 && videoData[i+1] == 0x00 && videoData[i+2] == 0x01)) {
                    frameData.write(videoData[i]);
                    i++;
                }

                // Create Image object from frame data
                Image image = ImageIO.read(new ByteArrayInputStream(frameData.toByteArray()));
                File outputFile = new File("frame_" + frameCount + ".png");
                BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                bufferedImage.getGraphics().drawImage(image, 0, 0, null);
                try {
                    ImageIO.write(bufferedImage, "png", outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Save Image object as PNG file
                //File outputFile = new File("frame_" + frameCount + ".png");
                //ImageIO.write(image, "png", outputFile);

                frameCount++;
            }
        }
    }
}
