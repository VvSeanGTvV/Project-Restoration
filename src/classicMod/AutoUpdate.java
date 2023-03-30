package classicMod;

import arc.files.*;
import arc.util.*;
import arc.util.Http.*;
import arc.util.io.*;
import arc.util.serialization.*;
import mindustry.mod.Mods.*;

import java.net.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class AutoUpdate {

    public static final String repo = "VvSeanGTvV/Project-Restoration";

    public static LoadedMod mod;
    public static String url;

    public static float progress;
    public static String download;
    public static int latestBuild;
    public static String latest;

    public static void load() {
        mod = mods.getMod("restored-mind");
        url = ghApi + "/repos/" + repo + "/releases/latest";

        Jval meta = Jval.read(new ZipFi(mod.file).child("mod.hjson").readString());
        mod.meta.author = meta.getString("author"); // restore colors in mod's meta
        mod.meta.description = meta.getString("description");
    }

    public static void check() {
        Log.info("Checking for updates.");
        Http.get(url, res -> {
            Jval json = Jval.read(res.getResultAsString());
            latest = json.getString("tag_name").substring(1);
            download = json.get("assets").asArray().get(0).getString("browser_download_url");
            latestBuild = Integer.parseInt(latest); //change into INT as build number
            int modBuild = Integer.parseInt(ClassicMod.BuildVer); //change into INT as build number

            //check if Build is not in the latest
            if (modBuild < latestBuild) ui.showCustomConfirm(
                    "@updater.restored-mind.name", bundle.format("updater.restored-mind.info", mod.meta.version, latest),
                    "@updater.restored-mind.load", "@ok", AutoUpdate::update, () -> {});
        }, Log::err);
    }

    public static void update() {
        try { // dancing with tambourines, just to remove the old mod
            if (mod.loader instanceof URLClassLoader cl) cl.close();
            mod.loader = null;
        } catch (Throwable e) { Log.err(e); } // this has never happened before, but everything can be

        ui.loadfrag.show("@updater.restored-mind.updating");
        ui.loadfrag.setProgress(() -> progress);

        Http.get(download, AutoUpdate::handle, Log::err);
    }

    public static void handle(HttpResponse res) {
        try {
            Fi file = tmpDirectory.child(repo.replace("/", "") + ".zip");
            Streams.copyProgress(res.getResultAsStream(), file.write(false), res.getContentLength(), 4096, p -> progress = p);

            mods.importMod(file).setRepo(repo);
            file.delete();

            app.post(ui.loadfrag::hide);
            ui.showInfoOnHidden("@mods.reloadexit", app::exit);
        } catch (Throwable e) { Log.err(e); }
    }
    public static int getLatestBuild(){ return latestBuild; };

    /*public static Fi script() {
        return mod.root.child("scripts").child("main.js");
    }*/

    public static boolean installed(String mod) {
        return mods.getMod(mod) != null && mods.getMod(mod).enabled();
    }
}
