package classicMod.library.blocks.legacyBlocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.Button;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import classicMod.library.ai.RallyAI;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.ui.Styles;
import mindustry.world.Block;

import java.lang.reflect.Array;

public class LegacyCommandCenter extends Block {
    public float MaximumRangeCommand = 300f;
    public boolean CommandAir;
    public boolean CommandGround;
    public TextureRegion teamRegion = Core.atlas.find(name+"-team");

    protected Unit[] ArrayTarget;

    public LegacyCommandCenter(String name) {
        super(name);

        update = true;
        solid = true;
        configurable = true;
    }

    /** AutoTargets the nearest enemy unit/block while keeping track on a listed array, this could be saved on {@link #ArrayTarget} **/
    public void NearbyUnit(LegacyCommandCenterBuild b){
        var target = Units.closest(b.team, b.x, b.y, MaximumRangeCommand, e -> e.isValid() && e.checkTarget(CommandAir, CommandGround));
        if(target != null){
            this.ArrayTarget = new Unit[]{target};
        } else {
            ArrayTarget = null;
        }
    }


    public class LegacyCommandCenterBuild extends Building {
        protected String CommandSelect = "attack";
        @Override
        public void buildConfiguration(Table table) {
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
            Draw.rect(block.region, x, y);
            TextureRegion c = Core.atlas.find(block.name+"-"+CommandSelect);

            Draw.color(team.color);
            Draw.rect(teamRegion, x, y);
            Draw.rect(c, x, y);
            Draw.reset();
        }

        public void UpdateCommand(RallyAI.UnitState State){
            NearbyUnit(this);
            for (Unit u : ArrayTarget){
                if(u.controller() instanceof RallyAI ai){
                    ai.state = State;
                }
            }
        }
    }
}
