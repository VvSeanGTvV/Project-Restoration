package classicMod.library.ui.dialog;

import arc.Core;
import arc.Events;
import arc.math.geom.Vec2;
import arc.scene.ui.Dialog;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import classicMod.ClassicMod;
import classicMod.library.blocks.legacyBlocks.MechPad;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.Sector;
import mindustry.type.SectorPreset;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.*;

import static mindustry.Vars.*;
import static mindustry.Vars.control;

public class ContentUnlockDebugDialog extends BaseDialog {

    float buttonWidth = 92f;
    float buttonHeight = 32f;
    public ContentUnlockDebugDialog() {
        super("@CUD.title");
        addUnlockAllButton();
        addCloseButton();
        addLockAllButton();

        rebuild();

        show();
    }

    public void addUnlockAllButton(float width){
        buttons.defaults().size(width, 64f);
        buttons.button("@unlockall", Icon.download, () -> {
            ClassicMod.unlockAll();
            rebuild();
        }).size(width, 64f);
    }

    public void addUnlockAllButton(){
        addUnlockAllButton(210f);
    }

    public void addLockAllButton(float width){
        buttons.defaults().size(width, 64f);
        buttons.button("@lockall", Icon.download, () -> {
            ClassicMod.lockAll();
            rebuild();
        }).size(width, 64f);
    }

    public void addLockAllButton(){
        addLockAllButton(210f);
    }

    void rebuildTable(){
        Items = new Table() {{
            for (var Content : Vars.content.items()) {
                table(Styles.grayPanel, info -> {
                    info.table(details -> {
                        details.image(Content.fullIcon).size(32f).scaling(Scaling.fit).pad(10f).left();
                        details.add(Content.localizedName).left().pad(10f);
                    });

                    info.row();
                    info.table(yes -> {
                        if (Content.alwaysUnlocked) {
                            yes.add("@alwaysUnlock").pad(2.5f).color(Pal.accent);
                        } else {
                            if (Content.unlocked()) yes.button("@lock", () -> {
                                Content.clearUnlock();
                                rebuild();
                            }).size(buttonWidth, buttonHeight).pad(2.5f);
                            else yes.button("@unlock", () -> {
                                Content.unlock();
                                rebuild();
                            }).size(buttonWidth, buttonHeight).pad(2.5f);
                        }
                    }).left().pad(2.5f);
                }).left().pad(10f);
                row();
            }
        }};

        Liquids = new Table() {{
            for (var Content : Vars.content.liquids()) {
                table(Styles.grayPanel, info -> {
                    info.table(details -> {
                        details.image(Content.fullIcon).size(32f).scaling(Scaling.fit).pad(10f).left();
                        details.add(Content.localizedName).left().pad(10f);
                    });

                    info.row();
                    info.table(yes -> {
                        if (Content.alwaysUnlocked) {
                            yes.add("@alwaysUnlock").pad(2.5f).color(Pal.accent);
                        } else {
                            if (Content.unlocked()) yes.button("@lock", () -> {
                                Content.clearUnlock();
                                rebuild();
                            }).size(buttonWidth, buttonHeight).pad(2.5f);
                            else yes.button("@unlock", () -> {
                                Content.unlock();
                                rebuild();
                            }).size(buttonWidth, buttonHeight).pad(2.5f);
                        }
                    }).left().pad(2.5f);
                }).left().pad(10f);
                row();
            }
        }};

        Blocks = new Table() {{
            for (var Content : Vars.content.blocks()){
                if(Content instanceof StaticTree ||
                        Content instanceof StaticWall ||
                        Content instanceof Floor ||
                        Content instanceof AirBlock ||
                        Content instanceof SpawnBlock ||
                        Content instanceof ShallowLiquid ||
                        Content.isHidden()
                ) continue;
                table(Styles.grayPanel, info -> {
                    info.table(details -> {
                        details.image(Content.fullIcon).size(32f).scaling(Scaling.fit).pad(10f).left();
                        details.add(Content.localizedName).left().pad(10f);
                    });

                    info.row();
                    info.table(yes -> {
                        if(Content.alwaysUnlocked) {
                            yes.add("@alwaysUnlock").pad(2.5f).color(Pal.accent);
                        } else {
                            if (Content.unlocked()) yes.button("@lock", () -> {
                                Content.clearUnlock();
                                rebuild();
                            }).size(buttonWidth, buttonHeight).pad(2.5f);
                            else yes.button("@unlock", () -> {
                                Content.unlock();
                                rebuild();
                            }).size(buttonWidth, buttonHeight).pad(2.5f);
                        }
                    }).left().pad(2.5f);
                }).left().pad(10f);
                row();
            }
        }};

        Units = new Table() {{
            for (var Content : Vars.content.units()){
                if(Content.isHidden()) continue;
                table(Styles.grayPanel, info -> {
                    info.table(details -> {
                        details.image(Content.fullIcon).size(32f).scaling(Scaling.fit).pad(10f).left();
                        details.add(Content.localizedName).left().pad(10f);
                    });

                    info.row();
                    info.table(yes -> {
                        if(Content.alwaysUnlocked) {
                            yes.add("@alwaysUnlock").pad(2.5f).color(Pal.accent);
                        } else {
                            if (Content.unlocked()) {
                                yes.button("@lock", () -> {
                                    Content.clearUnlock();
                                    rebuild();
                                }).size(buttonWidth, buttonHeight).pad(2.5f);

                                yes.button("@transform", () -> {
                                    spawnMech(Content, player);
                                }).size(buttonWidth, buttonHeight).pad(2.5f);

                            } else yes.button("@unlock", () -> {
                                Content.unlock();
                                rebuild();
                            }).size(buttonWidth, buttonHeight).pad(2.5f);
                        }
                    }).left().pad(2.5f);
                }).left().pad(10f);
                row();
            }
        }};

        Status = new Table() {{
            for (var Content : Vars.content.statusEffects()){
                if(Content.isHidden()) continue;
                table(Styles.grayPanel, info -> {
                    info.table(details -> {
                        details.image(Content.fullIcon).size(32f).scaling(Scaling.fit).pad(10f).left();
                        details.add(Content.localizedName).left().pad(10f);
                    });

                    info.row();
                    info.table(yes -> {
                        if(Content.alwaysUnlocked) {
                            yes.add("@alwaysUnlock").pad(2.5f).color(Pal.accent);
                        } else {
                            if (Content.unlocked()) yes.button("@lock", () -> {
                                Content.clearUnlock();
                                rebuild();
                            }).size(buttonWidth, buttonHeight).pad(2.5f);
                            else yes.button("@unlock", () -> {
                                Content.unlock();
                                rebuild();
                            }).size(buttonWidth, buttonHeight).pad(2.5f);
                        }
                    }).left().pad(2.5f);
                }).left().pad(10f);
                row();
            }
        }};

        SectorPresets = new Table() {{
            for (var Content : Vars.content.sectors()) {
                table(Styles.grayPanel, info -> {
                    info.table(details -> {
                        details.image(Icon.icons.get(Content.planet.icon + "Small", Icon.icons.get(Content.planet.icon, Icon.commandRallySmall))).size(32f).scaling(Scaling.fit).pad(10f).left().color(Content.planet.iconColor);
                        details.add(Content.localizedName).left().pad(10f);
                    });

                    info.row();
                    info.table(yes -> {
                        if (Content.alwaysUnlocked) {
                            yes.add("@alwaysUnlock").pad(2.5f).color(Pal.accent);
                        } else {
                            if (Content.unlocked()) {
                                yes.button("@lock", () -> {
                                    Content.clearUnlock();
                                    rebuild();
                                }).size(buttonWidth, buttonHeight).pad(2.5f);
                                yes.button("@launch-to", () -> {
                                    StartSector(Content);
                                    hide();
                                }).size(105f, 64f).pad(2.5f);
                            } else yes.button("@unlock", () -> {
                                Content.unlock();
                                rebuild();
                            }).size(buttonWidth, buttonHeight).pad(2.5f);
                        }
                    }).left().pad(2.5f);
                }).left().pad(10f);
                row();
            }
        }};
    }
    Table Items;
    Table Liquids;
    Table Blocks;

    Table Units;

    Table Status;

    Table SectorPresets;

    ScrollPane ItemPane = new ScrollPane(Items);
    ScrollPane LiquidPane = new ScrollPane(Liquids);
    ScrollPane BlockPane = new ScrollPane(Blocks);
    ScrollPane UnitPane = new ScrollPane(Units);
    ScrollPane StatPane = new ScrollPane(Status);
    ScrollPane SectorPane = new ScrollPane(SectorPresets);

    void rebuild(int Table){
        rebuildTable();
        if(Table == 0){
            var PaneAdd = ItemPane;
            var lastY = PaneAdd.getScrollY();
            cont.add(PaneAdd);
            PaneAdd.setScrollY(lastY);
        }
        if(Table == 1){
            var PaneAdd = LiquidPane;
            var lastY = PaneAdd.getScrollY();
            cont.add(PaneAdd);
            PaneAdd.setScrollY(lastY);
        }
        if(Table == 2){
            var PaneAdd = UnitPane;
            var lastY = PaneAdd.getScrollY();
            cont.add(PaneAdd);
            PaneAdd.setScrollY(lastY);
        }
        if(Table == 3){
            var PaneAdd = BlockPane;
            var lastY = PaneAdd.getScrollY();
            cont.add(PaneAdd);
            PaneAdd.setScrollY(lastY);
        }
        if(Table == 4){
            var PaneAdd = StatPane;
            var lastY = PaneAdd.getScrollY();
            cont.add(PaneAdd);
            PaneAdd.setScrollY(lastY);
        }
        if(Table == 5){
            var PaneAdd = SectorPane;
            var lastY = PaneAdd.getScrollY();
            cont.add(PaneAdd);
            PaneAdd.setScrollY(lastY);
        }
    }

    void rebuild(){
        cont.clearChildren();
        for(int i = 0; i < 5; i++) rebuild(i);
    }


    public void spawnMech(UnitType unitType, Player player){
        //do not try to respawn in unsupported environments at all
        if(!unitType.supportsEnv(state.rules.env)) return;
        if(unitType.isBanned()) return;

        if (Vars.net.server() || !Vars.net.active()){
            playerSpawn(player, unitType);
        }

        if (Vars.net.server()) {
            PlayerSpawnCallPacket packet = new PlayerSpawnCallPacket();
            packet.player = player;
            Vars.net.send(packet, true);
        }
    }

    public void playerSpawn(Player player, UnitType unitType){
        if(player == null) return;
        var lastPos = new Vec2(player.unit().x, player.unit().y);

        player.set(lastPos);

        if(!net.client()){
            Unit unit = unitType.create(player.unit().team);
            unit.set(lastPos);
            unit.rotation(90f);
            unit.impulse(0f, 3f);
            unit.controller(player);
            unit.spawnedByCore(true);
            unit.add();
        }
    }

    public void StartSector(SectorPreset to){
        var sector = to.sector;

        Events.fire(new EventType.SectorLaunchEvent(sector));
        control.playSector(sector);
    }

}
