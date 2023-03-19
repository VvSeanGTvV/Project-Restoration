package classicMod.library.converter;

import arc.graphics.*;
import mindustry.*;
import mindustry.mod.Mods.*;

import java.io.*;

public class VideoFrameExtractor {
    public static void extractFrames() throws IOException {
        LoadedMod mod = Vars.mods.locateMod("restored-mind");
        String videoFilePath = "mods/" + mod.file.name() + "/cutscene/cutscenEnd.mp4";
        int frameCount = 0;

        // Open video file as input stream
        InputStream inputStream = new FileInputStream(videoFilePath);
        BufferedInputStream bis = new BufferedInputStream(inputStream);

        // Read video file as byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = bis.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        byte[] videoData = baos.toByteArray();
        bis.close();

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

                // Create Pixmap from frame data
                byte[] frameBytes = frameData.toByteArray();
                Pixmap pixmap = new Pixmap(1, 1);
                //PixmapIO.read(pixmap, new ByteArrayInputStream(frameBytes));
                // Do whatever you want with the Pixmap, such as drawing it to the screen
                // Dispose of the Pixmap to free up memory
                pixmap.dispose();

                frameCount++;
            }
        }
    }
}

