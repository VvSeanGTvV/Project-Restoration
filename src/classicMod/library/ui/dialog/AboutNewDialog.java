package classicMod.library.ui.dialog;

import arc.*;
import arc.graphics.Color;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.*;
import arc.util.*;
import classicMod.library.ui.UIExtended;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.ui.*;
import mindustry.ui.dialogs.BaseDialog;

public class AboutNewDialog extends BaseDialog {
    Seq<String> contributors = new Seq();
    static ObjectSet<String> bannedItems = ObjectSet.with(new String[]{"google-play", "itch.io", "dev-builds", "f-droid"});

    public AboutNewDialog() {
        super("@about.button");
        this.shown(() -> {
            this.contributors = Seq.with(Core.files.internal("contributors").readString("UTF-8").split("\n"));
            Core.app.post(this::setup);
        });
        this.shown(this::setup);
        this.onResize(this::setup);
    }

    void setup() {
        this.cont.clear();
        this.buttons.clear();
        float h = Core.graphics.isPortrait() ? 90.0F : 80.0F;
        float w = Core.graphics.isPortrait() ? 400.0F : 600.0F;
        Table in = new Table();
        ScrollPane pane = new ScrollPane(in);
        Links.LinkEntry[] var5 = Links.getLinks();
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Links.LinkEntry link = var5[var7];
            if (!Vars.ios && !OS.isMac && !Vars.steam || !bannedItems.contains(link.name)) {
                Table table = new Table(Styles.grayPanel);
                table.margin(0.0F);
                table.table((img) -> {
                    img.image().height(h - 5.0F).width(40.0F).color(link.color);
                    img.row();
                    img.image().height(5.0F).width(40.0F).color(link.color.cpy().mul(0.8F, 0.8F, 0.8F, 1.0F));
                }).expandY();
                table.table((i) -> {
                    i.background(Styles.grayPanel);
                    i.image(link.icon);
                }).size(h - 5.0F, h);
                table.table((inset) -> {
                    inset.add("[accent]" + link.title).growX().left();
                    inset.row();
                    inset.labelWrap(link.description).width(w - 100.0F - h).color(Color.lightGray).growX();
                }).padLeft(8.0F);
                table.button(Icon.link, Styles.clearNoneTogglei, () -> {
                    if (link.name.equals("wiki")) {
                        Events.fire(EventType.Trigger.openWiki);
                    }

                    if (!Core.app.openURI(link.link)) {
                        Vars.ui.showErrorMessage("@linkfail");
                        Core.app.setClipboardText(link.link);
                    }

                }).size(h - 5.0F, h);
                in.add(table).size(w, h).padTop(5.0F).row();
            }
        }

        this.shown(() -> {
            Time.run(1.0F, () -> {
                Core.scene.setScrollFocus(pane);
            });
        });
        this.cont.add(pane).growX();
        this.addCloseButton();
        this.buttons.button("@credits", UIExtended.creditsCutsceneDialog::show).size(200.0F, 64.0F);
    }
}
