package classicMod.library.blocks;

import arc.*;
import arc.Graphics.Cursor;
import arc.Graphics.Cursor.SystemCursor;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import classicMod.content.ExtendedStat;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.units.UnitController;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.ControlBlock;

import static arc.Core.*;
import static classicMod.ClassicMod.getStatBundle;
import static classicMod.library.ui.UIExtended.fdelta;
import static mindustry.Vars.*;

public class NewAccelerator extends Block {
    protected static final float[] thrusterSizes = {0f, 0f, 0.15f, 0.3f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f};
    public TextureRegion arrowRegion = Core.atlas.find("launch-arrow");
    public Block launching = Blocks.coreBastion;
    public Block requirementsBlock = Blocks.coreNucleus;

    public Sector Destination = SectorPresets.onset.sector;
    public int[] capacities = {};

    public float launchTime = 60f * 10f;

    public float thrusterLength = 14f / 4f;
    //TODO dynamic
    boolean launchingStartup, once, StartAnimation;

    public NewAccelerator(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        itemCapacity = 8000;
        configurable = true;
    }

    @Override
    public void init() {
        itemCapacity = 0;
        capacities = new int[content.items().size];
        for (ItemStack stack : requirementsBlock.requirements) {
            capacities[stack.item.id] = stack.amount;
            itemCapacity += stack.amount;
        }
        consumeItems(requirementsBlock.requirements);
        super.init();
    }

    @Override
    public boolean outputsItems() {
        return false;
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(ExtendedStat.launchSector, table -> {
            table.row();
            table.table(Styles.grayPanel, t -> {
                t.table(infoSector -> {
                    infoSector.add(getStatBundle.get("sector-land")).row();
                    //infoSector.image(Icon.icons.get(Destination.planet.icon + "Small", Icon.icons.get(Destination.planet.icon, Icon.commandRallySmall))).color(Destination.planet.iconColor);
                    infoSector.table(details -> {
                        details.image(Icon.icons.get(Destination.planet.icon + "Small", Icon.icons.get(Destination.planet.icon, Icon.commandRallySmall))).size(16f).pad(2.5f).color(Destination.planet.iconColor);
                        details.add(Destination.planet.localizedName).color(Destination.planet.iconColor);
                        details.add(" : ");
                        details.add(Destination.preset.localizedName).row();
                    });
                }).left().pad(10f).row();

                t.table(coreInfo -> {
                    coreInfo.image(launching.uiIcon).size(40).pad(2.5f).left().scaling(Scaling.fit);
                    coreInfo.table(info -> {
                        info.add(getStatBundle.get("starting-core")).color(Pal.accent).row();
                        info.add(launching.localizedName);
                    });
                }).left().pad(10f).row();

                t.table(info -> {
                    info.add(getStatBundle.get("sector-launchtime")).row();
                    info.add(Strings.autoFixed(launchTime / 60f, 1) + " " + Core.bundle.get("unit.seconds")).color(Color.lightGray);
                }).left().pad(10f).row();
            });
        });
        //stats.add(Strings.autoFixed(launchTime / 60f, 1) + " " + Core.bundle.get("unit.seconds")).color(Color.lightGray);
    }

    public class NewAcceleratorBuild extends Building implements ControlBlock {
        public float heat, statusLerp, blockLerp, heatOpposite, progress;
        public @Nullable BlockUnitc unit;
        //counter
        public float launchAnimation, launchOppositeAnimation, zoomStyle, launchpadTimer, launchpadPrepTimer, shockwaveTimer;
        public UnitController origin;
        public Unit originUnit;
        float originMinZoom, originMaxZoom;
        int stageLaunch = 0;
        boolean reset;

        @Override
        public boolean shouldAutoTarget() {
            return false;
        }

        public float fraction() {
            return progress / launchTime;
        }

        @Override
        public void updateTile() {
            super.updateTile();

            if (reset) {
                launchpadTimer = 0;
                launchAnimation = 0;
                StartAnimation = false;
                progress = 0;
                reset = false;
            }

            heat = Mathf.lerpDelta(heat, efficiency, 0.05f);
            statusLerp = Mathf.lerpDelta(statusLerp, power.status, 0.05f);
            if (efficiency > 0) {
                if (isControlled()) {
                    progress += edelta();
                } else {
                    progress = 0;
                }
                if (heatOpposite < 1f) heatOpposite += fdelta(50f, 60f) / 50f;
                blockLerp = Mathf.clamp(Mathf.lerpDelta(blockLerp, heatOpposite, 0.05f));
            } else {
                progress = 0;
                heatOpposite = 0f;
                blockLerp = Mathf.clamp(Mathf.lerpDelta(blockLerp, efficiency, 0.05f));
                launchingStartup = false;
                once = false;
            }
            if (!StartAnimation) {
                if (launchingStartup || isControlled()) {
                    if (!once || mobile) {
                        if (!once) {
                            player.clearUnit();
                            unit.controller(player);

                            player.set(this);
                            once = true;
                        }
                        camera.position.set(this);
                    }

                    if (isControlled()) renderer.setScale(Scl.scl(6f));
                } else {
                    if (unit != null) {
                        if (origin == null) {
                            origin = unit.controller();
                            originUnit = player.unit();
                        } else {
                            unit.controller(origin);
                        }
                    }
                    //unit.controller();
                }
                if ((launchingStartup || once) && !isControlled()) {
                    launchingStartup = false;
                    once = false;
                }
            }
            unit.ammo(unit.type().ammoCapacity * fraction());

            if (progress >= launchTime && items.total() >= itemCapacity) {
                if (originMinZoom == 0 || originMaxZoom == 0) {
                    originMinZoom = renderer.minZoom;
                    originMaxZoom = renderer.maxZoom;
                }
                if (mobile) {
                    camera.position.set(this);
                }


                StartAnimation = true;
                //unit.spawnedByCore(false);
                renderer.minZoom = Scl.scl(0.02f);
                renderer.maxZoom = Scl.scl(6f);
                renderer.setScale(Scl.scl(zoomStyle));
                var maxScaleZoom = (Vars.mobile) ? Core.graphics.getAspect() : 4f;
                launchAnimation = Mathf.clamp(launchAnimation + 0.0025f * Time.delta);
                if (launchAnimation >= 1f && stageLaunch < 1) {
                    stageLaunch += 1;
                    launchAnimation = 0f;
                }
                if (stageLaunch == 0) {
                    launchOppositeAnimation = 1f;
                    zoomStyle = Interp.pow3In.apply(Scl.scl(2f), Scl.scl(maxScaleZoom), Mathf.clamp(launchAnimation * 6f));
                }
                if (stageLaunch == 1) {
                    launchOppositeAnimation = Mathf.clamp(launchOppositeAnimation - 0.01f * Time.delta);
                    if (launchAnimation < 0.01f) {
                        Effect.shake(3f, 3f, this);
                        settings.put("unlocks" + "-launched-planetary", true);
                    }

                    zoomStyle = Interp.pow3In.apply(Scl.scl(0.02f), Scl.scl(maxScaleZoom), Mathf.clamp(1f - launchpadTimer * 2f));
                    launchpadPrepTimer = Mathf.clamp(launchpadPrepTimer + 0.0035f * Time.delta);

                    if (launchpadPrepTimer >= 0.25f)
                        launchpadTimer = Mathf.clamp(launchpadTimer + 0.0075f * Time.delta);
                    if (launchpadTimer * 2f >= 0.85f) stageLaunch += 1;
                    if (launchpadTimer >= 0.25f) shockwaveTimer = Mathf.clamp(launchpadTimer + 0.01f * Time.delta);
                }
                if (stageLaunch >= 2) {
                    StartNewPlanet(Destination);
                }
            } else if (progress <= 0 && StartAnimation) {
                player.clearUnit();
                unit.controller(player);

                player.set(this);
                progress = launchTime;
                //StartAnimation = false;
                //launchAnimation = 0f;
            }
        }

        @Override
        public void draw() {
            super.draw();
            arrowRegion = Core.atlas.find("launch-arrow");

            for (int l = 0; l < 4; l++) {
                float length = 7f + l * 5f;
                Draw.color(Tmp.c1.set(Pal.darkMetal).lerp(team.color, statusLerp), Pal.darkMetal, Mathf.absin(Time.time + l * 50f, 10f, 1f));

                for (int i = 0; i < 4; i++) {
                    float rot = i * 90f + 45f;
                    Draw.rect(arrowRegion, x + Angles.trnsx(rot, length), y + Angles.trnsy(rot, length), rot + 180f);
                }
            }

            if (StartAnimation) {
                DrawAnimation(stageLaunch);
                return;
            }
            if (heat < 0.0001f) return;

            DrawCore();

            float rad = size * tilesize / 2f * 0.74f;
            float scl = 2f;

            Draw.z(Layer.bullet - 0.0001f);
            Lines.stroke(1.75f * heat, Pal.accent);
            Lines.square(x, y, rad * 1.22f, 45f);

            Lines.stroke(3f * heat, Pal.accent);
            Lines.square(x, y, rad, Time.time / scl);
            Lines.square(x, y, rad, -Time.time / scl);

            Draw.color(team.color);
            Draw.alpha(Mathf.clamp(heat * 3f));

            for (int i = 0; i < 4; i++) {
                float rot = i * 90f + 45f + (-Time.time / 3f) % 360f;
                float length = 26f * heat;
                Draw.rect(arrowRegion, x + Angles.trnsx(rot, length), y + Angles.trnsy(rot, length), rot + 180f);
            }

            Draw.reset();
        }

        public void DrawAnimation(int stageLaunchAnimation) {
            Draw.reset();

            float rad = size * tilesize / 2f * 0.74f;
            float scl = 2f;

            float strokeScaling = 1.75f;
            float warpSquareStroke = 1.25f;

            if (stageLaunchAnimation == 0) {
                Draw.rect(launching.uiIcon, x, y);

                Color epic = new Color(team.color.r, team.color.g, team.color.b, Mathf.clamp(launchAnimation));
                Drawf.additive(launching.uiIcon, epic, x, y);

                Color woah = new Color(team.color.r, team.color.g, team.color.b, Mathf.clamp(launchAnimation * 3f));
                Drawf.additive(launching.uiIcon, woah, x, y);

                Draw.z(Layer.bullet - 0.0001f);

                Color bruh = new Color(team.color.r, team.color.g, team.color.b, Mathf.clamp(launchAnimation));
                Draw.color(bruh);
                Draw.rect(launching.uiIcon, x, y);

                Color glow = new Color(team.color.r, team.color.g, team.color.b, Mathf.clamp(launchAnimation * 1.5f));
                Draw.color(glow);
                Draw.rect(launching.uiIcon, x, y);

                Lines.stroke(1.75f, Pal.accent);
                Lines.square(x, y, rad * 1.22f, 45f);
                for (int i = 1; i < 5; i++) {
                    var bop = i * 1.5f;
                    var stroke = warpSquareStroke * (strokeScaling * i);
                    var centre = i - (i * Mathf.clamp(launchAnimation));
                    Lines.stroke(stroke * Mathf.clamp(launchAnimation * 2f / bop), Pal.accent);
                    Lines.square(x, y + 10f * centre * centre, rad * 1.22f * i, 90f);
                }

                var Opposite = 1f - Mathf.clamp(launchAnimation * 3f);

                Lines.stroke(1.75f * Opposite, Pal.accent);
                Lines.square(x, y, rad * 1.22f * Opposite, 45f);

                Lines.stroke(1.75f * Opposite, Pal.accent);
                Lines.square(x, y, rad * 1.22f, 45f);

                Lines.stroke(3f * Opposite, Pal.accent);
                Lines.square(x, y, rad, Time.time / scl);
                Lines.square(x, y, rad, -Time.time / scl);

                Draw.color(team.color);
                Draw.alpha(Mathf.clamp(Opposite * 3f));

                for (int i = 0; i < 4; i++) {
                    float rot = i * 90f + 45f + (-Time.time / 3f) % 360f;
                    float length = 26f * Opposite;
                    Draw.rect(arrowRegion, x + Angles.trnsx(rot, length), y + Angles.trnsy(rot, length), rot + 180f);
                }


            }

            Draw.reset();
            if (stageLaunchAnimation == 1) {

                Draw.reset();

                var Opposite = (1f - Mathf.clamp(launchOppositeAnimation));

                Draw.z(Layer.bullet - 0.0001f);
                for (int i = 1; i < 5; i++) {
                    var bop = i * 1.5f;
                    var stroke = warpSquareStroke * (strokeScaling * i);
                    //var centre = i - (i * Mathf.clamp(Opposite));
                    Lines.stroke(stroke * Mathf.clamp(Opposite * 2f / bop), Pal.accent);
                    Lines.square(x, y, (rad * 1.22f * i) * Mathf.clamp(Opposite * 2f / bop), 90f);
                }

                for (int i = 1; i < 5; i++) {
                    var bop = i * 1.5f;
                    var stroke = warpSquareStroke * (strokeScaling * i);
                    //var centre = i - (i * Mathf.clamp(Opposite));
                    Lines.stroke(stroke * Mathf.clamp(launchOppositeAnimation * 2f / bop), Pal.accent);
                    Lines.square(x, y, (rad * 1.22f * i) * Mathf.clamp(launchOppositeAnimation * 2f / bop), 90f);
                }

                Draw.reset();
                DrawCoreLaunch();

                //Drawf.additive(launching.uiIcon, bruh, x, y);
            }
        }


        /*float cx(){
            return x + Interp.pow2In.apply(launchpadTimer) * (12f + Mathf.randomSeedRange(id() + 3, 4f));
        }

        float cy(){
            return y + Interp.sineIn.apply(launchpadTimer) * (100f + Mathf.randomSeedRange(id() + 2, 30f));
        }

        float fslope(){
            return (0.5f - Math.abs(launchpadTimer - 0.5f)) * 2f;
        }*/

        //damn
        public void DrawCoreLaunch() {

            float thrustTimer = Interp.pow2In.apply(Mathf.clamp(launchpadPrepTimer));
            //float cx = cx(), cy = y;
            float rotation = launchpadTimer * (130f + Mathf.randomSeedRange(id(), 50f));
            float thrustOpen = 0.25f;
            float thrusterFrame = thrustTimer >= thrustOpen ? 1f : thrustTimer / thrustOpen;
            float scl = Scl.scl(4f) / renderer.getDisplayScale();

            Draw.z(Layer.weather - 1);

            float thrusterSize = Mathf.sample(thrusterSizes, launchpadPrepTimer);

            float size = launching.size;
            float strength = (1f + (size - 3) / 2.5f) * scl * thrusterSize * (0.95f + Mathf.absin(2f, 0.1f));
            float offset = (size - 3) * 3f * scl;

            for (int i = 0; i < 4; i++) {
                Tmp.v1.trns(i * 90 + rotation, 1f);

                Tmp.v1.setLength((size * tilesize / 2f + 1f) * scl + strength * 2f + offset);
                Draw.color(Pal.accent);
                Fill.circle(Tmp.v1.x + x, Tmp.v1.y + y, 6f * strength);

                Tmp.v1.setLength((size * tilesize / 2f + 1f) * scl + strength * 0.5f + offset);
                Draw.color(Color.white);
                Fill.circle(Tmp.v1.x + x, Tmp.v1.y + y, 3.5f * strength);
            }

            Draw.scl(scl);

            drawLandingThrusters(x, y, rotation, thrusterFrame);

            Drawf.spinSprite(launching.fullIcon, x, y, rotation);

            Draw.alpha(Interp.pow4In.apply(thrusterFrame));
            drawLandingThrusters(x, y, rotation, thrusterFrame);
            Draw.alpha(1f);

            drawShockwave(x, y, scl, 6f, 50f, Mathf.clamp(shockwaveTimer * 4f));
            drawShockwave(x, y, scl, 3f, 50f, Mathf.clamp(shockwaveTimer * 3f));

            if (launchpadPrepTimer >= 0.05f) {
                tile.getLinkedTiles(t -> {
                    if (Mathf.chance(0.4f * Mathf.clamp(launchpadTimer * 1.5f))) {
                        Fx.coreLandDust.at(t.worldx(), t.worldy(), angleTo(t.worldx(), t.worldy()) + Mathf.range(30f), Tmp.c1.set(t.floor().mapColor).mul(1.5f + Mathf.range(0.15f)));
                    }
                });
            }

            Draw.color();
            Draw.scl();
            Draw.reset();
        }

        public void drawShockwave(float x, float y, float scl, float radOffset, float thick, float frame) {
            if (!(frame <= 0)) {
                var opposite = 1f - frame;
                Draw.alpha(opposite);
                circles(x, y, radOffset * (size * tilesize / 2f + 1f) * scl * frame, thick * opposite, Color.white);
                Draw.alpha(1f);
            }
        }

        public void circles(float x, float y, float rad, float thickness, Color color) {
            Lines.stroke(thickness, color);
            Lines.circle(x, y, rad);
        }

        protected void drawLandingThrusters(float x, float y, float rotation, float frame) {
            float length = thrusterLength * (frame - 1f) - 1f / 4f;
            float alpha = Draw.getColor().a;

            TextureRegion thruster1 = Core.atlas.find(launching.name + "-thruster1");
            TextureRegion thruster2 = Core.atlas.find(launching.name + "-thruster2");

            //two passes for consistent lighting
            for (int j = 0; j < 2; j++) {
                for (int i = 0; i < 4; i++) {
                    var reg = i >= 2 ? thruster2 : thruster1;
                    float rot = (i * 90) + rotation % 90f;
                    Tmp.v1.trns(rot, length * Draw.xscl);

                    //second pass applies extra layer of shading
                    if (j == 1) {
                        Tmp.v1.rotate(-90f);
                        Draw.alpha((rotation % 90f) / 90f * alpha);
                        rot -= 90f;
                        Draw.rect(reg, x + Tmp.v1.x, y + Tmp.v1.y, rot);
                    } else {
                        Draw.alpha(alpha);
                        Draw.rect(reg, x + Tmp.v1.x, y + Tmp.v1.y, rot);
                    }
                }
            }
            Draw.alpha(1f);
        }

        public void DrawCore() {
            Draw.reset();
            Draw.alpha(Mathf.clamp(blockLerp * 12f));
            Draw.rect(launching.uiIcon, x, y);

            Draw.z(Layer.effect + 0.001f);
            Color epic = new Color(team.color.r, team.color.g, team.color.b, 1f - Mathf.clamp(blockLerp * 3f));
            if (efficiency > 0) {
                Drawf.additive(launching.uiIcon, epic, x, y);
            }
            Draw.reset();
        }

        @Override
        public Cursor getCursor() {
            return !state.isCampaign() || efficiency <= 0f ? SystemCursor.arrow : super.getCursor();
        }

        public void StartNewPlanet(Sector to) {
            if (control.saves.getCurrent() != null && Vars.state.isGame()) {
                try {
                    control.saves.getCurrent().save();
                } catch (Throwable e) {
                    e.printStackTrace();
                    ui.showException("[accent]" + Core.bundle.get("savefail"), e);
                }
            }

            renderer.minZoom = originMinZoom;
            renderer.maxZoom = originMaxZoom;
            originMaxZoom = originMinZoom = 0;

            Events.fire(new SectorLaunchEvent(to));
            control.playSector(to);
            reset = true;
        }

        @Override
        public void buildConfiguration(Table table) {
            if (isControlled()) deselect();
            if (!state.isCampaign() || efficiency <= 0f) return;
            table.button(Icon.upOpen, Styles.cleari, () -> {
                launchingStartup = true;
                deselect();
            }).size(40f);

            Events.fire(Trigger.acceleratorUse);
        }

        @Override
        public int getMaximumAccepted(Item item) {
            return capacities[item.id];
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return items.get(item) < getMaximumAccepted(item);
        }

        @Override
        public Unit unit() {
            if (unit == null) {
                unit = (BlockUnitc) UnitTypes.block.create(team);
                unit.tile(this);
            }
            return (Unit) unit;
        }
    }
}
