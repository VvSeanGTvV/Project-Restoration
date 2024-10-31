package classicMod.library.blocks;

import arc.math.Mathf;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.struct.*;
import arc.util.io.*;
import classicMod.content.*;
import mindustry.Vars;
import mindustry.content.Bullets;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.consumers.ConsumeItemFilter;
import mindustry.world.meta.*;

import static mindustry.Vars.content;

public class ScatterSilo extends Block {

    public Effect siloLaunch = ExtendedFx.siloLaunchEffect;
    public BulletType bulletType = ClassicBullets.flakExplosive;

    public ObjectMap<ItemStack, BulletType> ammoTypes = new OrderedMap<>();

    public ScatterSilo(String name) {
        super(name);
    }

    public void ammo(Object... objects){
        ammoTypes = OrderedMap.of(objects);
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.remove(Stat.itemCapacity);
        stats.add(Stat.ammo, ExtendedStat.ammo(ammoTypes));
    }

    @Override
    public void init(){

        

        super.init();
    }

    public class ScatterSiloBuild extends Building {

        @Override
        public void buildConfiguration(Table table) {
            table.button(Icon.upOpen, Styles.clearTogglei, () -> {
                configure(0);
            }).size(50).disabled(this.isValid() && efficiency < 1f);
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            boolean yes = false;
            for (var ammo : ammoTypes){
                if (ammo.key.item == item) {
                    yes = true;
                    break;
                }
            }
            return yes;
        }

        /*
        configured(tile, value){
        //make sure this silo has the items it needs to fire
        if(tile.entity.cons.valid()){
            //make this effect occur at the tile location
            Effects.effect(siloLaunchEffect, tile)

            //create 10 bullets at this tile's location with random rotation and velocity/lifetime
            for(var i = 0; i < 15; i++){
                Calls.createBullet(Bullets.flakExplosive, tile.getTeam(), tile.drawx(), tile.drawy(), Mathf.random(360), Mathf.random(0.5, 1.0), Mathf.random(0.2, 1.0))
            }
            //triggering consumption makes it use up the items it requires
            tile.entity.cons.trigger()
        }
         */

        @Override
        public void configured(Unit builder, Object value) {
            if (efficiency >= 1f){
                siloLaunch.at(this);
                //BulletType type = ammoTypes.get(item);

                for (int i = 0; i < 15; i++){
                    bulletType.create(this, team, x, y, Mathf.random(360), Mathf.random(0.5f, 1f), Mathf.random(0.2f, 1f));
                }

                consume();
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
        }

        @Override
        public void write(Writes write){
            super.write(write);
        }
    }
}
