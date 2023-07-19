package classicMod.library.blocks.legacyBlocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.Button;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import classicMod.content.ExtendedStat;
import classicMod.library.ai.RallyAI;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;

import static classicMod.content.ExtendedFx.commandSend;
import static mindustry.Vars.tilesize;

public class LegacyCommandCenter extends Block {
    public static float MaximumRangeCommand = 120f;
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

        stats.add(Stat.range, MaximumRangeCommand / tilesize, StatUnit.blocks);
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
                CommandSelect = "attack";
            });
            buttons.button(Icon.commandRally, Styles.cleari, () -> {
                UpdateCommand(RallyAI.UnitState.rally);
                CommandSelect = "rally";
            });
            table.add(buttons);
        }

        @Override
        public void draw() {
            super.draw();
            TextureRegion c = Core.atlas.find(name+"-"+CommandSelect);

            Draw.color(team.color);
            //Draw.rect(topRegion, x, y);
            Draw.rect(c, x, y);
            Draw.reset();
        }
        public void UpdateCommand(RallyAI.UnitState State){
            commandSend.at(this);

            CommandCenterArea.clear();
            Units.nearbyBuildings(x, y, Float.MAX_VALUE, b -> {
                if(b instanceof LegacyCommandCenterBuild){
                    CommandCenterArea.add(b);
                }
            });

            targets.clear();
            Units.nearby(team, x, y, Float.MAX_VALUE, u -> {
                if(u.controller() instanceof RallyAI){
                    targets.add(u);
                }
            });

            for (var build : CommandCenterArea){
                if(build instanceof LegacyCommandCenterBuild b){
                    b.CommandSelect = CommandSelect;
                    Log.info(b);
                    Log.info(b.CommandSelect);
                }
            }

            for (var target : targets){
                if(target.controller() instanceof RallyAI ai){
                    ai.state = State;
                    ai.lastCommandCenterID = blockID;
                }
            }
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.str(CommandSelect);
            write.f(blockID);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            CommandSelect = read.str();
            blockID = read.f();
        }
    }
}
