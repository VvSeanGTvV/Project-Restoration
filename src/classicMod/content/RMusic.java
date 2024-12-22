package classicMod.content;

import arc.Core;
import arc.audio.Music;
import arc.struct.Seq;
import arc.util.Nullable;

import static mindustry.Vars.*;

// WilloIzCitron Music Player
// uujuju#8068 - for reminding me that they do have loading things......... guh
public class RMusic {

    private static @Nullable Music current;
    private static Music lastMusicPlayed;
    public static Music
            credits, bleepsgalore, aprilmenu, rickrool, seq, day4, menu;

    public static Seq<Music> ostMusic = new Seq<>();

    public static void playMusic(Music music){
        if(current != null || music == null || !(boolean)(Core.settings.getInt("musicvol") > 0)) return;
        lastMusicPlayed = music;
        current = music;
        current.setVolume(1f);
        current.setLooping(false);
        current.play();
    }

    public static void stopMusic(){
        if(current != null) {
            current.stop();
            current = null;
        }
    }

    public static Music loadMusic(String musicName, Seq<Music> to){
        var music = tree.loadMusic(musicName);
        to.add(music);
        return music;
    }

    public static void load() {
        seq = loadMusic("seq", ostMusic);
        day4 = loadMusic("day4", control.sound.ambientMusic);
        credits = loadMusic("credits", ostMusic);
        bleepsgalore = loadMusic("bleepsgalore", ostMusic);
        aprilmenu = loadMusic("aprilmenu", ostMusic);
        rickrool = loadMusic("mistake", ostMusic);
    }
}
