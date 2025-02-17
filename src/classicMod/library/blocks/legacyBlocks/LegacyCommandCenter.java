package classicMod.library.blocks.legacyBlocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.io.*;
import classicMod.library.ai.*;
import mindustry.ai.UnitCommand;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.ui.Styles;
import mindustry.world.Block;

import java.util.Objects;

import static classicMod.content.RVars.*;
import static classicMod.content.RFx.commandSend;

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
        public String CommandSelect = null;
        public Seq<Unit> targets = new Seq<>();
        public Seq<Unit> targetsModern = new Seq<>();
        public Seq<LegacyCommandCenterBuild> CommandCenterArea = new Seq<>();
        public float blockID;
        protected int select = 0;

        @Override
        public void buildConfiguration(Table table) {
            if (blockID == 0f) blockID = Mathf.randomSeed(this.id) * 120;
            final ButtonGroup<Button> group = new ButtonGroup<>();
            group.setMinCheckCount(0);
            ImageButton attack = table.button(Icon.commandAttack, Styles.cleari, () -> {
                configure(0);
            }).get();

            ImageButton rally = table.button(Icon.commandRally, Styles.cleari, () -> {
                configure(1);
            }).get();

            //attack.changed(() -> configure(1));
            //rally.changed(() -> configure(2));

            /*Table buttons = new Table();
            buttons.button(Icon.commandAttack, Styles.cleari, () -> {
                UpdateCommand(RallyAI.UnitState.attack, "attack");
            });
            buttons.button(Icon.commandRally, Styles.cleari, () -> {
                UpdateCommand(RallyAI.UnitState.rally, "rally");
            });
            table.add(buttons);*/
        }

        @Override
        public void created() {
            Seq<Building> buildingSeq = new Seq<>();
            LegacyCommandCenterBuild closestBuild = null;
            Units.nearbyBuildings(x, y, MaximumRangeCommand, b -> {
                if (b instanceof LegacyCommandCenterBuild) {
                    buildingSeq.add(b);
                }
            });
            Building building = null;
            if (buildingSeq.size >= 1) building = buildingSeq.get(0);
            if (building != null && building instanceof LegacyCommandCenterBuild f) closestBuild = f;
            if (closestBuild != null) CommandSelect = closestBuild.CommandSelect; else {
                select = 0;
                CommandSelect = "attack";
                configure(0);
            }

            super.created();
        }

        @Override
        public Integer config() {
            return select;
        }

        @Override
        public void configured(Unit builder, Object value) {
            if (value instanceof Integer val) select = val;
            if (select > -1) UpdateCommand(RallyAI.UnitState.all[select], RallyAI.UnitState.allString[select]);
        }

        @Override
        public void draw() {
            super.draw();
            TextureRegion c = Core.atlas.find(name + "-" + CommandSelect);

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

        public void UpdateCommand(RallyAI.UnitState State, String command) {
            CommandSelect = command;
            commandSend.at(this);
            //PublicState = State;

            CommandCenterArea.clear();
            Units.nearbyBuildings(x, y, MaximumRangeCommand, b -> {
                if (b instanceof LegacyCommandCenterBuild f && b.team == this.team) {
                    CommandCenterArea.add(f);
                }
            });

            targets.clear();
            targetsModern.clear();
            Units.nearby(team, x, y, MaximumRangeCommand, u -> {
                if (u.team == this.team)
                    if (u.controller() instanceof RallyAI) {
                        targets.add(u);
                    } else {
                        targetsModern.add(u);
                    }

            });

            for (var build : CommandCenterArea) {
                build.CommandSelect = this.CommandSelect;
            }

            for (var target : targets) {
                if (target.controller() instanceof RallyAI ai) {
                    ai.updateState(State);
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
                    if (!(u.controller() instanceof RallyAI)) {
                        targetsModern.add(u);
                    }
            });

            for (var targetM : targetsModern) {
                if (targetM.isCommandable()) {
                    var ai = targetM.command();
                    if (Objects.equals(CommandSelect, "rally")) {
                        if (targetClosest(targetM) != null) {
                            var target = targetClosest(targetM);
                            if (target != null && targetM.hasWeapons()) {
                                ai.command = targetM.type.defaultCommand == null ? targetM.type.commands[0] : targetM.type.defaultCommand;
                            }
                        } else {
                            var building = Units.closestBuilding(targetM.team, targetM.x, targetM.y, MaximumRangeCommand, b -> (b instanceof LegacyCommandCenter.LegacyCommandCenterBuild) && b.isValid() && !(b.isNull()));
                            if (targetM.isFlying()) ai.circle(building, 65f + Mathf.randomSeed(targetM.id) * 100);
                            else {
                                ai.pathfind(PathfinderExtended.fieldCommandCenter);
                                //ai.moveTo(building, 65f + Mathf.randomSeed(targetM.id) * 100, 25f, true, Vec2.ZERO, true);
                                ai.faceMovement();
                            }
                            ai.commandTarget(building);
                            ai.command(UnitCommand.moveCommand);
                        }
                    }
                    if (Objects.equals(CommandSelect, "attack")) {
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
        public byte version() {
            return 2;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.str(CommandSelect);
            write.f(blockID);

            write.i(select);
            //write.b((byte) PublicState.ordinal());
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            if (revision == 1 || revision == 2) { // for Build 12
                CommandSelect = read.str();
                blockID = read.f();
                if (revision == 2) select = read.i();
            }
            if (revision == 0) { // for Build 9 - Build 11
                CommandSelect = read.str();
                blockID = read.f();
                int i = read.b();
                UpdateCommand(RallyAI.UnitState.all[i], RallyAI.UnitState.allString[i]);
            }
            //PublicState = RallyAI.UnitState.all[read.b()];
        }
    }
}
