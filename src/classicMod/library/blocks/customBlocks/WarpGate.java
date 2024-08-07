package classicMod.library.blocks.customBlocks;

import arc.*;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import classicMod.content.*;
import classicMod.uCoreGraphics.uCoreLines;
import mindustry.content.Liquids;
import mindustry.entities.*;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType.WorldLoadEvent;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.Block;
import mindustry.world.meta.StatUnit;

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

    public float warmupTime = 60f;
    /**
     * time between Teleports
     **/
    public float teleportMax = 500f;
    public float powerUse = 0.3f;
    public float teleportLiquidUse = 0.3f;
    public float liquidUse = 0.1f;
    public Liquid inputLiquid;
    protected Effect activateEffect = ExtendedFx.teleportActivate;
    protected Effect teleportEffect = ExtendedFx.teleport;
    protected Effect teleportOutEffect = ExtendedFx.teleportOut;
    protected TextureRegion blankRegion;
    private float powerMulti;
    private float TYPE;

    public WarpGate(String name) {
        super(name);
        itemCapacity = 100;
        update = true;
        solid = true;
        configurable = true;
        saveConfig = true;
        unloadable = false;
        hasItems = true;
        Events.on(WorldLoadEvent.class, e -> {
            for (int i = 0; i < teleporters.length; i++) {
                for (int j = 0; j < teleporters[i].length; j++) teleporters[i][j].clear();
            }
        });
        config(Integer.class, (WarpGate.WarpGateBuild build, Integer value) -> {
            if (build.toggle != -1) teleporters[build.team.id][build.toggle].remove(build);
            if (value != -1) teleporters[build.team.id][value].add(build);
            build.toggle = value;
        });
        configClear((WarpGate.WarpGateBuild build) -> {
            if (build.toggle != -1) teleporters[build.team.id][build.toggle].remove(build);
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
        consumePowerCond(powerUse + powerMulti, WarpGate.WarpGateBuild::isConsuming);
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
        protected float duration;
        protected boolean teleporting;
        protected float activeScl;
        protected @Nullable ItemStack[] itemStacks;
        protected WarpGate.WarpGateBuild target;
        protected Team previousTeam;
        protected boolean firstTime;
        /**
         * is this specific building avaliable and able to transport
         **/
        protected boolean transportable;
        protected boolean OnTransport; // Is already on Transport
        protected float lastDuration;

        protected void onDuration() {
            if (duration < 0f && !teleporting) duration = teleportMax;
            else duration -= Time.delta;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return super.acceptLiquid(source, liquid) && liquid == inputLiquid;
        }

        public float fraction() {
            return (teleportMax - duration) / teleportMax;
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
                //if(liquids.get(inputLiquid) <= 0f) catastrophicFailure();
                activeScl = Mathf.lerpDelta(activeScl, 1f, 0.015f);
                duration = lastDuration;
                if (teleporting) {
                    lastDuration = 0f;
                    duration = teleportMax;
                }
                if (items.total() >= itemCapacity) {
                    onDuration();
                } else {
                    duration = teleportMax;
                }
                if (firstTime) {
                    if (toggle != -1) activateEffect.at(this.x, this.y, selection[toggle]);
                    firstTime = false;
                }
                if (!teleporting && this.items.total() >= itemCapacity && duration <= 1f) {
                    powerMulti = Math.min(this.block.consPower.capacity, powerUse * Time.delta);
                    //consumeLiquid(inputLiquid, teleportLiquidUse);
                    if (toggle != -1) {
                        if (!teleporting) {
                            teleportEffect.at(this.x, this.y, selection[toggle]);
                            teleporting = true;
                        }
                        Time.run(warmupTime, () -> {
                            WarpGate.WarpGateBuild other = findLink(toggle);
                            if (this.items.total() <= 0 || other == null || toggle == -1) {
                                Time.clear(); //remove timer, when interrupted or has nothujg in it.
                                teleporting = false;
                            }
                            if (other != null) {
                                if (!other.transportable) {
                                    Time.clear();
                                    teleporting = false;
                                }
                                teleportOutEffect.at(this.x, this.y, selection[toggle]);
                                handleTransport(other);
                                teleportOutEffect.at(other.x, other.y, selection[toggle]);
                            }
                        });
                    }
                    if (isTeamChanged() && toggle != -1) {
                        teleporters[team.id][toggle].add(this);
                        ExtendedFx.teleport.at(this.x, this.y, selection[toggle]);
                        Time.run(warmupTime, () -> {
                            //remove waiting shooters, it's done firing
                            teleporters[previousTeam.id][toggle].remove(this);
                            previousTeam = team;
                        });
                    }
                    duration = teleportMax;
                }
            } else {
                if (efficiency > 0f && activeScl > 0) activeScl = 0;
                if (toggle == -1 && activeScl > 0) activeScl = 0;
                activeScl = Mathf.lerpDelta(activeScl, 0f, 0.01f);
                firstTime = true;
                duration = teleportMax;
            }
            //if(!liquids.hasFlowLiquid(inputLiquid) && this.block.consPower.efficiency(this)>=1) catastrophicFailure();
            if (items.any()) dump();
            transportable = !(items.total() >= this.block.itemCapacity); //prevent buildings from having too much items in single block.
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
            table.row();
            ImageButton Transport = table.button(Icon.up, Styles.clearTogglei, 24f, () -> {
            }).size(34f).group(group).get();
            ImageButton Unload = table.button(Icon.down, Styles.clearTogglei, 24f, () -> {
            }).size(34f).group(group).get();

            Transport.changed(() -> configure(Transport.isChecked() ? TYPE : -1));
            Transport.update(() -> Transport.setChecked(TYPE == 1));

            Unload.changed(() -> configure(Unload.isChecked() ? TYPE : -1));
            Unload.update(() -> Unload.setChecked(TYPE == 2));
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
                    if (other.transportable) return other;
                }
            }
            return null;
        }

        public void handleTransport(WarpGate.WarpGateBuild other) {
            teleporting = true;
            int[] data = new int[content.items().size];
            int totalUsed = 0;
            if (other == null) other = findLink(toggle);
            for (int i = 0; i < content.items().size; i++) {
                int maxTransfer = Math.min(items.get(content.item(i)), tile.block().itemCapacity - totalUsed);
                data[i] = maxTransfer;
                totalUsed += maxTransfer;
                items.remove(content.item(i), maxTransfer);
            }

            int totalItems = items.total();
            for (int i = 0; i < data.length; i++) {
                int maxAdd = Math.min(data[i], itemCapacity * 2 - totalItems);
                other.items.add(content.item(i), maxAdd);
                data[i] -= maxAdd;
                totalItems += maxAdd;

                if (totalItems >= itemCapacity * 2) {
                    break;
                }
            }
            itemStacks = null; //set to null after finishing transport
            teleporting = false;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            if (toggle == -1) return false;
            target = findLink(toggle);
            if (target == null) return false;
            return source != this && canConsume() && items.total() < itemCapacity;
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
            ExtendedFx.nuclearShockwave.at(this);
            for (int i = 0; i < 6; i++) {
                Time.run(Mathf.random(40), () -> {
                    ExtendedFx.nuclearcloud.at(this);
                });
            }

            Damage.damage(tile.worldx(), tile.worldy(), explosionRadius * tilesize, explosionDamage * 4);

            for (int i = 0; i < 20; i++) {
                Time.run(Mathf.random(50), () -> {
                    tr.rnd(Mathf.random(40f));
                    ExtendedFx.explosion.at(tr.x + tile.worldx(), tr.y + tile.worldy());
                });
            }

            for (int i = 0; i < 70; i++) {
                Time.run(Mathf.random(80), () -> {
                    tr.rnd(Mathf.random(120f));
                    ExtendedFx.nuclearsmoke.at(tr.x + tile.worldx(), tr.y + tile.worldy());
                });
            }
        }

        @Override
        public Integer config() {
            return toggle;
        }

        @Override
        public void write(Writes write) { //TODO fix issues with loading saves
            super.write(write);
            write.b(toggle);
            write.bool(teleporting);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            toggle = read.b();
            teleporting = read.bool();
        }
    }
}
