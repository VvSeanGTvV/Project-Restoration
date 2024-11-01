package classicMod.content;

import arc.Core;
import arc.func.Boolf;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.*;
import arc.util.*;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.bullet.BulletType;
import mindustry.type.*;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.meta.*;

import static mindustry.Vars.*;
import static mindustry.world.meta.StatValues.*;

public class ExtendedStat {
    public static final Stat
            StatusOutput = new Stat("status-give", StatCat.function),
            StatusDuration = new Stat("status-give-duration", StatCat.function),

            fuel = new Stat("fuel", StatCat.crafting),
            inbetweenTeleport = new Stat("inbetween-teleport", StatCat.items),
            burnTime = new Stat("burn-time", StatCat.crafting),
            canBreak = new Stat("can-break", StatCat.function),
            launchSector = new Stat("launch-sector", StatCat.function),
            itemsMovedBase = new Stat("itemsmoved-base", StatCat.items),
            itemsMovedBoost = new Stat("itemsmoved-boost", StatCat.optional),
            tierLevel = new Stat("unit-level")
    ;

    public static StatValue drillablesStack(float drillTime, int outputAmount, ObjectFloatMap<Item> multipliers, Boolf<Block> filter){
        return table -> {
            table.row();
            table.table(c -> {
                int i = 0;
                for(Block block : content.blocks()){
                    if(!filter.get(block)) continue;

                    c.table(Styles.grayPanel, b -> {
                        b.image(block.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                        b.table(info -> {
                            info.left();
                            info.add(block.localizedName).left().row();
                            info.add(block.itemDrop.emoji()).left();
                        }).grow();
                        if(multipliers != null){
                            b.add(Strings.autoFixed(drillTime, 2) + StatUnit.perSecond.localized())
                                    .right().pad(10f).padRight(15f).color(Color.lightGray);
                        }
                    }).growX().pad(5);
                    if(++i % 2 == 0) c.row();
                }
            }).growX().colspan(table.getColumns());
        };
    }

    public static StatValue squaredRange(float value, StatUnit unit){
        return table -> {
            String fixed = fixValue(value);
            table.add(fixed);
            table.add((unit.space ? " " : "") + unit.localized());
        };
    }

    public static StatValue ammo(ObjectMap<ItemStack, BulletType> map){
        return ammo(map, 0, false);
    }

    private static TextureRegion icon(UnlockableContent t){
        return t.uiIcon;
    }

    //for AmmoListValue
    private static void sep(Table table, String text){
        table.row();
        table.add(text);
    }

    //for AmmoListValue
    private static String ammoStat(float val){
        return (val > 0 ? "[stat]+" : "[negstat]") + Strings.autoFixed(val, 1);
    }

    public static StatValue ammo(ObjectMap<ItemStack, BulletType> map, int indent, boolean showUnit) {
        return table -> {

            table.row();

            var orderedKeys = map.keys().toSeq();
            orderedKeys.sort();

            for(var t : orderedKeys){
                boolean compact = indent > 0;

                BulletType type = map.get(t);

                table.table(Styles.grayPanel, bt -> {
                    bt.left().top().defaults().padRight(3).left();
                    bt.table(title -> {
                        title.add(displayItem(t.item, t.amount, true)).padRight(5);
                        //title.image(icon(t.item)).size(3 * 8).padRight(4).right().scaling(Scaling.fit).top();
                        //title.add(t.item.localizedName).padRight(10).left().top();
                        //title.add(String.valueOf(t.amount));
                    });

                    bt.row();

                    if(type.damage > 0 && (type.collides || type.splashDamage <= 0)){
                        if(type.continuousDamage() > 0){
                            bt.add(Core.bundle.format("bullet.damage", type.continuousDamage()) + StatUnit.perSecond.localized());
                        }else{
                            bt.add(Core.bundle.format("bullet.damage", type.damage));
                        }
                    }

                    if(type.buildingDamageMultiplier != 1){
                        int val = (int)(type.buildingDamageMultiplier * 100 - 100);
                        sep(bt, Core.bundle.format("bullet.buildingdamage", ammoStat(val)));
                    }

                    if(type.rangeChange != 0 && !compact){
                        sep(bt, Core.bundle.format("bullet.range", ammoStat(type.rangeChange / tilesize)));
                    }

                    if(type.splashDamage > 0){
                        sep(bt, Core.bundle.format("bullet.splashdamage", (int)type.splashDamage, Strings.fixed(type.splashDamageRadius / tilesize, 1)));
                    }

                    if(!compact && !Mathf.equal(type.ammoMultiplier, 1f) && type.displayAmmoMultiplier){
                        sep(bt, Core.bundle.format("bullet.multiplier", (int)type.ammoMultiplier));
                    }
                });
                table.row();
            }
        };
    }

}
