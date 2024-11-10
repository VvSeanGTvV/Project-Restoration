package classicMod.library.blocks.legacyBlocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.io.*;
import classicMod.library.ai.RallyAI;
import mindustry.ai.UnitCommand;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.ui.Styles;
import mindustry.world.Block;

import java.util.Objects;

import static classicMod.content.ClassicVars.*;
import static classicMod.content.ExtendedFx.commandSend;

public class LegacyCommandCenter extends Block {
    public TextureRegion topRegion = Core.atlas.find(name + "-top");

    protected Unit[] ArrayTarget;

    public LegacyCommandCenter(String name) {
        super(name);

        update = true;
        solid = true;
        configurable = true;
    }

    @Override
    public void setStats() {
        super.setStats();

        //stats.add(Stat.range, MaximumRangeCommand / tilesize, StatUnit.blocks);
    }


    public class LegacyCommandCenterBuild extends Building {
        public String CommandSelect = "attack";
        public Seq<Unit> targets = new Seq<>();
        public Seq<Unit> targetsModern = new Seq<>();
        public Seq<Building> CommandCenterArea = new Seq<>();
        public float blockID;

        @Override
        public void buildConfiguration(Table table) {
            if (blockID == 0f) blockID = Mathf.randomSeed(this.id) * 120;
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
            TextureRegion c = Core.atlas.find(name + "-" + CommandOrigin);

            if (c != null) {
                Draw.alpha(255 / 2f);
                Draw.color(Color.valueOf("5e5e5e"));
                Draw.rect(c, x, y - 0.4f);

                Draw.alpha(0f);
                Draw.color(team.color);
                Draw.rect(c, x, y);
            }


            Draw.reset();
        }

        public void UpdateCommand(RallyAI.UnitState State) {
            commandSend.at(this);
            PublicState = State;

            CommandCenterArea.clear();
            Units.nearbyBuildings(x, y, MaximumRangeCommand, b -> {
                if (b instanceof LegacyCommandCenterBuild) {
                    CommandCenterArea.add(b);
                }
            });

            targets.clear();
            targetsModern.clear();
            Units.nearby(team, x, y, MaximumRangeCommand, u -> {
                if (u.team == team)
                    if (u.controller() instanceof RallyAI) {
                        targets.add(u);
                    } else {
                        targetsModern.add(u);
                    }

            });

            for (var build : CommandCenterArea) {
                if (build instanceof LegacyCommandCenterBuild b) {
                    Log.info(b);
                    Log.info(CommandOrigin);
                }
            }

            for (var target : targets) {
                if (target.controller() instanceof RallyAI ai) {
                    ai.state = State;
                    ai.lastCommandCenterID = blockID;
                }
            }
        }

        @Override
        public void update() {
            super.update();

            //-- shesh about to go laggy with this one.
            targetsModern.clear();
            Units.nearby(team, x, y, MaximumRangeCommand, u -> {
                if (u.team == team)
                    if (!(u.controller() instanceof RallyAI)) {
                        targetsModern.add(u);
                    }
            });

            for (var targetM : targetsModern) {
                if (targetM.isCommandable() && targetM.team == team) {
                    var ai = targetM.command();
                    if (Objects.equals(CommandOrigin, "rally")) {

                        if (targetClosest(targetM) != null) {
                            var target = targetClosest(targetM);
                            if (target != null && targetM.hasWeapons()) {
                                ai.command = targetM.type.defaultCommand == null ? targetM.type.commands[0] : targetM.type.defaultCommand;
                            }
                        } else {
                            var building = Units.closestBuilding(targetM.team, targetM.x, targetM.y, MaximumRangeCommand, b -> (b instanceof LegacyCommandCenter.LegacyCommandCenterBuild) && b.isValid() && !(b.isNull()));
                            if (targetM.isFlying()) ai.circle(building, 65f + Mathf.randomSeed(targetM.id) * 100);
                            else {
                                ai.moveTo(building, 65f + Mathf.randomSeed(targetM.id) * 100, 25f, true, Vec2.ZERO, true);
                                ai.faceMovement();
                            }
                            ai.commandTarget(building);
                            ai.command(UnitCommand.moveCommand);
                        }
                    }
                    if (Objects.equals(CommandOrigin, "attack")) {
                        ai.command = targetM.type.defaultCommand == null ? targetM.type.commands[0] : targetM.type.defaultCommand;
                    }
                }
            }
        }

        protected Teamc targetClosest(Unit unit) {
            Teamc target = null;
            Teamc newTarget = Units.closestTarget(unit.team(), unit.x(), unit.y(), Math.max(unit.range(), unit.type().range), u -> (unit.type().targetAir && u.isFlying()) || (unit.type().targetGround && !u.isFlying()));
            if (newTarget != null) {
                target = newTarget;
            }
            return target;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.str(CommandOrigin);
            write.f(blockID);
            write.b((byte) PublicState.ordinal());
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            CommandOrigin = read.str();
            blockID = read.f();
            PublicState = RallyAI.UnitState.all[read.b()];
        }
    }
}
