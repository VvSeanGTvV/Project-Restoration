package classicMod.library.blocks.customBlocks;

import arc.*;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import classicMod.content.*;
import classicMod.uCoreGraphics.uCoreLines;
import mindustry.Vars;
import mindustry.content.Liquids;
import mindustry.entities.*;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.meta.StatUnit;
import mindustry.world.modules.ItemModule;

import static arc.Core.atlas;
import static mindustry.Vars.*;

//TODO make this more functional
public class WarpGate extends Block {

    protected static final Color[] selection = new Color[]{Color.royal, Color.orange, Color.scarlet, Color.forest, Color.purple, Color.gold, Color.pink, Color.white};
    protected static final ObjectSet<WarpGate.WarpGateBuild>[][] teleporters;

    static {
        teleporters = new ObjectSet[Team.baseTeams.length][selection.length];
        for (int i = 0; i < Team.baseTeams.length; i++) {
            if (teleporters[i] == null) teleporters[i] = new ObjectSet[selection.length];
            for (int j = 0; j < selection.length; j++) teleporters[i][j] = new ObjectSet<>();
        }
    }

    //Time effect ends
    private final float warmupTime = 60f;
    /**
     * Time between each teleport Transportation
     **/
    public float teleportMax = 500f;
    public float powerUse = 0.3f;
    public float teleportLiquidUse = 0.3f;
    public float liquidUse = 0.1f;
    public Liquid inputLiquid;
    protected Effect activateEffect = RFx.teleportActivate;
    protected Effect teleportEffect = RFx.teleport;
    protected Effect teleportOutEffect = RFx.teleportOut;
    protected TextureRegion blankRegion;

    public WarpGate(String name) {
        super(name);
        itemCapacity = 100;
        update = true;
        solid = true;
        configurable = true;
        saveConfig = true;
        unloadable = false;
        hasItems = true;

        config(Integer.class, (WarpGate.WarpGateBuild build, Integer value) -> {
            if (build.toggle != -1) if (teleporters[build.team.id][build.toggle].contains(build)) teleporters[build.team.id][build.toggle].remove(build);
            if (value != -1) teleporters[build.team.id][value].add(build);
            build.toggle = value;

        });
        configClear((WarpGate.WarpGateBuild build) -> {
            if (teleporters[build.team.id][build.toggle].contains(build) && build.toggle != -1) teleporters[build.team.id][build.toggle].remove(build);
            build.toggle = -1;
        });
    }

    @Override
    public boolean outputsItems() {
        return true;
    }

    @Override
    public void init() {
        if (inputLiquid == null) inputLiquid = Liquids.cryofluid;
        consumePowerCond(powerUse, WarpGate.WarpGateBuild::isConsuming);
        super.init();
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(ExtendedStat.inbetweenTeleport, teleportMax / 60f, StatUnit.seconds);
    }

    @Override
    public void load() {
        super.load();
        blankRegion = atlas.find(name + "-mid");
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("items", entity -> new Bar(
                () -> Core.bundle.format("bar.items-input", entity.items.total()),
                () -> Pal.items,
                () -> (float)entity.items.total() / itemCapacity)
        );

        addBar("items-output", (WarpGate.WarpGateBuild entity) -> new Bar(
                () -> Core.bundle.format("bar.items-output", entity.OutputStackHold.total()),
                () -> Pal.lighterOrange,
                () -> (float)entity.OutputStackHold.total() / itemCapacity)
        );

        addBar("next-teleport", (WarpGate.WarpGateBuild e) -> new Bar(Core.bundle.format("bar.next-tele"), Pal.ammo, e::fraction));
    }

    @Override
    public void drawPlanConfig(BuildPlan req, Eachable<BuildPlan> list) {
        drawPlanConfigCenter(req, req.config, "nothing");
    }

    @Override
    public void drawPlanConfigCenter(BuildPlan req, Object content, String region) {
        if (!(content instanceof Integer temp) || temp < 0 || temp >= selection.length) return;
        Draw.color(selection[temp]);
        Draw.rect(blankRegion, req.drawx(), req.drawy());
    }

    public class WarpGateBuild extends Building {
        protected int toggle = -1, entry;
        protected float duration, activeScl, teleProgress, otherX, otherY;
        protected boolean teleporting, onTransfer;
        protected WarpGateBuild otherWarp;
        protected @Nullable ItemModule OutputStackHold = new ItemModule();
        protected WarpGate.WarpGateBuild target;
        protected Team previousTeam;
        protected boolean firstTime = true;
        /**
         * is this specific building avaliable and able to transport
         **/
        protected boolean transportable;


        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return super.acceptLiquid(source, liquid) && liquid == inputLiquid;
        }

        public float fraction() {
            return teleProgress;
        }
        protected boolean isConsuming() {
            return warmupTime > 0f;
        }

        protected boolean isTeamChanged() {
            return previousTeam != team;
        }

        @Override
        public void draw() {
            super.draw();
            if (toggle != -1) {
                Draw.color(selection[toggle]);
                Draw.rect(blankRegion, x, y);
            }
            Draw.color(Color.white);
            Draw.alpha(0.45f + Mathf.absin(7f, 0.26f));
            Draw.reset();

            float time = Time.time;
            float rad = activeScl;
            float radMin = 0.01f;

            if (rad <= radMin && toggle == -1) return;
            if (rad >= radMin && toggle != -1) {

                Draw.colorMul(selection[toggle], 0.7f);

                Fill.circle(tile.drawx(), tile.drawy(), rad * (7f + Mathf.absin(time + 55, 8f, 1f)));

                Draw.colorMul(selection[toggle], 0.5f);

                Fill.circle(tile.drawx(), tile.drawy(), rad * (2f + Mathf.absin(time, 7f, 3f)));

                for (int i = 0; i < 11; i++) {
                    uCoreLines.swirl(tile.drawx(), tile.drawy(),
                            rad * (2f + i / 3f + Mathf.sin(time - i * 75, 20f + i, 3f)),
                            0.3f + Mathf.sin(time + i * 33, 10f + i, 0.1f),
                            time * (1f + Mathf.randomSeedRange(i + 1, 1f)) + Mathf.randomSeedRange(i, 360f));
                }

                Draw.colorMul(selection[toggle], 1.5f);

                Lines.stroke(2f);
                Lines.circle(tile.drawx(), tile.drawy(), rad * (7f + Mathf.absin(time + 55, 8f, 1f)));
                Lines.stroke(1f);

                for (int i = 0; i < 11; i++) {
                    uCoreLines.swirl(tile.drawx(), tile.drawy(),
                            rad * (3f + i / 3f + Mathf.sin(time + i * 93, 20f + i, 3f)),
                            0.2f + Mathf.sin(time + i * 33, 10f + i, 0.1f),
                            time * (1f + Mathf.randomSeedRange(i + 1, 1f)) + Mathf.randomSeedRange(i, 360f));
                }
            }

            Draw.reset();
        }

        @Override
        public void updateTile() {
            if (efficiency > 0 && toggle != -1) {
                if (firstTime) {
                    activateEffect.at(this.x, this.y, selection[toggle]);
                    firstTime = false;
                }

                activeScl = Mathf.lerpDelta(activeScl, 1f, 0.015f);

                Building otherBuild = world.buildWorld(otherX, otherY);
                WarpGateBuild otherWarpGateBuild = (otherBuild instanceof WarpGateBuild Build) ? Build : null;
                WarpGateBuild other = (otherWarpGateBuild != null && otherWarpGateBuild.onTransfer && this.onTransfer) ? otherWarpGateBuild : findLink(toggle);
                if (this.items.total() >= itemCapacity) {
                    if (other == this) other = null;
                    if (other != null) teleProgress += getProgressIncrease(warmupTime); else {
                        teleProgress %= 1f;
                        duration = 0f;
                    }
                    if (teleProgress >= 1f) {
                        teleProgress = 1f;
                        if (!teleporting && other != null) {
                            teleportEffect.at(this.x, this.y, selection[toggle]);
                            teleporting = true;
                        }
                        if (other != null) duration += getProgressIncrease(60f);
                        if (duration >= 1f) {
                            if (this.items.total() <= 0 || toggle == -1) {
                                teleProgress %= 1f;
                                duration = 0f;
                                teleporting = false;
                            }
                            teleportOutEffect.at(this.x, this.y, selection[toggle]);
                            //onTransferData.set(items.copy());
                            //items.clear();
                            handleTransport(other);

                            teleProgress %= 1f;
                            duration = 0f;
                            teleporting = false;
                        }
                    }
                }
            } else {
                activeScl = Mathf.lerpDelta(activeScl, 0f, 0.015f);
                firstTime = true;
                teleProgress = 0f;
                duration = 0f;
            }
            //if(!liquids.hasFlowLiquid(inputLiquid) && this.block.consPower.efficiency(this)>=1) catastrophicFailure();
            if (OutputStackHold.any()) dumpOutputHold();
            transportable = !(OutputStackHold.total() >= this.block.itemCapacity); //prevent buildings from having too much items in single block.
        }

        public boolean dumpOutputHold() {
            return this.dumpOutputHold((Item) null);
        }

        public boolean dumpOutputHold(Item todump){
            if (this.block.hasItems && this.OutputStackHold.total() != 0 && this.proximity.size != 0 && (todump == null || this.OutputStackHold.has(todump))) {
                int dump = this.cdump;
                Seq<Item> allItems = Vars.content.items();
                int itemSize = allItems.size;
                Object[] itemArray = allItems.items;

                for(int i = 0; i < this.proximity.size; ++i) {
                    Building other = (Building)this.proximity.get((i + dump) % this.proximity.size);
                    if (todump == null) {
                        for(int ii = 0; ii < itemSize; ++ii) {
                            if (this.OutputStackHold.has(ii)) {
                                Item item = (Item)itemArray[ii];
                                if (other.acceptItem(this, item) && this.canDump(other, item)) {
                                    other.handleItem(this, item);
                                    this.OutputStackHold.remove(item, 1);
                                    this.incrementDump(this.proximity.size);
                                    return true;
                                }
                            }
                        }
                    } else if (other.acceptItem(this, todump) && this.canDump(other, todump)) {
                        other.handleItem(this, todump);
                        this.OutputStackHold.remove(todump, 1);
                        this.incrementDump(this.proximity.size);
                        return true;
                    }

                    this.incrementDump(this.proximity.size);
                }

                return false;
            } else {
                return false;
            }
        }

        public void catastrophicFailure() {
            this.damage(this.health + 1);
            //TODO fail gloriously lol
        }

        @Override
        public void buildConfiguration(Table table) {
            final ButtonGroup<Button> group = new ButtonGroup<>();
            group.setMinCheckCount(0);
            for (int i = 0; i < selection.length; i++) {
                int j = i;
                ImageButton button = table.button(Tex.whiteui, Styles.clearTogglei, 24f, () -> {
                }).size(34f).group(group).get();
                button.changed(() -> configure(button.isChecked() ? j : -1));
                button.getStyle().imageUpColor = selection[j];
                button.update(() -> button.setChecked(toggle == j));
                if (i % 4 == 3) table.row();
            }
        }

        public WarpGate.WarpGateBuild findLink(int value) {
            ObjectSet<WarpGate.WarpGateBuild> teles = teleporters[team.id][value];
            Seq<WarpGate.WarpGateBuild> entries = teles.toSeq();
            if (entry >= entries.size) entry = 0;
            if (entry == entries.size - 1) {
                WarpGate.WarpGateBuild other = teles.get(entries.get(entry));
                if (other == this) entry = 0;
            }
            for (int i = entry, len = entries.size; i < len; i++) {
                WarpGate.WarpGateBuild other = teles.get(entries.get(i));
                if (other != this) {
                    entry = i + 1;
                    if (!other.isValid() && teleporters[team.id][toggle].contains(other)) {
                        teleporters[team.id][toggle].remove(other);
                        return null;
                    }
                    if (!(other.OutputStackHold.total() >= other.block.itemCapacity) && other.isValid()) return other;
                }
                if (entry >= entries.size) entry = 0;
            }

            /*Seq<Building> entries = team.data().buildings.copy().removeAll(b -> (b instanceof WarpGateBuild warpGateBuild && warpGateBuild.toggle != this.toggle));

            if (entry >= entries.size) entry = 0;
            if (entry == entries.size - 1) {
                Building other = entries.get(entry);
                if (other == this) entry = 0;
            }

            for (int i = entry, len = entries.size; i < len; i++) {
                if (entries.get(i) instanceof WarpGateBuild warpGateBuild) {
                    WarpGate.WarpGateBuild other = warpGateBuild;
                    if (other != this) {
                        entry = i + 1;
                        if (!other.isValid() && teleporters[team.id][toggle].contains(other)) {
                            teleporters[team.id][toggle].remove(other);
                            return null;
                        }
                        if (!(other.OutputStackHold.total() >= other.block.itemCapacity) && other.isValid())
                            return other;
                    }
                }
                if (entry >= entries.size) entry = 0;
            }*/
            return null;
        }

        public void handleTransport(WarpGate.WarpGateBuild other) {
            int[] data = new int[content.items().size];
            int totalUsed = 0;
            float otherX0 = (otherX != 0) ? otherX : other.x();
            float otherY0 = (otherY != 0) ? otherY : other.y();

            Building tempOther = world.buildWorld(otherX0, otherY0);
            if (tempOther instanceof WarpGateBuild otherWarpBuild) {

                if ((items.total() <= 0 || toggle == -1) && otherWarpBuild != this) {
                    teleProgress %= 1f;
                    duration = 0f;
                    teleporting = onTransfer = false;
                    return;
                }

                teleportOutEffect.at(otherWarpBuild.x, otherWarpBuild.y, selection[toggle]);
                otherWarp = otherWarpBuild;
                otherWarpBuild.onTransfer = onTransfer = true;
                if (otherX != 0 && otherY != 0){
                    otherX = otherY = 0;
                }

                for (int i = 0; i < content.items().size; i++) {
                    int maxTransfer = Math.min(items.get(content.item(i)), tile.block().itemCapacity - totalUsed);
                    data[i] = maxTransfer;
                    totalUsed += maxTransfer;
                    items.remove(content.item(i), maxTransfer);
                }

                int totalItems = items.total();
                for (int i = 0; i < data.length; i++) {
                    int maxAdd = Math.min(data[i], itemCapacity * 2 - totalItems);
                    otherWarpBuild.OutputStackHold.add(content.item(i), maxAdd);
                    data[i] -= maxAdd;
                    totalItems += maxAdd;

                    if (totalItems >= itemCapacity * 2) {
                        break;
                    }
                }
                otherWarpBuild.onTransfer = onTransfer = false;

            } else {
                teleProgress %= 1f;
                duration = 0f;
                teleporting = onTransfer = false;
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return items.total() < itemCapacity && (toggle != -1);
        }

        @Override
        public void created() {
            if (toggle != -1) teleporters[team.id][toggle].add(this);
            previousTeam = team;
        }

        @Override
        public void onRemoved() {
            if (toggle != -1) {
                if (isTeamChanged()) teleporters[previousTeam.id][toggle].remove(this);
                else teleporters[team.id][toggle].remove(this);
            }
        }

        @Override
        public void onDestroyed() {
            super.onDestroyed();

            if (activeScl < 0.5f) return;

            float explosionRadius = 50;
            float explosionDamage = 20 * 66;
            Vec2 tr = new Vec2();

            Effect.shake(6f, 16f, this);
            RFx.nuclearShockwave.at(this);
            for (int i = 0; i < 6; i++) {
                Time.run(Mathf.random(40), () -> {
                    RFx.nuclearcloud.at(this);
                });
            }

            Damage.damage(tile.worldx(), tile.worldy(), explosionRadius * tilesize, explosionDamage * 4);

            for (int i = 0; i < 20; i++) {
                Time.run(Mathf.random(50), () -> {
                    tr.rnd(Mathf.random(40f));
                    RFx.explosion.at(tr.x + tile.worldx(), tr.y + tile.worldy());
                });
            }

            for (int i = 0; i < 70; i++) {
                Time.run(Mathf.random(80), () -> {
                    tr.rnd(Mathf.random(120f));
                    RFx.nuclearsmoke.at(tr.x + tile.worldx(), tr.y + tile.worldy());
                });
            }
        }

        @Override
        public Integer config() {
            return toggle;
        }

        @Override
        public byte version(){
            return 2;
        }

        @Override
        public void write(Writes write) { //TODO fix issues with loading saves
            super.write(write);
            write.b(toggle);
            write.bool(firstTime);
            //write.bool(onTransfer);
            float otherX = (otherWarp != null) ? otherWarp.x : 0f;
            float otherY = (otherWarp != null) ? otherWarp.y : 0f;

            write.f(otherX);
            write.f(otherY);

            Seq<Item> allItems = Vars.content.items();
            int itemSize = allItems.size;
            Object[] itemArray = allItems.items;

            for(int ii = 0; ii < itemSize; ++ii) {
                if (this.OutputStackHold.has(ii)) {
                    Item item = (Item) itemArray[ii];
                    write.b(this.OutputStackHold.get(item));
                } else {
                    write.b(0);
                }
            }

        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            if (revision == 0) { //for Build 9 - Build 11
                toggle = read.b();
                teleporting = read.bool();
            }

            if (revision == 1) { //for Build 12
                teleProgress %= 1f;
                duration = 0f;
                otherWarp = null;

                toggle = read.b();
                firstTime = read.bool();
                onTransfer = read.bool();
                otherX = read.f();
                otherY = read.f();

                if (toggle > 0) {
                    if (!teleporters[team.id][toggle].contains(this))
                        teleporters[team.id][toggle].add(this);
                    else {
                        teleporters[previousTeam.id][toggle].remove(this);
                        teleporters[team.id][toggle].add(this);
                    }
                }
                teleporting = false;

                Seq<Item> allItems = Vars.content.items();
                int itemSize = allItems.size;
                Object[] itemArray = allItems.items;

                for (int ii = 0; ii < itemSize; ++ii) {
                    Item item = (Item) itemArray[ii];
                    int val = read.b();
                    if (val > 0) OutputStackHold.add(item, val);
                }
            }

            if (revision == 2) { //for Build 13
                teleProgress %= 1f;
                duration = 0f;
                otherWarp = null;

                toggle = read.b();
                firstTime = read.bool();
                onTransfer = false;
                otherX = read.f();
                otherY = read.f();

                if (toggle > 0) {
                    if (!teleporters[team.id][toggle].contains(this))
                        teleporters[team.id][toggle].add(this);
                    else {
                        teleporters[previousTeam.id][toggle].remove(this);
                        teleporters[team.id][toggle].add(this);
                    }
                }
                teleporting = false;

                Seq<Item> allItems = Vars.content.items();
                int itemSize = allItems.size;
                Object[] itemArray = allItems.items;

                for (int ii = 0; ii < itemSize; ++ii) {
                    Item item = (Item) itemArray[ii];
                    int val = read.b();
                    if (val > 0) OutputStackHold.add(item, val);
                }
            }
        }
    }
}
