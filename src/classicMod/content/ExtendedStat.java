package classicMod.content;

import arc.func.Boolf;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.struct.*;
import arc.util.*;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.bullet.BulletType;
import mindustry.type.*;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.meta.*;

import static mindustry.Vars.content;
import static mindustry.world.meta.StatValues.fixValue;

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

    public static <T extends ItemStack> StatValue ammo(ObjectMap<T, BulletType> map){
        return ammo(map, 0, false);
    }

    private static TextureRegion icon(UnlockableContent t){
        return t.uiIcon;
    }


    public static <T extends ItemStack> StatValue ammo(ObjectMap<T, BulletType> map, int indent, boolean showUnit) {
        return table -> {

            table.row();

            var orderedKeys = map.keys().toSeq();
            orderedKeys.sort();

            for(T t : orderedKeys){
                BulletType type = map.get(t);

                table.table(Styles.grayPanel, bt -> {
                    bt.left().top().defaults().padRight(3).left();
                    bt.table(title -> {
                        String amount = String.valueOf(t.amount);
                        title.image(icon(t.item)).size(3 * 8).padRight(4).right().scaling(Scaling.fit).top();
                        title.add(t.item.localizedName).padRight(10).left().top();
                        title.add(amount);
                    });
                });
            }
        };
    }

}
