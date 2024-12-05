package classicMod.library.blocks;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.g2d.Draw;
import arc.math.*;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.struct.*;
import arc.util.Log;
import arc.util.io.*;
import classicMod.content.*;
import mindustry.Vars;
import mindustry.content.Bullets;
import mindustry.entities.*;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.consumers.ConsumeItemFilter;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class ScatterSilo extends Block {

    public Effect siloLaunch = ExtendedFx.siloLaunchEffect;
    public Sound shootSound = Sounds.shoot;
    public Effect shootEffect;

    public float range = 60f;

    public ObjectMap<ItemStack, BulletType> ammoTypes = new OrderedMap<>();
    /** Maximum ammo units stored. */
    public int maxAmmo = 30;

    public float warmupSpeed = 0.019f;

    /** Range for pitch of shoot sound. */
    public float soundPitchMin = 0.9f, soundPitchMax = 1.1f;

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

        stats.add(Stat.range, range / tilesize, StatUnit.blocks);
        stats.add(Stat.ammo, ExtendedStat.ammo(ammoTypes));
    }

    @Override
    public void setBars(){
        super.setBars();

        removeBar("items");
        addBar("ammo", (ScatterSiloBuild entity) ->
                new Bar(
                        "stat.ammo",
                        Pal.ammo,
                        () -> (float)entity.ammoTotal / maxAmmo
                )
        );
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, player.team().color);
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
                                    () -> build instanceof ScatterSiloBuild it && !it.ammoStacks.isEmpty() && (it.ammoStacks.peek().item) == item)));
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

        public Seq<ItemEntry> ammoStacks = new Seq<>();
        public BulletType bulletType = null;
        boolean shoot = false, manualActivation = false;
        public float ammoTotal, ammoConsume, warmup;

        @Override
        public void buildConfiguration(Table table) {
            table.button(Icon.upOpen, Styles.clearTogglei, () -> {
                configure(0);
            }).size(50).disabled(efficiency < 1f || ammoTotal <= 0f || manualActivation);
        }

        @Override
        public void updateTile() {
            if (ammoTotal <= 0f) {
                if (ammoTotal < 0f) ammoTotal = 0f;
                ammoStacks.clear();
            }

            if (shoot && efficiency >= 1f && bulletType != null && ammoTotal > 0f) warmup = Mathf.approachDelta(warmup, 1f, warmupSpeed); else warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            if (warmup >= 1f || manualActivation){
                siloLaunch.at(this);

                ItemEntry entry = ammoStacks.peek();
                entry.ammoMultiplier -= ammoConsume;
                if(entry.ammoMultiplier <= 0) ammoStacks.pop();

                for (int i = 0; i < ammoConsume; i++){
                    float rot = Mathf.random(360);
                    float xOffset = 5f;
                    float yOffset = 0f;
                    (shootEffect == null ? bulletType.shootEffect : shootEffect).at(x + Angles.trnsx(rot, xOffset, yOffset), y + Angles.trnsy(rot, xOffset, yOffset), rot, bulletType.hitColor);
                    bulletType.create(this, team, x, y, rot, Mathf.random(0.5f, 1f), Mathf.random(0.2f, 1f));
                    shootSound.at(this, Mathf.random(soundPitchMin, soundPitchMax));
                }
                ammoTotal -= ammoConsume;
                ammoTotal = Math.max(ammoTotal, 0);
                shoot = manualActivation = false;
                warmup %= 1f;
            }

            shoot = (Units.closestEnemy(team, x, y, range, u -> u.type.killable && u.type.hittable) != null);
            super.updateTile();
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
                ItemEntry entry = ammoStacks.get(i);

                //if found, put it to the right
                //if found, put it to the right
                if(entry.item == item){
                    entry.ammoMultiplier += type.ammoMultiplier;
                    ammoStacks.swap(i, ammoStacks.size - 1);
                    return;
                }
            }

            ammoStacks.add(new ItemEntry(item, (int)type.ammoMultiplier));
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
        public int acceptStack(Item item, int amount, Teamc source){
            float ammoMultiplier = 0;
            BulletType type = null;
            for (var ammo : ammoTypes){
                if (ammo.key.item == item){
                    type = ammo.value;
                    ammoMultiplier = ammo.value.ammoMultiplier;
                    break;
                }
            }

            if(type == null) return 0;

            return Math.min((int)((maxAmmo - ammoTotal) / ammoMultiplier), amount);
        }

        @Override
        public void handleStack(Item item, int amount, Teamc source){
            for(int i = 0; i < amount; i++){
                handleItem(null, item);
            }
        }

        @Override
        public int removeStack(Item item, int amount){
            return 0;
        }

        @Override
        public void draw() {
            super.draw();
        }

        @Override
        public void drawConfigure() {
            Drawf.dashCircle(x, y, range, team.color);
        }

        @Override
        public void configured(Unit builder, Object value) {
            manualActivation = true;
        }

        Boolean containsItem (Item item){
            Seq<Item> ammoItems = new Seq<>();
            for (var ammo : ammoTypes){
                ammoItems.add(ammo.key.item); //Adds every item using ammo.
            }

            for(int i = 0; i < ammoItems.size; i++){
                Item entry = ammoItems.get(i);

                if(entry == item){
                    return true;
                }
            }
            return false;
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            ammoTotal = 0;
            ammoStacks.clear();

            int amount = read.ub();
            bulletType = Vars.content.bullet(read.s());
            ammoConsume = read.f();
            for(int i = 0; i < amount; i++){
                Item item = Vars.content.item(read.s());
                short a = read.s();

                //only add ammo if this is a valid ammo type
                if(item != null && containsItem(item)){
                    ammoTotal += a;
                    ammoStacks.add(new ItemEntry(item, a));
                }
            }
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.b(ammoStacks.size);
            write.s(bulletType.id);
            write.f(ammoConsume);
            for (var ammo : ammoStacks){
                write.s(ammo.item.id);
                write.s(ammo.ammoMultiplier);
            }
        }

        public class ItemEntry {
            public Item item;
            public int ammoMultiplier;

            ItemEntry(Item item, int ammoMultiplier){
                this.item = item;
                this.ammoMultiplier = ammoMultiplier;
            }
        }
    }
}
