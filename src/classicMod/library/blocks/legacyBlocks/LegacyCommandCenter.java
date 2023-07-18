package classicMod.library.blocks.legacyBlocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.Button;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import classicMod.library.ai.RallyAI;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.ui.Styles;
import mindustry.world.Block;

import java.lang.reflect.Array;

import static classicMod.content.ExtendedFx.commandSend;

public class LegacyCommandCenter extends Block {
    public float MaximumRangeCommand = 300f;
    public boolean CommandAir;
    public boolean CommandGround;
    public TextureRegion topRegion = Core.atlas.find(name+"-top");

    protected Unit[] ArrayTarget;

    public LegacyCommandCenter(String name) {
        super(name);

        update = true;
        solid = true;
        configurable = true;
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
            super.draw();
            TextureRegion c = Core.atlas.find(name+"-"+CommandSelect);

            Draw.color(team.color);
            //Draw.rect(topRegion, x, y);
            Draw.rect(c, x, y);
            Draw.reset();
        }

        /** AutoTargets the nearest enemy unit/block while keeping track on a listed array, this could be saved on {@link #ArrayTarget} **/
        public void NearbyUnit(LegacyCommandCenterBuild b){
            var target = Units.closest(b.team, b.x, b.y, MaximumRangeCommand, e -> e.isValid() && e.checkTarget(CommandAir, CommandGround));
            Log.info(target);
            if(target != null) {
                commandSend.at(target);
                ArrayTarget = new Unit[]{target};
            }
        }

        public void UpdateCommand(RallyAI.UnitState State){
            NearbyUnit(this);
            commandSend.at(this);
            if(ArrayTarget != null) {
                for (Unit u : ArrayTarget) {
                    if (u.controller() instanceof RallyAI ai) {
                        ai.state = State;
                    }
                }
            }
        }
    }
}
