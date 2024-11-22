package classicMod.library.generator;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.util.Tmp;
import arc.util.noise.Simplex;
import classicMod.content.ClassicBlocks;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.type.*;
import mindustry.world.*;

public class TantrosPlanetGenerator extends PlanetGenerator {
    Color c1 = Color.valueOf("5057a6"), c2 = Color.valueOf("272766");
    Color deepOcean = Color.valueOf("414891"), lightOcean = Color.valueOf("656cad"), ocean = Color.valueOf("5057a6");
    Color out = new Color();
    Block[][] arr;

    public TantrosPlanetGenerator() {
        arr = new Block[][]{{Blocks.redmat, Blocks.redmat, Blocks.darksand, Blocks.bluemat, Blocks.bluemat}};
        baseSeed = 1;
    }

    public void generateSector(Sector sector) {
    }

    public float getHeight(Vec3 position) {
        return 0.0F;
    }

    public boolean skip(int seed, Vec3 position, int octaves, float persistence, float scale, float threshold){
        return Simplex.noise3d(seed, octaves, persistence, scale, position.x, position.y, position.z) >= threshold;
    }

    public Color getColor(Vec3 position) {
        boolean light = skip(seed, position, 2, 0.08f, 1.75f, 0.25f); //Simplex.noise3d(seed, 2.0, 0.08f, 1.75f, position.x, position.y, position.z) >= 0.25f;
        boolean deep = skip(seed, position, 2, 0.08f, 1.75f, 0.45f);
        //boolean skip = Simplex.noise3d(seed, 2.0, 0.08f, 1.75f, position.x, position.y, position.z) >= 0.25f;
        return (light && !deep) ? lightOcean : (deep) ? deepOcean : ocean;
        //float depth = Simplex.noise3d(this.seed, 2.0, 0.56, 1.7000000476837158, position.x, position.y, position.z) / 2.0F;
        //return this.c1.write(this.out).lerp(this.c2, Mathf.clamp(Mathf.round(depth, 0.15F))).a(0.2F);
    }

    public float getSizeScl() {
        return 2000.0F;
    }

    public void addWeather(Sector sector, Rules rules) {
        rules.weather.add(new Weather.WeatherEntry(Weathers.suspendParticles)).peek().always = true;
    }

    public void genTile(Vec3 position, TileGen tile) {
        tile.floor = getBlock(position);
        if (tile.floor == Blocks.redmat && this.rand.chance(0.1)) {
            tile.block = Blocks.redweed;
        }

        if (tile.floor == Blocks.bluemat && this.rand.chance(0.03)) {
            tile.block = Blocks.purbush;
        }

        if (tile.floor == Blocks.bluemat && this.rand.chance(0.002)) {
            tile.block = Blocks.yellowCoral;
        }
        if (tile.floor == Blocks.redmat && this.rand.chance(0.001)) {
            tile.block = Blocks.yellowCoral;
        }

        if (tile.floor == Blocks.darksand && this.rand.chance(0.002)) {
            tile.block = ClassicBlocks.glowBlob;
        }

    }

    protected void generate() {
        this.pass((x, y) -> {
            float max = 0.0F;
            Point2[] var4 = Geometry.d8;
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                Point2 p = var4[var6];
                max = Math.max(max, Vars.world.getDarkness(x + p.x, y + p.y));
            }

            if (max > 0.0F) {
                block = floor.asFloor().wall;
            }

            if ((double)noise((float)x, (float)y, 50.0, 1.0) > 0.9) {
            }

        });
        Schematics.placeLaunchLoadout(width / 2, height / 2);
    }

    float rawHeight(Vec3 position) {
        return Simplex.noise3d(seed, 8.0, 0.699999988079071, 1.0, position.x, position.y, position.z);
    }

    Block getBlock(Vec3 position) {
        float height = this.rawHeight(position);
        Tmp.v31.set(position);
        position = Tmp.v33.set(position).scl(2.0F);
        float temp = Simplex.noise3d(seed, 8.0, 0.6, 0.5, position.x, position.y + 99.0F, position.z);
        height *= 1.2F;
        height = Mathf.clamp(height);
        return arr[Mathf.clamp((int)(temp * (float)arr.length), 0, arr[0].length - 1)][Mathf.clamp((int)(height * (float)arr[0].length), 0, arr[0].length - 1)];
    }
}

