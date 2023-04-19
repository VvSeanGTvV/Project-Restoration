package classicMod.content;

import static classicMod.content.ClassicBlocks.*;
import static mindustry.content.TechTree.*;

public class ClassicTechtree {
    //TODO make new campaign here

    public void load(){
        nodeRoot("serpulo", coreSolo, () -> {
            node(stoneDrill, () -> {
                node(ironDrill, () -> {
                    node(steelSmelter, () -> {
                        node(crucible);
                        node(ClassicBlocks.melter, () -> {
                            node(lavaSmelter);
                            node(stoneFormer);
                        });
                        node(titaniumDrill, () -> {
                            node(omniDrill);
                        });
                    });
                    node(uraniumDrill);
                });
                node(coalDrill);
            });

            node(plasmaTurret, () -> {
                node(teslaTurret);
                node(chainTurret, () -> {
                    node(titanCannon);
                });
            });

            nodeProduce(ClassicItems.stone, () -> {
                //nodeProduce(ClassicLiquids.lava);
                nodeProduce(ClassicItems.uranium, () -> {

                });
                nodeProduce(ClassicItems.iron, () -> {
                    nodeProduce(ClassicItems.steel, () -> {
                        nodeProduce(ClassicItems.dirium, () -> {

                        });
                    });
                });
            });
        });
    }
}
