package classicMod.library.blocks.legacyBlocks;

import arc.*;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.*;
import arc.struct.EnumSet;
import arc.util.io.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

//Created by: VvSeanGtvV#2295 at Discord!
//Similar to modern unit factory but in an older style mechanics, where it doesn't need payload!

public class LegacyUnitFactory extends Block {
    public UnitType unitType;
    public float produceTime = 60f;
    public int originCap = 0;
    //public float launchVelocity = 0f; //never used who knows why
    public TextureRegion topRegion;
    public int maxSpawn = 8; //Default by 4
    public int originMax = maxSpawn;
    public int[] capacities = {};
    public ItemStack[] requirement; //Requirements for the unit
    protected boolean varRuleSet = false; //If it is already setted and multiplied by the rules unitcap.

    public LegacyUnitFactory(String name){
        super(name);
        update = true;
        hasPower = true;
        hasItems = true;
        solid = false;
        flags = EnumSet.of(BlockFlag.factory);
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    @Override
    public void setStats(){
        stats.remove(Stat.itemCapacity);
        stats.add(Stat.productionTime, produceTime/60f, StatUnit.seconds);
        stats.add(Stat.maxUnits, maxSpawn, StatUnit.none);

        super.setStats();
    }

    public void updateStats(){
        if(stats.intialized) {
            stats.remove(Stat.productionTime);
            stats.remove(Stat.maxUnits);

            stats.add(Stat.productionTime, produceTime / 60f, StatUnit.seconds);
            stats.add(Stat.maxUnits, maxSpawn, StatUnit.none);
            
        }

        //super.setStats();
    }


    @Override
    public void setBars(){
        super.setBars();
        addBar("progress", (LegacyUnitFactory.LegacyUnitFactoryBuild e) -> new Bar("bar.progress", Pal.ammo, e::fraction));
        addBar("units", (LegacyUnitFactory.LegacyUnitFactoryBuild e) -> new Bar(Core.bundle.format("bar.unitcap", Fonts.getUnicodeStr(unitType.name), e.team.data().countType(unitType), originMax), Pal.command, e::fractionUnitCap));
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{Core.atlas.find(name), Core.atlas.find(name + "-top")};
    }

    @Override
    public void init(){
        if(maxSpawn < 1 || maxSpawn == 0){ maxSpawn = 4; }
        topRegion = Core.atlas.find(name + "-top");
        if(requirement != null){
            capacities = new int[Vars.content.items().size];
            for(ItemStack stack : requirement){
                capacities[stack.item.id] = Math.max(capacities[stack.item.id], stack.amount * 2);
                itemCapacity = Math.max(itemCapacity, stack.amount * 2);
        
                consumeBuilder.each(c -> c.multiplier = b -> state.rules.unitCost(b.team));
            }
            consumeItems(requirement);
        }

        super.init();
    }

    public class LegacyUnitFactoryBuild extends Building {
        public float progress, time, speedScl;
        public int FactoryunitCap, temp1;
        public int maximumINT = 2147483647;
        public float buildTime = produceTime;
        //public @Nullable BlockUnitc unit;

        public float fraction(){ return progress / buildTime; }
        public float fractionUnitCap(){ return (float)team.data().countType(unitType) / (FactoryunitCap); }


        @Override
        public void updateTile(){
            if(Units.getCap(team) < maximumINT) {
                temp1 = Units.getCap(team) * maxSpawn;
                temp1 = temp1 * maxSpawn / 2 - (Units.getCap(team) + maxSpawn);
                if (originCap > 8) {
                    FactoryunitCap = temp1 / (maxSpawn * 4);
                } else {
                    FactoryunitCap = maxSpawn;
                }
            } else {
               FactoryunitCap = 2147483647;
            }
            originMax = FactoryunitCap;
            updateStats();
            //varCapSet = true;
            originCap = Units.getCap(team);

            if(state.rules.unitCap > 0 && !varRuleSet){
                FactoryunitCap = FactoryunitCap * state.rules.unitCap;
                originMax = FactoryunitCap;
                updateStats();
                varRuleSet = true;
            }

            if(efficiency > 0 && ((float)team.data().countType(unitType) < FactoryunitCap)){
                time += edelta() * speedScl * Vars.state.rules.unitBuildSpeed(team);
                progress += edelta() * Vars.state.rules.unitBuildSpeed(team);
                speedScl = Mathf.lerpDelta(speedScl, 1f, 0.05f);
                //unit.ammo(unit.type().ammoCapacity * fraction());
                //ambientSound.at(unit);
            }else{
                speedScl = Mathf.lerpDelta(speedScl, 0f, 0.05f);
            }

            if(progress >= buildTime) {
                progress %= 1f;

                consume();
                LegacyUnitFactory factory = (LegacyUnitFactory)tile.block();
                //Call.unitBlockSpawn(this.tile);
                Unit unit = factory.unitType.create(team);
                unit.set(this);
                unit.rotation(90f);
                unit.add();
                Fx.producesmoke.at(this);
                Effect.shake(2f, 3f, this);
                Events.fire(new UnitSpawnEvent(unit));
            }
        }

        @Override
        public boolean shouldConsume(){
            if((float)team.data().countType(unitType) > FactoryunitCap) return false;
            return enabled && ((float)team.data().countType(unitType) < FactoryunitCap);
        }

        @Override
        public void draw(){
            super.draw();
            TextureRegion region = Core.atlas.find(name);
            Draw.rect(region, tile.drawx(), tile.drawy());
            TextureRegion unitRegion  = unitType.fullIcon;

            Draw.draw(Layer.blockOver, () -> {
                Shaders.build.region = unitRegion;
                Shaders.build.progress = fraction();
                Shaders.build.color.set(Pal.accent);
                Shaders.build.color.a = speedScl;
                Shaders.build.time = -time / 20f;

                Draw.shader(Shaders.build, true);
                Draw.rect(unitRegion, tile.drawx(), tile.drawy());
                Draw.shader();
            });

            Draw.color(Pal.accent);
            Draw.alpha(speedScl);

            Lines.lineAngleCenter(tile.drawx() + Mathf.sin(time, 20f, Vars.tilesize / 2f * size - 2f), tile.drawy(), 90, size * Vars.tilesize - 4f);

            Draw.reset();

            Draw.rect(topRegion, tile.drawx(), tile.drawy());
        }

        @Override
        public void write(Writes stream){
            super.write(stream);
            stream.f(progress);
            stream.i(FactoryunitCap);
        }

        @Override
        public void read(Reads stream, byte revision){
            super.read(stream, revision);
            progress = stream.f();
            FactoryunitCap = stream.i();
        }
    }
}
