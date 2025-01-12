package classicMod.content;

import arc.struct.Seq;
import arc.util.Log;
import mindustry.content.*;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Objectives;
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
        margeNode(radar, () -> {
            node(ClassicBlocks.barrierProjector, Seq.with(
                    new OnSector(aegis)
            ), () -> {});

            node(ClassicBlocks.shieldBreaker, Seq.with(
                    new SectorComplete(intersect)
            ), () -> {

            });

        });

        margeNode(diffuse, () -> {
            node(fracture, Seq.with(new OnSector(aegis)), () -> {
                node(horde,Seq.with(
                        new Research(ClassicBlocks.slagCentrifuge),
                        new Produce(scrap)
                ), () -> {

                });
            });
        });

        margeNode(breach, () -> {
            node(tinyBreach);
        });

        margeNode(afflict, () -> {
            node(chrome, Seq.with(
                    new Research(ClassicBlocks.heatReactor),
                    new Produce(fissileMatter)
            ), () -> {

            });
        });

        margeNode(duct, () -> {
            node(ductJunction);
        });

        margeNode(armoredDuct, () -> {
            node(surgeDuct);
        });

        margeNode(electricHeater, () -> {
            node(ClassicBlocks.heatReactor, Seq.with(new OnSector(stronghold), new Produce(thorium) ,new Research(atmosphericConcentrator), new Produce(nitrogen)), () -> {

            });
        });

        margeNode(fluxReactor, () -> {
            node(cellSynthesisChamber, Seq.with(new Produce(carbide), new Produce(cyanogen), new Produce(phaseFabric)), () -> {});
        });

        margeNode(slagIncinerator, () -> {
            node(ClassicBlocks.slagCentrifuge,Seq.with(new OnSector(crevice), new Produce(Items.sand), new Produce(Liquids.slag)), () -> {});
        });

        margeNode(basicAssemblerModule, () -> {
            node(droneCenter, Seq.with(new Produce(phaseFabric)), () -> {});
        });

        margeNode(cliffCrusher, () ->{
            node(ClassicBlocks.largeCliffCrusher);
        });

        margeNodeSpecific(Liquids.slag, tungsten, () -> {
            nodeProduce(scrap, () -> nodeProduce(Liquids.gallium));
        });

        margeNodeSpecific(thorium, tungsten, () -> {
            nodeProduce(fissileMatter);
        });

        margeNode(reinforcedVault, () -> {
            node(reinforcedSafe);
        });

        margeNode(UnitTypes.vanquish, () -> {
            //node(ClassicUnitTypes.howit, () -> node(ClassicUnitTypes.mantel));
        });
    }


    private static void margeNode(UnlockableContent parent, Runnable children){ //from betamindy!
        context = TechTree.all.find(t -> t.content == parent);
        children.run();
    }

    static TechTree.TechNode findParentedContent(UnlockableContent parent){
        for (var t : TechTree.all){
            if (t.content != null) {
                if (t.content == parent) return t;
            }
        }
        return null;
    }

    private static void margeNodeSpecific(UnlockableContent parent, UnlockableContent previous, Runnable children){ //modification
        var parented = findParentedContent(previous);
        context = TechTree.all.find(t -> t.content == parent && t.parent == parented);
        children.run();
    }

    private static void node(UnlockableContent content, ItemStack[] requirements, Seq<Objective> objectives, Runnable children){
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
