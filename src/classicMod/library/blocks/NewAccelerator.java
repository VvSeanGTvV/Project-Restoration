package classicMod.library.blocks;

import arc.*;
import arc.Graphics.*;
import arc.Graphics.Cursor.*;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import classicMod.content.ExtendedStat;
import classicMod.library.converter.*;
import classicMod.library.ui.dialog.epicCreditsDialog;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.units.UnitController;
import mindustry.game.EventType.*;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.PlanetDialog;
import mindustry.world.*;
import mindustry.world.blocks.ControlBlock;
import mindustry.world.meta.Stat;

import static arc.Core.camera;
import static classicMod.ClassicMod.getStatBundle;
import static classicMod.library.ui.UIExtended.fdelta;
import static mindustry.Vars.*;
import static mindustry.input.Binding.zoom;
import static mindustry.ui.dialogs.PlanetDialog.Mode.planetLaunch;
import static mindustry.ui.dialogs.PlanetDialog.Mode.select;

public class NewAccelerator extends Block{
    public TextureRegion arrowRegion = Core.atlas.find("launch-arrow");

    //TODO dynamic
    boolean launchingStartup, once, StartAnimation;
    public Block launching = Blocks.coreBastion;
    public Block requirementsBlock = Blocks.coreNucleus;

    public Sector Destination = SectorPresets.onset.sector;
    public int[] capacities = {};

    public float launchTime = 60f * 5f;

    public NewAccelerator(String name){
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        itemCapacity = 8000;
        configurable = true;
    }

    @Override
    public void init(){
        itemCapacity = 0;
        capacities = new int[content.items().size];
        for(ItemStack stack : requirementsBlock.requirements){
            capacities[stack.item.id] = stack.amount;
            itemCapacity += stack.amount;
        }
        consumeItems(requirementsBlock.requirements);
        super.init();
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    @Override
    public void setStats() {
        super.setStats();

        //stats.add(Stat.launchTime, launchTime);
        stats.add(ExtendedStat.launchPlanet, table -> {
            table.row();
            table.table(Styles.grayPanel, t -> {
                t.add(Destination.planet.localizedName).color(Destination.planet.iconColor).row();
                t.row();

                t.image(launching.uiIcon).size(40).left().scaling(Scaling.fit);
                t.table(coreInfo -> {
                    //coreInfo.image(launching.uiIcon).size(40).pad(2.5f).left().scaling(Scaling.fit);
                    coreInfo.add(getStatBundle.get("starting-core")).color(Pal.accent).row();
                    coreInfo.add(launching.localizedName);
                }).left().pad(10f);

                t.row();
                t.table(info -> {
                    info.add(Core.bundle.get("stat.launchtime")).row();
                    info.add(Strings.autoFixed(launchTime / 60f, 1) + " " + Core.bundle.get("unit.seconds")).color(Color.lightGray);
                }).left().pad(10f);

            });
        });
        //stats.add(Strings.autoFixed(launchTime / 60f, 1) + " " + Core.bundle.get("unit.seconds")).color(Color.lightGray);
    }

    public class NewAcceleratorBuild extends Building implements ControlBlock{
        public float heat, statusLerp, blockLerp, heatOpposite, progress;
        public @Nullable BlockUnitc unit;

        //counter
        public float launchAnimation, launchOppositeAnimation, zoomStyle, launcpadTimer;
        int stageLaunch = 0;
        public UnitController origin;
        public Unit originUnit;

        @Override
        public boolean shouldAutoTarget() {
            return false;
        }

        public float fraction(){ return progress / launchTime; }

        @Override
        public void updateTile(){
            super.updateTile();

            heat = Mathf.lerpDelta(heat, efficiency, 0.05f);
            statusLerp = Mathf.lerpDelta(statusLerp, power.status, 0.05f);
            if(efficiency > 0){
                if(isControlled()) {
                    progress += edelta();
                } else {
                    progress = 0;
                }
                if(heatOpposite < 1f) heatOpposite += fdelta(50f, 60f) / 50f;
                blockLerp = Mathf.clamp(Mathf.lerpDelta(blockLerp, heatOpposite, 0.05f));
            } else {
                progress = 0;
                heatOpposite = 0f;
                blockLerp = Mathf.clamp(Mathf.lerpDelta(blockLerp, efficiency, 0.05f));
                launchingStartup = false;
                once = false;
            }
            if(!StartAnimation) {
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

            if(progress >= launchTime && items.total() >= itemCapacity){
                if(mobile){
                    camera.position.set(this);
                }                       


                StartAnimation = true;
                //unit.spawnedByCore(false);
                renderer.setScale(Scl.scl(zoomStyle));
                launchAnimation = Mathf.clamp(launchAnimation + 0.0025f * Time.delta);
                if(launchAnimation >= 1f){ stageLaunch += 1; launchAnimation = 0f; }
                if(stageLaunch == 0){
                    launchOppositeAnimation = 1f;
                    zoomStyle = 1.5f;
                }
                if(stageLaunch == 1){
                    launchOppositeAnimation = Mathf.clamp(launchOppositeAnimation - 0.01f * Time.delta);
                    if(launchAnimation < 0.01f){ zoomStyle = 6f; Effect.shake(3f, 3f, this); } else {
                        if(zoomStyle > 1.5f) zoomStyle -= 0.025f * Time.delta;
                    }
                    launcpadTimer = Mathf.clamp(launchAnimation + 0.25f * Time.delta);
                }
                if(stageLaunch >= 2){

                    launcpadTimer = 0;
                    launchAnimation = 0;
                    StartAnimation = false;
                    progress = 0;
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
        public void draw(){
            super.draw();
            arrowRegion = Core.atlas.find("launch-arrow");

            for(int l = 0; l < 4; l++){
                float length = 7f + l * 5f;
                Draw.color(Tmp.c1.set(Pal.darkMetal).lerp(team.color, statusLerp), Pal.darkMetal, Mathf.absin(Time.time + l*50f, 10f, 1f));

                for(int i = 0; i < 4; i++){
                    float rot = i*90f + 45f;
                    Draw.rect(arrowRegion, x + Angles.trnsx(rot, length), y + Angles.trnsy(rot, length), rot + 180f);
                }
            }

            if(StartAnimation) {
                DrawAnimation(stageLaunch);
                return;
            }
            if(heat < 0.0001f) return;

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

            for(int i = 0; i < 4; i++){
                float rot = i*90f + 45f + (-Time.time /3f)%360f;
                float length = 26f * heat;
                Draw.rect(arrowRegion, x + Angles.trnsx(rot, length), y + Angles.trnsy(rot, length), rot + 180f);
            }

            Draw.reset();
        }

        public void DrawAnimation(int stageLaunchAnimation){
            Draw.reset();
            
            float rad = size * tilesize / 2f * 0.74f;

            if(stageLaunchAnimation == 0) {
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
                    var stroke = 1.75f * (2f * i);
                    var centre = i - (i * Mathf.clamp(launchAnimation));
                    Lines.stroke(stroke * Mathf.clamp(launchAnimation * bop), Pal.accent);
                    Lines.square(x, y + 10f * centre * centre, rad * 1.22f * i, 90f);
                }

                
            }

            Draw.reset();
            if(stageLaunchAnimation == 1){

                Draw.reset();

                Draw.z(Layer.bullet - 0.0001f);
                //Lines.stroke(1.75f, Pal.accent);
                //Lines.square(x, y, rad * 1.22f, 45f);
                for (int i = 1; i < 5; i++) {
                    var bop = i * 1.5f;
                    var stroke = 1.75f * (2f * i);
                    Lines.stroke(stroke * Mathf.clamp(launchOppositeAnimation * bop), Pal.accent);
                    Lines.square(x, y, rad * 1.22f * i, 90f);
                }

                Draw.z(Layer.bullet - 0.0001f);
                Lines.stroke(1.75f * launchOppositeAnimation, Pal.accent);
                Lines.square(x, y, rad * 1.22f * launchOppositeAnimation, 45f);

                DrawCoreLaunchLikeLaunchpod();

                
                //Drawf.additive(launching.uiIcon, bruh, x, y);
            }
        }


        float cx(){
            return x + Interp.pow2In.apply(launcpadTimer) * (12f + Mathf.randomSeedRange(id() + 3, 4f));
        }

        float cy(){
            return y + Interp.pow5In.apply(launcpadTimer) * (100f + Mathf.randomSeedRange(id() + 2, 30f));
        }

        float fslope(){
            return (0.5f - Math.abs(launcpadTimer - 0.5f)) * 2f;
        }

        public void DrawCoreLaunchLikeLaunchpod(){
            float alpha = Interp.pow5Out.apply(launcpadTimer);
            float scale = (1f - alpha) * 1.3f + 1f;
            float cx = cx(), cy = cy();
            float rotation = launcpadTimer * (130f + Mathf.randomSeedRange(id(), 50f));

            Draw.z(Layer.effect + 0.001f);

            Draw.color(Pal.engine);

            float rad = 0.2f + fslope();

            Fill.light(cx, cy, 10, 25f * (rad + scale-1f), Tmp.c2.set(Pal.engine).a(alpha), Tmp.c1.set(Pal.engine).a(0f));

            Draw.alpha(alpha);
            for(int i = 0; i < 4; i++){
                Drawf.tri(cx, cy, 6f, 40f * (rad + scale-1f), i * 90f + rotation);
            }

            Draw.color();

            Draw.z(Layer.weather - 1);

            TextureRegion region = launching.fullIcon;
            scale *= region.scl();
            float rw = region.width * scale, rh = region.height * scale;

            Draw.alpha(alpha);
            Draw.rect(region, cx, cy, rw, rh, rotation);

            Tmp.v1.trns(225f, Interp.pow3In.apply(launcpadTimer) * 260f);

            Draw.z(Layer.flyingUnit + 1);
            Draw.color(0, 0, 0, 0.22f * alpha);
            Draw.rect(region, cx + Tmp.v1.x, cy + Tmp.v1.y, rw, rh, rotation);

            Draw.reset();
        }

        public void DrawCore(){
            Draw.reset();
            Draw.alpha(Mathf.clamp(blockLerp * 12f));
            Draw.rect(launching.uiIcon, x, y);

            Color epic = new Color(team.color.r, team.color.g, team.color.b, 1f - Mathf.clamp(blockLerp * 3f));
            if(efficiency > 0) Drawf.additive(launching.uiIcon, epic, x, y);
            Draw.reset();
        }

        @Override
        public Cursor getCursor(){
            return !state.isCampaign() || efficiency <= 0f ? SystemCursor.arrow : super.getCursor();
        }

        public void StartNewPlanet(Sector to){
            if(control.saves.getCurrent() != null && Vars.state.isGame()){
                try{
                    control.saves.getCurrent().save();
                }catch(Throwable e){
                    e.printStackTrace();
                    ui.showException("[accent]" + Core.bundle.get("savefail"), e);
                }
            }
            Events.fire(new SectorLaunchEvent(to));
            control.playSector(to);
        }

        @Override
        public void buildConfiguration(Table table){
            //deselect();

            if(isControlled()) deselect();
            if(!state.isCampaign() || efficiency <= 0f) return;

            //ui.showInfo("This Block does not work or does not have a video implemented into this! Please check back for Update!");

            //ui.campaignComplete.show(Planets.serpulo);
            table.button(Icon.upOpen, Styles.cleari, () -> {
                //state.rules.sector.info.destination = Destination;
                //TODO cutscene, etc...

                launchingStartup = true;
                //Save before heading
                /*if(control.saves.getCurrent() != null && Vars.state.isGame()){
                    try{
                        control.saves.getCurrent().save();
                    }catch(Throwable e){
                        e.printStackTrace();
                        ui.showException("[accent]" + Core.bundle.get("savefail"), e);
                    }
                }
                Events.fire(new SectorLaunchEvent(Destination));
                control.playSector(Destination);
                */
                /*ui.planet.showPlanetLaunch(state.rules.sector, sector -> {
                    //TODO cutscene, etc...

                    //TODO should consume resources based on destination schem


                    consume();

                    universe.clearLoadoutInfo();
                    universe.updateLoadout(sector.planet.generator.defaultLoadout.findCore(), sector.planet.generator.defaultLoadout);
                });*/

                deselect();
            }).size(40f);

            Events.fire(Trigger.acceleratorUse);
        }

        @Override
        public int getMaximumAccepted(Item item){
            return capacities[item.id];
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return items.get(item) < getMaximumAccepted(item);
        }

        @Override
        public Unit unit(){
            if(unit == null){
                unit = (BlockUnitc) UnitTypes.block.create(team);
                unit.tile(this);
            }
            return (Unit)unit;
        }
    }
}
