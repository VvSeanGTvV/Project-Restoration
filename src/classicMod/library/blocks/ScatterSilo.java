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
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.consumers.ConsumeItemFilter;
import mindustry.world.meta.*;

import static mindustry.Vars.content;

public class ScatterSilo extends Block {

    public Effect siloLaunch = ExtendedFx.siloLaunchEffect;

    public ObjectMap<ItemStack, BulletType> ammoTypes = new OrderedMap<>();
    public int maxAmmo = 30;

    public ScatterSilo(String name) {
        super(name);
        hasItems = false;
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
    public void setBars(){
        super.setBars();
        
        addBar("ammo", (ScatterSiloBuild entity) ->
                new Bar(
                        "stat.ammo",
                        Pal.ammo,
                        () -> (float)entity.ammoTotal / maxAmmo
                )
        );
    }

    @Override
    public void init(){
        Seq<Item> ammoItems = new Seq<>();
        for (var ammo : ammoTypes){
            ammoItems.add(ammo.key.item); //Adds every item using ammo.
        }

        consume(new ConsumeItemFilter(i -> ammoItems.contains(i)){
                @Override
                public void build(Building build, Table table){
                    MultiReqImage image = new MultiReqImage();
                    content.items().each(i -> filter.get(i) && i.unlockedNow(),
                            item -> image.add(new ReqImage(new Image(item.uiIcon),
                                    () -> build instanceof ScatterSiloBuild it && !it.ammoStacks.isEmpty() && (it.ammoStacks.peek()) == item)));
                    table.add(image).size(8 * 4);
                }
                @Override
                public float efficiency(Building build){
                    //valid when there's any ammo in the turret
                    return build instanceof ScatterSiloBuild it && !it.ammoStacks.isEmpty() ? 1f : 0f;
                }
                @Override
                public void display(Stats stats){
                    //don't display
                }
            });

        super.init();
    }

    public class ScatterSiloBuild extends Building {

        public Seq<Item> ammoStacks = new Seq<>();
        public BulletType bulletType = null;
        public float ammoTotal, ammoConsume;

        @Override
        public void buildConfiguration(Table table) {
            table.button(Icon.upOpen, Styles.clearTogglei, () -> {
                configure(0);
            }).size(50).disabled(efficiency < 1f || ammoTotal <= 0f);
        }

        @Override
        public void handleItem(Building source, Item item) {
            BulletType type = null;
            for (var ammo : ammoTypes){
                if (ammo.key.item == item){
                    type = ammo.value;
                    ammoConsume = ammo.key.amount;
                    break;
                }
            }
            if(type == null) return;
            bulletType = type;
            ammoTotal += type.ammoMultiplier;

            //find ammo entry by type
            for(int i = 0; i < ammoStacks.size; i++){
                Item entry = ammoStacks.get(i);

                //if found, put it to the right
                if(entry == item){
                    ammoStacks.swap(i, ammoStacks.size - 1);
                    return;
                }
            }

            ammoStacks.add(item);
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            boolean contains = false;
            float ammoMultiplier = 0;
            for (var ammo : ammoTypes){
                if (ammo.key.item == item){
                    contains = true;
                    ammoMultiplier = ammo.value.ammoMultiplier;
                    break;
                }
            }
            return contains && ammoTotal + ammoMultiplier <= maxAmmo;
        }

        @Override
        public int removeStack(Item item, int amount){
            return 0;
        }

        @Override
        public void configured(Unit builder, Object value) {
            if (efficiency >= 1f && bulletType != null && ammoTotal > 0f){
                siloLaunch.at(this);
                //BulletType type = ammoTypes.get(item);

                for (int i = 0; i < ammoConsume; i++){
                    bulletType.create(this, team, x, y, Mathf.random(360), Mathf.random(0.5f, 1f), Mathf.random(0.2f, 1f));
                }
                ammoTotal -= ammoConsume;
                //consume();
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
