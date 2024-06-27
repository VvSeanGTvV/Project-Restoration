package classicMod.library.blocks;

import arc.*;
import arc.Graphics.*;
import arc.Graphics.Cursor.*;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.Writes;
import classicMod.library.converter.*;
import classicMod.library.ui.dialog.epicCreditsDialog;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.units.UnitController;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.PlanetDialog;
import mindustry.world.*;
import mindustry.world.blocks.ControlBlock;

import static arc.Core.camera;
import static classicMod.library.ui.UIExtended.fdelta;
import static mindustry.Vars.*;
import static mindustry.input.Binding.zoom;
import static mindustry.ui.dialogs.PlanetDialog.Mode.select;

public class NewAccelerator extends Block{
    public TextureRegion arrowRegion = Core.atlas.find("launch-arrow");

    //TODO dynamic
    boolean launchingStartup, once;
    public Block launching = Blocks.coreBastion;
    public Block requirementsBlock = Blocks.coreNucleus;

    public Sector Destination = SectorPresets.onset.sector;
    public int[] capacities = {};

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

    public class NewAcceleratorBuild extends Building implements ControlBlock{
        public float heat, statusLerp, blockLerp, heatOpposite;
        public @Nullable BlockUnitc unit;
        public UnitController origin;
        public Unit originUnit;

        @Override
        public void updateTile(){
            super.updateTile();
            heat = Mathf.lerpDelta(heat, efficiency, 0.05f);
            statusLerp = Mathf.lerpDelta(statusLerp, power.status, 0.05f);
            if(efficiency > 0){
                if(heatOpposite < 1f) heatOpposite += fdelta(50f, 60f) / 50f;
                blockLerp = Mathf.clamp(Mathf.lerpDelta(blockLerp, heatOpposite, 0.05f));
            } else {
                heatOpposite = 0f;
                blockLerp = Mathf.clamp(Mathf.lerpDelta(blockLerp, efficiency, 0.05f));
                launchingStartup = false;
                once = false;
            }
            if(launchingStartup){
                if(!once) {

                    player.clearUnit();
                    unit.controller(player);

                    player.set(this);
                    camera.position.set(this);
                    once = true;
                }
                renderer.scaleCamera(4.5f * 4);
            }else{
                if(unit != null) {

                    if (origin == null) {
                        origin = unit.controller();
                        originUnit = player.unit();
                    } else {
                        unit.controller(origin);
                        unit.spawnedByCore(true);
                    }
                }
                //unit.controller();
            }
        }

        @Override
        public void draw(){
            super.draw();

            for(int l = 0; l < 4; l++){
                float length = 7f + l * 5f;
                Draw.color(Tmp.c1.set(Pal.darkMetal).lerp(team.color, statusLerp), Pal.darkMetal, Mathf.absin(Time.time + l*50f, 10f, 1f));

                for(int i = 0; i < 4; i++){
                    float rot = i*90f + 45f;
                    Draw.rect(arrowRegion, x + Angles.trnsx(rot, length), y + Angles.trnsy(rot, length), rot + 180f);
                }
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

        //a
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

        @Override
        public void buildConfiguration(Table table){
            //deselect();

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
        public void write(Writes write) {
            super.write(write);
            launchingStartup = false;
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