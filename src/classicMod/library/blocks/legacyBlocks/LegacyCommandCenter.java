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
        public Seq<Unit> targets = new Seq<>();
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
        public void UpdateCommand(RallyAI.UnitState State){
            commandSend.at(this);

            targets.clear();
            Units.nearby(team, x, y, MaximumRangeCommand, u -> {
                if(u.controller() instanceof RallyAI){
                    commandSend.at(u);
                    targets.add(u);
                }
            });
        }
    }
}
