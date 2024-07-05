package classicMod.content;

import mindustry.content.Items;

import static classicMod.content.ClassicBlocks.*;
import static mindustry.content.TechTree.*;

public class ClassicTechtree {
    //TODO move this to a seperate mod!

    public void load(){
        nodeRoot("Classic", coreSolo, () -> {
            node(tunnelBridge, () -> {
                node(teleporter);
            });

            node(stoneDrill, () -> {
                node(ironDrill, () -> {
                    node(titaniumDrill, () -> {
                        node(omniDrill);
                    });
                });

                node(coalDrill, () -> {
                    node(steelSmelter, () -> {
                        node(pumpBasic, () -> {
                            node(purifierCoal, () -> {
                                node(purifierTitanium);
                            });
                            node(pumpFlux);
                        });
                        node(ClassicBlocks.melter, () -> {
                            node(lavaSmelter);
                            node(stoneFormer);
                        });
                        node(crucible);
                    });
                    node(uraniumDrill, () -> {
                        node(nuclearReactor);
                    });
                });
            });

            node(basicTurret, () -> {
                node(doubleTurret, () -> {
                    node(shotgunTurret, () -> {
                        node(chainTurret);
                    });
                });
                node(gattlingTurret, () -> {
                    node(sniperTurret, () -> {
                        node(mortarTurret, () -> {
                            node(titanCannon);
                        });
                    });
                    node(flameTurret, () -> {
                        node(plasmaTurret, () -> {

                        });
                        node(laserTurret, () -> {
                            node(teslaTurret, () -> {

                            });
                        });
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
                        nodeProduce(Items.titanium, () -> {});
                        nodeProduce(ClassicItems.dirium, () -> {

                        });
                    });
                });
            });
        });
    }
}
