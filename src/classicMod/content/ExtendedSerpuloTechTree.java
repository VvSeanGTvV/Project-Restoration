package classicMod.content;

import arc.struct.Seq;
import mindustry.content.*;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Objectives.*;
import mindustry.type.ItemStack;

import static classicMod.content.ClassicBlocks.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.SectorPresets.*;
import static mindustry.content.TechTree.*;

public class ExtendedSerpuloTechTree {
    static TechTree.TechNode context = null;

    public static void load() {

        margeNode(combustionGenerator, () -> {
            node(draugFactory, () -> {
                node(spiritFactory, () -> {
                    node(phantomFactory);
                });

                node(daggerFactory, () -> {
                    node(commandCenter, () -> {});
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

        margeNode(ripple, () -> {
            node(rippleb41);
            node(fuseMKI, () -> {
                node(fuseMKII);
            });
        });

        margeNode(arc, () -> node(arcAir));

        margeNode(laserDrill, () -> {
            node(nuclearDrill,() -> {

            });
        });

        // Transportation
        margeNode(phaseConveyor, () -> {
            node(laserConveyor,() -> {

            });
        });

        margeNode(massDriver, () -> {
            node(warpGate,() -> {

            });
            node(payloadPropulsionTower,() -> {

            });
        });

        margeNode(Blocks.melter, () -> {

        });

        margeNode(graphitePress, () -> {
            node(denseSmelter, () -> {
                node(crucible);
                node(arcSmelter, () -> {
                    //node(alloySmelter);
                });
            });
        });

        margeNode(pneumaticDrill, () -> {
            node(smolSeparator, () -> {
                node(stoneMelter, () -> {
                    node(stoneFormer);
                });
                node(centrifuge);
            });
            node(poweredDrill);
        });

        margeNode(Blocks.rtgGenerator, () -> {
            node(ClassicBlocks.rtgGenerator);
        });

        //wall
        margeNode(copperWallLarge, () -> {
            node(insulatorWall, () -> {
                node(insulatorWallLarge);
                node(wallDense, () -> {
                    node(wallDenseLarge);

                });
            });

        });

        margeNode(titaniumWall, () -> {
            node(wallDirium, () -> {
                node(wallDiriumLarge);
                node(wallComposite, () -> {});
            });
        });

        margeNode(titaniumWallLarge, () -> {
            node(wallShieldedTitanium);
        });

        /*margeNode(copperWall, () -> {
            node(wallStone, () -> {
                node(wallIron, () -> {
                    node(wallSteel, () -> {
                        node(wallSteelLarge);
                        node(wallDirium, () -> {
                            node(wallDiriumLarge);
                            node(wallComposite, () -> {});
                        });
                    });
                });
            });
        });*/

        //item
        margeNode(Items.lead, () -> {
            nodeProduce(ClassicItems.denseAlloy, () -> {

            });
        });

        margeNode(Liquids.slag, () -> {
            nodeProduce(ClassicItems.stone, () -> {

            });
        });

        // etc.
        margeNode(impulsePump, () -> {
            node(thermalPump);
        });

        // Launchpad
        margeNode(planetaryTerminal, () -> {
            nodePlanetary(onset, ClassicBlocks.interplanetaryAccelerator, () -> {});
        });

        margeNode(launchPad, () -> {
            node(launchPadLarge, () -> {
                node(ClassicBlocks.interplanetaryAccelerator, Seq.with(new SectorComplete(planetaryTerminal)), () -> {}); //Endgame bois
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

    private static void nodePlanetary(UnlockableContent content, UnlockableContent thisOne, Runnable children){
            node(content, Seq.with(new VisualObjectives.LaunchedPlanetaryAccelerator(thisOne)), children);
    }
}
