package classicMod.library.blocks.legacyBlocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import classicMod.library.ai.RallyAI;
import classicMod.library.ai.ReplacementFlyingAI;
import classicMod.library.ai.ReplacementGroundAI;
import mindustry.ai.types.FlyingAI;
import mindustry.ai.types.GroundAI;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Unit;
import mindustry.ui.Styles;
import mindustry.world.Block;

import java.util.Objects;

import static classicMod.content.ClassicVars.CommandOrigin;
import static classicMod.content.ClassicVars.MaximumRangeCommand;
import static classicMod.content.ExtendedFx.commandSend;

public class LegacyCommandCenter extends Block {
    public TextureRegion topRegion = Core.atlas.find(name+"-top");

    protected Unit[] ArrayTarget;

    public LegacyCommandCenter(String name) {
        super(name);

        update = true;
        solid = true;
        configurable = true;
    }

    @Override
    public void setStats(){
        super.setStats();

        //stats.add(Stat.range, MaximumRangeCommand / tilesize, StatUnit.blocks);
    }


    public class LegacyCommandCenterBuild extends Building {
        public String CommandSelect = "attack";
        public Seq<Unit> targets = new Seq<>();
        public Seq<Building> CommandCenterArea = new Seq<>();
        public float blockID;

        @Override
        public void buildConfiguration(Table table) {
            if(blockID==0f)blockID=Mathf.randomSeed(this.id) * 120;
            Table buttons = new Table();
            buttons.button(Icon.commandAttack, Styles.cleari, () -> {
                UpdateCommand(RallyAI.UnitState.attack);
                CommandOrigin = "attack";
            });
            buttons.button(Icon.commandRally, Styles.cleari, () -> {
                UpdateCommand(RallyAI.UnitState.rally);
                CommandOrigin = "rally";
            });
            table.add(buttons);
        }

        @Override
        public void draw() {
            super.draw();
            TextureRegion c = Core.atlas.find(name+"-"+CommandOrigin);

            Draw.color(team.color);
            //Draw.rect(topRegion, x, y);
            if(c != null)Draw.rect(c, x, y);
            Draw.color(Color.valueOf("5e5e5e"));
            if(c != null)Draw.rect(c, x, y - 1);
            Draw.reset();
        }
        public void UpdateCommand(RallyAI.UnitState State){
            commandSend.at(this);

            CommandCenterArea.clear();
            Units.nearbyBuildings(x, y, MaximumRangeCommand, b -> {
                if(b instanceof LegacyCommandCenterBuild){
                    CommandCenterArea.add(b);
                }
            });

            targets.clear();
            Units.nearby(team, x, y, MaximumRangeCommand, u -> {
                if(u.controller() instanceof RallyAI){
                    targets.add(u);
                }
            });

            for (var build : CommandCenterArea){
                if(build instanceof LegacyCommandCenterBuild b){
                    Log.info(b);
                    Log.info(CommandOrigin);
                }
            }

            for (var target : targets){
                if(target.controller() instanceof RallyAI ai){
                    ai.state = State;
                    ai.lastCommandCenterID = blockID;
                }else{
                    rewriteUnit(target);
                }
            }
        }

        public void rewriteUnit(Unit u){
            if(!(u.controller() instanceof GroundAI) || !(u.controller() instanceof FlyingAI)) return;
            if(u.isFlying())u.controller(new ReplacementFlyingAI());
            if(!u.isFlying())u.controller(new ReplacementGroundAI());
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.str(CommandOrigin);
            write.f(blockID);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            CommandOrigin = read.str();
            blockID = read.f();
        }
    }
}
