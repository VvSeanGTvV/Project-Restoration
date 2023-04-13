package classicMod.library.blocks.customBlocks;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import classicMod.content.*;
import mindustry.content.*;
import mindustry.entities.units.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;

import static arc.Core.*;
import static mindustry.Vars.content;

public class WarpGate extends Block {

    protected static final Color[] selection = new Color[]{Color.royal, Color.orange, Color.scarlet, Color.forest, Color.purple, Color.gold, Color.pink, Color.black};
    protected static final ObjectSet<WarpGate.WarpGateBuild>[][] teleporters;
    public float warmupTime = 60f;
    /** time between Teleports **/
    public float teleportMax = 1000f;
    public float powerUse = 0.3f;
    public float teleportLiquidUse = 0.3f;
    public float liquidUse = 0.1f;

    protected float powerMulti;

    public Liquid inputLiquid;
    //protected Effect activateEffect = BlockFx.teleportActivate;
    //protected Effect teleportEffect = BlockFx.teleport;
    //protected Effect teleportOutEffect = BlockFx.teleportOut;
    protected TextureRegion blankRegion;

    static{
        teleporters = new ObjectSet[Team.baseTeams.length][selection.length];
        for(int i = 0; i < Team.baseTeams.length; i++){
            if(teleporters[i] == null) teleporters[i] = new ObjectSet[selection.length];
            for(int j = 0; j < selection.length; j++) teleporters[i][j] = new ObjectSet<>();
        }
    }

    public WarpGate(String name){
        super(name);
        update = true;
        solid = true;
        configurable = true;
        saveConfig = true;
        unloadable = false;
        hasItems = true;
        Events.on(WorldLoadEvent.class, e -> {
            for(int i = 0; i < teleporters.length; i++){
                for(int j = 0; j < teleporters[i].length; j++) teleporters[i][j].clear();
            }
        });
        config(Integer.class, (WarpGate.WarpGateBuild build, Integer value) -> {
            if(build.toggle != -1) teleporters[build.team.id][build.toggle].remove(build);
            if(value != -1) teleporters[build.team.id][value].add(build);
            build.toggle = value;
        });
        configClear((WarpGate.WarpGateBuild build) -> {
            if(build.toggle != -1) teleporters[build.team.id][build.toggle].remove(build);
            build.toggle = -1;
        });
    }

    @Override
    public boolean outputsItems(){
        return true;
    }

    @Override
    public void init(){
        consumePowerCond(powerUse + powerMulti, WarpGate.WarpGateBuild::isConsuming);
        super.init();
    }

    @Override
    public void load(){
        super.load();
        blankRegion = atlas.find(name + "-mid");
        if(inputLiquid == null)inputLiquid = Liquids.cryofluid;
    }

    @Override
    public void drawPlanConfig(BuildPlan req, Eachable<BuildPlan> list){
        drawPlanConfigCenter(req, req.config, "nothing");
    }

    @Override
    public void drawPlanConfigCenter(BuildPlan req, Object content, String region){
        if(!(content instanceof Integer temp) || temp < 0 || temp >= selection.length) return;
        Draw.color(selection[temp]);
        Draw.rect(blankRegion, req.drawx(), req.drawy());
    }

    public class WarpGateBuild extends Building {
        protected int toggle = -1, entry;
        protected float duration;
        protected WarpGateState currentState = WarpGateState.idle;
        protected ItemStack itemStack;
        protected @Nullable ItemStack[] itemStacks;
        protected WarpGate.WarpGateBuild target;
        protected Team previousTeam;
        protected boolean firstTime;

        protected void onDuration(){
            if(duration < 0f) duration = teleportMax;
            else duration -= Time.delta;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return super.acceptLiquid(source, liquid) && liquid == inputLiquid;
        }

        protected boolean isConsuming(){
            return warmupTime > 0f;
        }

        protected boolean isTeamChanged(){
            return previousTeam != team;
        }

        public void consumeLiquid(Liquid liquid, float amount){
            this.liquids.remove(liquid, amount);
        }

        @Override
        public void draw(){
            super.draw();
            if(toggle != -1){
                Draw.color(selection[toggle]);
                Draw.rect(blankRegion, x, y);
            }
            Draw.color(Color.white);
            Draw.alpha(0.45f + Mathf.absin(7f, 0.26f));
            Draw.reset();
        }

        @Override
        public void updateTile() {
            if (efficiency > 0) {
                onDuration();
                if (firstTime) {
                    if (toggle != -1) ExtendedFx.teleportActivate.at(this.x, this.y, selection[toggle]);
                    firstTime = false;
                }
                if (duration <= 1f) {
                    powerMulti = Math.min(this.block.consPower.capacity, powerUse * Time.delta);
                    //consumeLiquid(inputLiquid, teleportLiquidUse);
                    if (toggle != -1) {
                        ExtendedFx.teleportOut.at(this.x, this.y, selection[toggle]);
                        WarpGate.WarpGateBuild other = findLink(toggle, WarpGateState.receiver);
                        if (other != null && currentState == WarpGateState.transporter) {
                            handleTransport(other);
                            ExtendedFx.teleportOut.at(other.x, other.y, selection[toggle]);
                        }
                    }
                    if (isTeamChanged() && toggle != -1) {
                        teleporters[team.id][toggle].add(this);
                        ExtendedFx.teleport.at(this.x, this.y, selection[toggle]);
                        Time.run(warmupTime, () -> {
                            //remove waiting shooters, it's done firing
                            teleporters[previousTeam.id][toggle].remove(this);
                            previousTeam = team;
                        });
                    }
                    duration = teleportMax;
                }
            } else {
                firstTime = true;
            }
            if (currentState == WarpGateState.idle) {
                if (this.items.total() > 0) {
                    currentState = WarpGateState.transporter;
                } else {
                    currentState = WarpGateState.receiver;
                }
            }
            if (toggle != -1) {
                if (currentState == WarpGateState.transporter) {
                    if (findLink(toggle, WarpGateState.receiver) == null) currentState = WarpGateState.idle;
                }
                if (currentState == WarpGateState.receiver && items.total() <= 0) {
                    if (findLink(toggle, WarpGateState.transporter) == null) currentState = WarpGateState.idle;
                }
            }
            if(currentState==WarpGateState.receiver && items.any()) dump();
        }

        public void catastrophicFailure(){
            this.damage(this.health + 1);
            //TODO fail gloriously lol
        }

        @Override
        public void buildConfiguration(Table table){
            final ButtonGroup<Button> group = new ButtonGroup<>();
            group.setMinCheckCount(0);
            for(int i = 0; i < selection.length; i++){
                int j = i;
                ImageButton button = table.button(Tex.whiteui, Styles.clearTogglei, 24f, () -> {}).size(34f).group(group).get();
                button.changed(() -> configure(button.isChecked() ? j : -1));
                button.getStyle().imageUpColor = selection[j];
                button.update(() -> button.setChecked(toggle == j));
                if(i % 4 == 3) table.row();
            }
        }

        public WarpGate.WarpGateBuild findLink(int value, WarpGateState state){
            ObjectSet<WarpGate.WarpGateBuild> teles = teleporters[team.id][value];
            Seq<WarpGate.WarpGateBuild> entries = teles.toSeq();
            if(entry >= entries.size) entry = 0;
            if(entry == entries.size - 1){
                WarpGate.WarpGateBuild other = teles.get(entries.get(entry));
                if(other == this) entry = 0;
            }
            for(int i = entry, len = entries.size; i < len; i++){
                WarpGate.WarpGateBuild other = teles.get(entries.get(i));
                if(state != null) {
                    if (other != this && other.currentState == state) {
                        entry = i + 1;
                        return other;
                    }
                }else{
                    if (other != this) {
                        entry = i + 1;
                        return other;
                    }
                }
            }
            return null;
        }

        public WarpGate.WarpGateBuild findLink(int value){
            return findLink(value, null);
        }

        public void handleTransport(WarpGate.WarpGateBuild other) {
            if (other == null) other = findLink(toggle, WarpGateState.receiver);
            for (int i = 0; i < content.items().size; i++) {
                int totalIncap;
                totalIncap = this.items.get(content.items().get(i));
                if (totalIncap > 0) {
                    itemStack = new ItemStack(content.items().get(i), totalIncap);
                    itemStacks = new ItemStack[]{itemStack};
                }
            }
            if (itemStacks != null) {
                for (ItemStack itemTransport : itemStacks) {
                    if (other != null) other.items.add(itemTransport.item, itemTransport.amount);
                    this.items.remove(itemTransport.item, itemTransport.amount);
                }
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            if(toggle == -1) return false;
            target = findLink(toggle);
            if(target == null) return false;
            return source != this && canConsume() && Mathf.zero(1 - efficiency()) && target.items.total() < target.getMaximumAccepted(item) && this.items.total() < this.getMaximumAccepted(item);
        }

        /*@Override
        public void handleItem(Building source, Item item){
            if(duration<=1f) {
                target.items.add(item, this.items.total());
                duration = teleportMax;
                if(toggle != -1) {

                }
            }
        }*/

        @Override
        public void created(){
            if(toggle != -1) teleporters[team.id][toggle].add(this);
            previousTeam = team;
        }

        @Override
        public void onRemoved(){
            if(toggle != -1){
                if(isTeamChanged()) teleporters[previousTeam.id][toggle].remove(this);
                else teleporters[team.id][toggle].remove(this);
            }
            //unity.Unity.print(teleporters[team.id]);
        }

        @Override
        public Integer config(){
            return toggle;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.b(toggle);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            toggle = read.b();
        }
    }

    public enum WarpGateState{
        idle, //Does nothing
        receiver, //receives from transporter
        transporter; //transport to receiver

        public static final WarpGateState[] all = values();
    }
}
