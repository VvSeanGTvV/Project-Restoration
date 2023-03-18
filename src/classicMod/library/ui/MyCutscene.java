package classicMod.library.ui;


import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.stage.*;

import java.nio.file.*;

import static classicMod.library.ui.UIExtended.*;


public class MyCutscene {
    public static void main(String[] args) {
        File file = new File("cutscenEnd.mp4");
        String filePath = file.toURI().toString();
        
        // Create a JavaFX MediaPlayer object
        Media media = new Media(filePath);
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        // Create a JavaFX MediaView object
        MediaView mediaView = new MediaView(mediaPlayer);

        // Create a JavaFX Pane object and add the MediaView to it
        Pane pane = new Pane(mediaView);

        // Create a JavaFX Scene object and add the Pane to it
        Scene scene = new Scene(pane, getWidth(), getHeight());

        // Create a JavaFX Stage object and set the Scene
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

        // Play the video
        mediaPlayer.play();

        // Listen for when the video has finished playing
        mediaPlayer.setOnEndOfMedia(() -> {
            //Platform.exit();
            stage.hide();
        });
    }
}

