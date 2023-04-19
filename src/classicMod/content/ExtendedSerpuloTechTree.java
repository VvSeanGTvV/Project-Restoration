package classicMod.content;

import arc.struct.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.Objectives.*;
import mindustry.type.*;

import static classicMod.content.ClassicBlocks.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.TechTree.*;

public class ExtendedSerpuloTechTree {
    static TechTree.TechNode context = null;

    public static void load() {
        margeNode(copperWallLarge, () -> {
            node(insulatorWall, () -> {
               node(insulatorWallLarge);
            });
        });

        margeNode(combustionGenerator, () -> {
            node(draugFactory, () -> {
                node(spiritFactory, () -> {
                    node(phantomFactory);
                });

                node(daggerFactory, () -> {
                    node(crawlerFactory, () -> {
                        node(titanFactory, () -> {
                            node(fortressFactory, () -> {

                            });
                        });
                    });

                    node(wraithFactory, () -> {
                        node(ghoulFactory, () -> {
                            node(revenantFactory, () -> {

                            });
                        });
                    });
                });
            });

            node(dartPad, () -> {
                node(deltaPad, () -> {

                    node(javelinPad, () -> {
                        node(tridentPad, () -> {
                            node(glaivePad);
                        });
                    });

                    node(tauPad, () -> {
                        node(omegaPad, () -> {

                        });
                    });
                });
            });
        });

        margeNode(fuse, () -> {
            node(fuseMKI, () -> {
                node(fuseMKII);
            });
        });
        margeNode(phaseConveyor, () -> {
            node(warpGate,() -> {
                node(teleporter);
            });
        });

        margeNode(coreShard, () -> {
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
        });

        margeNode(mechanicalDrill, () -> {
            node(denseSmelter, () -> {
                node(arcSmelter, () -> {

                });
            });
        });

        margeNode(duo, () -> {
            node(plasmaTurret, () -> {
                node(teslaTurret);
                node(chainTurret, () -> {
                    node(titanCannon);
                });
            });
        });

        margeNode(launchPad, () -> {
            node(launchPadLarge, () -> {
                //node(ClassicBlocks.interplanetaryAccelerator, Seq.with(new SectorComplete(planetaryTerminal)), () -> {}); //Endgame bois
            });
        });

        //item
        margeNode(Items.coal, () -> {
            nodeProduce(ClassicItems.denseAlloy, () -> {

            });
        });

        margeNode(coreShard, () -> {
            nodeProduce(ClassicItems.stone, () -> {
                nodeProduce(ClassicLiquids.lava);
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

    private static void margeNode(UnlockableContent parent, Runnable children){ //from betamindy!
        context = TechTree.all.find(t -> t.content == parent);
        children.run();
    }

    private static void node(UnlockableContent content, ItemStack[] requirements, Seq<Objective> objectives, Runnable children){
        TechNode node = new TechNode(context, content, requirements);
        if(objectives != null) node.objectives = objectives;

        TechNode prev = context;
        context = node;
        children.run();
        context = prev;
    }

    private static void node(UnlockableContent content, ItemStack[] requirements, Runnable children){
        node(content, requirements, null, children);
    }

    private static void node(UnlockableContent content, Seq<Objective> objectives, Runnable children){
        node(content, content.researchRequirements(), objectives, children);
    }

    private static void node(UnlockableContent content, Runnable children){
        node(content, content.researchRequirements(), children);
    }

    private static void node(UnlockableContent block){
        node(block, () -> {});
    }

    private static void nodeProduce(UnlockableContent content, Seq<Objective> objectives, Runnable children){
        node(content, content.researchRequirements(), objectives.add(new Produce(content)), children);
    }

    private static void nodeProduce(UnlockableContent content, Runnable children){
        nodeProduce(content, Seq.with(), children);
    }

    private static void nodeProduce(UnlockableContent content){
        nodeProduce(content, Seq.with(), () -> {});
    }
}
