package v5mod.content;

import arc.func.Prov;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.struct.*;
import arc.struct.ObjectMap.Entry;
import mindustry.content.*;
import mindustry.entities.abilities.MoveEffectAbility;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.type.weapons.PointDefenseWeapon;
import mindustry.world.blocks.payloads.PayloadSource;
import mindustry.world.meta.BlockFlag;
import static mindustry.Vars.*;

public class V5UnitTypes {
    public static UnitType 
    //Mech
    omega;

    public static void load(){

        omega = new UnitType("omega"){{
            speed = 0.5f;
            hitSize = 8f;
            health = 150;
        }};
    }
    
}
