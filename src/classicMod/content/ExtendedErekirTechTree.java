package classicMod.content;

import arc.struct.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.game.Objectives.*;
import mindustry.type.*;

import static classicMod.content.ClassicBlocks.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.*;
import static mindustry.content.SectorPresets.*;

public class ExtendedErekirTechTree {
    static TechTree.TechNode context = null;

    public static void load() {
        /*margeNode(breach, () -> {
            node(ClassicBlocks.shieldBreaker, Seq.with(new OnSector(intersect)), () -> {
                node(ClassicBlocks.barrierProjector,Seq.with(new Objectives.Research(surgeCrucible), new Produce(scrap)), () -> {

                });
            });
        });
        margeNode(breach, () -> {

        });*/

        margeNode(diffuse, () -> {
            node(fracture, Seq.with(new Objectives.OnSector(aegis)), () -> {
                node(horde,Seq.with(new Objectives.Research(ClassicBlocks.slagCentrifuge), new Produce(scrap)), () -> {});
            });
        });

        margeNode(breach, () -> {
            node(tinyBreach);
        });

        margeNode(afflict, () -> {
            node(chrome,Seq.with(new Objectives.Research(ClassicBlocks.heatReactor), new Produce(fissileMatter)), () -> {});
        });

        margeNode(armoredDuct, () -> {
            node(surgeDuct);
        });

        margeNode(electricHeater, () -> {
            node(ClassicBlocks.heatReactor, Seq.with(new Objectives.OnSector(stronghold), new Produce(thorium) ,new Objectives.Research(atmosphericConcentrator), new Produce(nitrogen)), () -> {

            });
        });

        margeNode(fluxReactor, () -> {
            node(cellSynthesisChamber, Seq.with(new Produce(carbide), new Produce(cyanogen), new Produce(phaseFabric)), () -> {});
        });

        margeNode(slagIncinerator, () -> {
            node(ClassicBlocks.slagCentrifuge,Seq.with(new Objectives.OnSector(crevice), new Produce(Items.sand), new Produce(Liquids.slag)), () -> {});
        });

        margeNode(basicAssemblerModule, () -> {
            node(droneCenter,Seq.with(new Produce(phaseFabric)), () -> {});
        });

        margeNode(carbide, () -> {
            nodeProduce(Liquids.gallium, () -> {});
        });

        margeNode(oxide, () -> {
            nodeProduce(fissileMatter);
            nodeProduce(scrap);
        });

        margeNode(reinforcedVault, () -> {
            node(reinforcedSafe);
        });
    }


    private static void margeNode(UnlockableContent parent, Runnable children){ //from betamindy!
        context = TechTree.all.find(t -> t.content == parent);
        children.run();
    }

    private static void node(UnlockableContent content, ItemStack[] requirements, Seq<Objectives.Objective> objectives, Runnable children){
        TechTree.TechNode node = new TechTree.TechNode(context, content, requirements);
        if(objectives != null) node.objectives = objectives;

        TechTree.TechNode prev = context;
        context = node;
        children.run();
        context = prev;
    }

    private static void node(UnlockableContent content, ItemStack[] requirements, Runnable children){
        node(content, requirements, null, children);
    }

    private static void node(UnlockableContent content, Seq<Objectives.Objective> objectives, Runnable children){
        node(content, content.researchRequirements(), objectives, children);
    }

    private static void node(UnlockableContent content, Runnable children){
        node(content, content.researchRequirements(), children);
    }

    private static void node(UnlockableContent block){
        node(block, () -> {});
    }

    private static void nodeProduce(UnlockableContent content, Seq<Objectives.Objective> objectives, Runnable children){
        node(content, content.researchRequirements(), objectives.add(new Objectives.Produce(content)), children);
    }

    private static void nodeProduce(UnlockableContent content, Runnable children){
        nodeProduce(content, Seq.with(), children);
    }

    private static void nodeProduce(UnlockableContent content){
        nodeProduce(content, Seq.with(), () -> {});
    }
}
