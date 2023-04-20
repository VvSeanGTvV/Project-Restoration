package classicMod.content;

import static classicMod.content.ClassicBlocks.*;
import static mindustry.content.TechTree.*;

public class ClassicTechtree {
    //TODO make new campaign here

    public void load(){
        nodeRoot("Classic", coreSolo, () -> {
            node(teleporter);
            node(stoneDrill, () -> {
                node(ironDrill, () -> {
                    node(steelSmelter, () -> {
                        node(ClassicBlocks.melter, () -> {
                            node(lavaSmelter);
                            node(stoneFormer);
                        });
                        node(titaniumDrill, () -> {
                            node(crucible);
                            node(omniDrill);
                        });
                    });
                    node(uraniumDrill, () -> {
                        node(nuclearReactor);
                    });
                });
                node(coalDrill);
            });

            node(basicTurret, () -> {
                node(plasmaTurret, () -> {
                    node(teslaTurret);
                    node(chainTurret, () -> {
                        node(titanCannon);
                    });
                });
            });

            node(wallStone, () -> {
                node(wallIron, () -> {
                    node(wallSteel, () -> {
                        node(wallDirium, () -> {
                            node(wallComposite, () -> {});
                        });
                    });
                });
            });

            nodeProduce(ClassicItems.stone, () -> {
                nodeProduce(ClassicLiquids.lava, () -> {});
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
