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

    private float timing = 0f;

    private float timSine;

    public JumpingUnitType(String name) {
        super(name);
        controller = u -> new JumpingAI();
    }

    @Override
    public void draw(Unit unit) {
        this.timing += 0.1f * Time.delta;
        var sine = Mathf.sin(this.timing);
        Draw.z(Layer.groundUnit);
        if(sine < -0.85f) this.timing = 2f;
        if(sine > 0f) {
            this.timSine = sine;
            Draw.rect(region, unit.x, unit.y + 2 - sine, ((float) region.width / 2) + sine * 5, ((float) region.height / 2) - sine * 10);
        } else {
            Draw.rect(region, unit.x, unit.y + 2, (float) region.width /2, (float) region.height /2);
        }
    }

    public float getTimSine(){
        return timSine;
    }
}
