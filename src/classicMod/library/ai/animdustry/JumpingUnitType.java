package classicMod.library.ai.animdustry;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Time;
import classicMod.library.ai.JumpingAI;
import classicMod.library.ai.OldFlyingAI;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;

public class JumpingUnitType extends UnitType {

    public float timing = 0f;

    public float timSine;

    public JumpingUnitType(String name) {
        super(name);
        controller = u -> new JumpingAI();
    }

    @Override
    public void draw(Unit unit) {
        timing += 0.07f * Time.delta;
        var sine = Mathf.sin(timing);
        if(sine < -0.85f) timing = 0;
        timSine = sine;
        Draw.rect(region, unit.x, unit.y, region.width + sine * 4, region.height - sine * 8);
    }
}
