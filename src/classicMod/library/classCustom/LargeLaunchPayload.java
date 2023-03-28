package classicMod.library.classCustom;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import classicMod.library.blocks.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.io.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.storage.CoreBlock.*;

import static mindustry.Vars.net;
import static mindustry.Vars.state;

public class LargeLaunchPayload implements Drawc, Timedc, Teamc, LaunchPayloadc, Entityc{
    protected transient boolean added;
    public transient int id = EntityGroup.nextId();
    public transient Interval in = new Interval();
    protected transient int index__all = -1;
    protected transient int index__draw = -1;
    public Seq<ItemStack> stacks = new Seq();
    public float lifetime;
    public float time;
    public Team team;
    public float x,y;

    public Payload payload;
    //transient Interval in = new Interval();

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public int classId() {
        return 15;
    }

    public int id() {
        return this.id;
    }

    public String toString() {
        return "LaunchPayload#" + this.id;
    }

    public Team team() {
        return this.team;
    }

    public void id(int id) {
        this.id = id;
    }

    public void in(Interval in) {
        this.in = in;
    }

    public void lifetime(float lifetime) {
        this.lifetime = lifetime;
    }

    public void setIndex__all(int index) {
        this.index__all = index;
    }

    public void setIndex__draw(int index) {
        this.index__draw = index;
    }

    public void stacks(Seq<ItemStack> stacks) { this.stacks = stacks; }


    public void team(Team team) { this.team = team; }

    public void time(float time) {
        this.time = time;
    }

    public void x(float x) {
        this.x = x;
    }

    public void y(float y) {
        this.y = y;
    }

    public void read(Reads read) {
        short REV = read.s();
        if (REV == 0) {
            this.lifetime = read.f();
            int stacks_LENGTH = read.i();
            this.stacks.clear();

            for(int INDEX = 0; INDEX < stacks_LENGTH; ++INDEX) {
                ItemStack stacks_ITEM = TypeIO.readItems(read);
                if (stacks_ITEM != null) {
                    this.stacks.add(stacks_ITEM);
                }
            }

            this.team = TypeIO.readTeam(read);
            this.time = read.f();
            this.x = read.f();
            this.y = read.f();
            this.afterRead();
        } else {
            throw new IllegalArgumentException("Unknown revision '" + REV + "' for entity type 'LaunchPayloadComp'");
        }
    }

    public void write(Writes write) {
        write.s(0);
        write.f(this.lifetime);
        write.i(this.stacks.size);

        for(int INDEX = 0; INDEX < this.stacks.size; ++INDEX) {
            TypeIO.writeItems(write, (ItemStack)this.stacks.get(INDEX));
        }

        TypeIO.writeTeam(write, this.team);
        write.f(this.time);
        write.f(this.x);
        write.f(this.y);
    }
    public Building buildOn() {
        return Vars.world.buildWorld(this.x, this.y);
    }

    public boolean cheating() {
        return this.team.rules().cheat;
    }

    public boolean inFogTo(Team viewer) {
        return this.team != viewer && !Vars.fogControl.isVisible(viewer, this.x, this.y);
    }

    @Override
    public <T extends Entityc> T self() {
        return (T) this;
    }

    @Override
    public <T> T as() {
        return (T) this;
    }

    public boolean isAdded() {
        return this.added;
    }

    public boolean isLocal() {
        boolean var10000;
        if (this != Vars.player) {
            label26: {
                if (this instanceof Unitc) {
                    Unitc u = (Unitc)this;
                    if (u.controller() == Vars.player) {
                        break label26;
                    }
                }

                var10000 = false;
                return var10000;
            }
        }

        var10000 = true;
        return var10000;
    }

    public boolean isNull() {
        return false;
    }

    public boolean isRemote() {
        boolean var10000;
        if (this instanceof Unitc) {
            Unitc u = (Unitc)this;
            if (u.isPlayer() && !this.isLocal()) {
                var10000 = true;
                return var10000;
            }
        }

        var10000 = false;
        return var10000;
    }

    public boolean onSolid() {
        Tile tile = this.tileOn();
        return tile == null || tile.solid();
    }

    public boolean serialize() {
        return true;
    }

    public float clipSize() {
        return Float.MAX_VALUE;
    }

    public float fin() {
        return this.time / this.lifetime;
    }

    @Override
    public float lifetime() {
        return 0;
    }

    @Override
    public float time() {
        return 0;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public int tileX() {
        return World.toTile(this.x);
    }

    public int tileY() {
        return World.toTile(this.y);
    }

    public Block blockOn() {
        Tile tile = this.tileOn();
        return tile == null ? Blocks.air : tile.block();
    }

    public Tile tileOn() {
        return Vars.world.tileWorld(this.x, this.y);
    }

    @Override
    public void set(Position pos) {
        this.set(pos.getX(), pos.getY());
    }

    @Override
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void trns(Position pos) {
        this.trns(pos.getX(), pos.getY());
    }

    public void trns(float x, float y) {
        this.set(this.x + x, this.y + y);
    }

    public Floor floorOn() {
        Tile tile = this.tileOn();
        return tile != null && tile.block() == Blocks.air ? tile.floor() : (Floor)Blocks.air;
    }

    public CoreBlock.CoreBuild closestCore() {
        return Vars.state.teams.closestCore(this.x, this.y, this.team);
    }

    public CoreBlock.CoreBuild closestEnemyCore() {
        return Vars.state.teams.closestEnemyCore(this.x, this.y, this.team);
    }

    public CoreBlock.CoreBuild core() {
        return this.team.core();
    }

    public void add() {
        if (!this.added) {
            this.index__all = Groups.all.addIndex(this);
            this.index__draw = Groups.draw.addIndex(this);
            this.added = true;
        }
    }

    public void afterRead() {
    }

    @Override
    public void draw(){
        float alpha = fout(Interp.pow5Out);
        float scale = (1f - alpha) * 1.3f + 1f;
        float cx = cx(), cy = cy();
        float rotation = fin() * (130f + Mathf.randomSeedRange(id(), 50f));

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

        TextureRegion region = blockOn() instanceof PayloadLaunchPad p ? p.podRegion : Core.atlas.find("launchpod");
        float rw = region.width * Draw.scl * scale, rh = region.height * Draw.scl * scale;

        Draw.alpha(alpha);
        Draw.rect(region, cx, cy, rw, rh, rotation);

        Tmp.v1.trns(225f, fin(Interp.pow3In) * 250f);

        Draw.z(Layer.flyingUnit + 1);
        Draw.color(0, 0, 0, 0.22f * alpha);
        Draw.rect(region, cx + Tmp.v1.x, cy + Tmp.v1.y, rw, rh, rotation);

        Draw.reset();
    }

    @Override
    public Seq<ItemStack> stacks() {
        return null;
    }

    @Override
    public Interval in() {
        return null;
    }

    public float cx(){
        return x + fin(Interp.pow2In) * (12f + Mathf.randomSeedRange(id() + 3, 4f));
    }

    public float cy(){
        return y + fin(Interp.pow5In) * (100f + Mathf.randomSeedRange(id() + 2, 30f));
    }

    @Override
    public void update(){
        float r = 3f;
        if(in.get(4f - fin()*2f)){
            Fx.rocketSmoke.at(cx() + Mathf.range(r), cy() + Mathf.range(r), fin());
        }
    }

    public static LargeLaunchPayload create() { return new LargeLaunchPayload(); }

    @Override
    public void remove(){
        if(!state.isCampaign()) return;

        Sector destsec = state.rules.sector.info.getRealDestination();

        //actually launch the items upon removal
        if(team() == state.rules.defaultTeam){
            if(destsec != null && (destsec != state.rules.sector || net.client())){
                    /*
                    ItemSeq dest = new ItemSeq();
                    for(ItemStack stack : stacks){
                        dest.add(stack);
                        //update export
                        state.rules.sector.info.handleItemExport(stack);
                        Events.fire(new LaunchItemEvent(stack));
                    }
                    if(!net.client()){
                        destsec.addItems(dest);
                    }*/
            }
        }
    }
}
