package classicMod.library.ui.dialog;

import arc.*;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.input.KeyCode;
import arc.math.*;
import arc.math.geom.Rect;
import arc.scene.*;
import arc.scene.actions.*;
import arc.scene.event.*;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import classicMod.library.ui.UIExtended;
import mindustry.content.TechTree;
import mindustry.content.TechTree.TechNode;
import mindustry.ctype.ContentType;
import mindustry.game.EventType;
import mindustry.game.Objectives.Objective;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.layout.BranchTreeLayout;
import mindustry.ui.layout.TreeLayout.TreeNode;

import java.util.Arrays;

import static arc.Core.settings;
import static mindustry.Vars.*;
import static mindustry.gen.Tex.buttonOver;

public class TechTreeDialog extends BaseDialog {
    private final float nodeSize = Scl.scl(60f);
    private final ObjectSet<TechTreeNode> nodes = new ObjectSet<>();
    private TechTreeNode root = new TechTreeNode(TechTree.roots.first(), null);
    private Rect bounds = new Rect();
    public ItemsDisplay itemDisplay;
    private boolean showTechSelect;
    private View view;
    private Cons<TechNode> selector = c -> {};
    private TechNode selectorNode;

    public TechTreeDialog(){
        super("");

        titleTable.remove();
        titleTable.clear();
        titleTable.top();
        titleTable.button(b -> {
            //TODO custom icon here.
            b.imageDraw(() -> root.node.icon()).padRight(8).size(iconMed);
            b.add().growX();
            b.label(() -> root.node.localizedName()).color(Pal.accent);
            b.add().growX();
            b.add().size(iconMed);
        }, () -> {
        }).visible(() -> showTechSelect = TechTree.roots.count(node -> !(node.requiresUnlock && !node.content.unlocked())) > 1).minWidth(300f);
        margin(0f).marginBottom(8);
        Stack stack = cont.stack(view = new View()/*, items = new ItemsDisplay()*/).grow().get();

        shouldPause = true;

        //Events.on(ContentReloadEvent.class, e -> {
        TechNode TechTreeDef = root.node;
        Planet currPlanet = ui.planet.isShown() ? ui.planet.state.planet : state.isCampaign() ? state.rules.sector.planet : null;
        Planet planet = content.getByName(ContentType.planet, settings.getString("lastplanet", "serpulo"));
        if(currPlanet != null)TechTreeDef = currPlanet.techTree;
        nodes.clear();
        root = new TechTreeNode(TechTreeDef, null); //TechTree.roots.first()
        checkNodes(root);
        treeLayout();
        stack.getChildren().get(0).remove();
        stack.addChildAt(0, view = new View());
        refixPan();
        //});

        shown(() -> {
            checkNodes(root);
            treeLayout();
        });

        //hidden(ui.planet::setup);
        //onResize(this::checkMargin);
        //cont.stack(titleTable, view = new View(), itemDisplay = new ItemsDisplay()).grow();
        shouldPause = true;

        addCloseButton();

        buttons.button("@database", Icon.book, () -> {
            hide();
            ui.database.show();
        }).size(210f, 64f);

        //scaling/drag input
        addListener(new InputListener(){
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY){
                view.setScale(Mathf.clamp(view.scaleX - amountY / 10f * view.scaleX, 0.25f, 1f));
                view.setOrigin(Align.center);
                view.setTransform(true);
                return true;
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y){
                view.requestScroll();
                return super.mouseMoved(event, x, y);
            }
        });

        touchable = Touchable.enabled;

        addListener(new ElementGestureListener(){
            @Override
            public void zoom(InputEvent event, float initialDistance, float distance){
                if(view.lastZoom < 0){
                    view.lastZoom = view.scaleX;
                }

                view.setScale(Mathf.clamp(distance / initialDistance * view.lastZoom, 0.25f, 1f));
                view.setOrigin(Align.center);
                view.setTransform(true);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button){
                view.lastZoom = view.scaleX;
            }

            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY){
                view.panX += deltaX / view.scaleX;
                view.panY += deltaY / view.scaleY;
                view.moved = true;
                view.clamp();
            }
        });
    }

    public void refixPan(){
        view.panX = 0f;
        view.panY = -200f;
        view.setScale(1f);

        view.hoverNode = null;
        view.infoTable.remove();
        view.infoTable.clear();
    }


    protected void onResize(Runnable run){
        Events.on(EventType.ResizeEvent.class, event -> {
            if(isShown() && Core.scene.getDialog() == this){
                run.run();
                updateScrollFocus();
            }
        });
    }

    void checkMargin(){
        if(Core.graphics.isPortrait()){
            itemDisplay.marginTop(60f);
        }else{
            itemDisplay.marginTop(0f);
        }
        itemDisplay.invalidate();
        itemDisplay.layout();
    }


    public Dialog show(Cons<TechNode> selector){
        this.selector = selector;
        return super.show();
    }

    public Dialog show(){
        return show(c -> {});
    }
    
    public TechNode getSelector(){
        return selectorNode;
    }

    public void NullifyNode(){
        selectorNode = null;
    }

    void treeLayout(){
        float spacing = 20f;
        LayoutNode node = new LayoutNode(root, null);
        LayoutNode[] children = node.children;
        LayoutNode[] leftHalf = Arrays.copyOfRange(node.children, 0, Mathf.ceil(node.children.length/2f));
        LayoutNode[] rightHalf = Arrays.copyOfRange(node.children, Mathf.ceil(node.children.length/2f), node.children.length);

        node.children = leftHalf;
        new BranchTreeLayout(){{
            gapBetweenLevels = gapBetweenNodes = spacing;
            rootLocation = TreeLocation.top;
        }}.layout(node);

        float lastY = node.y;
        if(rightHalf.length > 0){

            node.children = rightHalf;
            new BranchTreeLayout(){{
                gapBetweenLevels = gapBetweenNodes = spacing;
                rootLocation = TreeLocation.bottom;
            }}.layout(node);

            shift(leftHalf, node.y - lastY);
        }

        node.children = children;

        float minx = 0f, miny = 0f, maxx = 0f, maxy = 0f;
        copyInfo(node);

        for(TechTreeNode n : nodes){
            if(!n.visible) continue;
            minx = Math.min(n.x - n.width/2f, minx);
            maxx = Math.max(n.x + n.width/2f, maxx);
            miny = Math.min(n.y - n.height/2f, miny);
            maxy = Math.max(n.y + n.height/2f, maxy);
        }
        bounds = new Rect(minx, miny, maxx - minx, maxy - miny);
        bounds.y += nodeSize*1.5f;
    }

    void shift(LayoutNode[] children, float amount){
        for(LayoutNode node : children){
            node.y += amount;
            if(node.children != null && node.children.length > 0) shift(node.children, amount);
        }
    }

    void copyInfo(LayoutNode node){
        node.node.x = node.x;
        node.node.y = node.y;
        if(node.children != null){
            for(LayoutNode child : node.children){
                copyInfo(child);
            }
        }
    }

    void checkNodes(TechTreeNode node){
        boolean locked = locked(node.node);
        if(!locked) node.visible = true;
        for(TechTreeNode l : node.children){
            l.visible = !locked;
            checkNodes(l);
        }
    }

    void showToast(String info){
        Table table = new Table();
        table.actions(Actions.fadeOut(0.5f, Interp.fade), Actions.remove());
        table.top().add(info);
        table.name = "toast";
        table.update(() -> {
            table.toFront();
            table.setPosition(Core.graphics.getWidth() / 2f, Core.graphics.getHeight() - 21, Align.top);
        });
        Core.scene.add(table);
    }

    boolean locked(TechNode node){
        return node.content.locked();
    }

    class LayoutNode extends TreeNode<LayoutNode>{
        final TechTreeNode node;

        LayoutNode(TechTreeNode node, LayoutNode parent){
            this.node = node;
            this.parent = parent;
            this.width = this.height = nodeSize;
            if(node.children != null){
                children = Seq.with(node.children).map(t -> new LayoutNode(t, this)).toArray(LayoutNode.class);
            }
        }
    }

    class TechTreeNode extends TreeNode<TechTreeNode>{
        final TechNode node;
        boolean visible = true;

        TechTreeNode(TechNode node, TechTreeNode parent){
            this.node = node;
            this.parent = parent;
            this.width = this.height = nodeSize;
            nodes.add(this);
            if(node.children != null){
                children = new TechTreeNode[node.children.size];
                for(int i = 0; i < children.length; i++){
                    children[i] = new TechTreeNode(node.children.get(i), this);
                }
            }
        }
    }

    class View extends Group{
        float panX = 0, panY = -200, lastZoom = -1;
        boolean moved = false;
        ImageButton hoverNode;
        Table infoTable = new Table();

        {
            infoTable.touchable = Touchable.enabled;

            for(TechTreeNode node : nodes){
                ImageButton button = new ImageButton(node.node.content.uiIcon, Styles.nodei);
                button.resizeImage(32f);
                button.getImage().setScaling(Scaling.fit);
                button.visible(() -> node.visible);
                button.clicked(() -> {
                    if(moved) return;

                    if(mobile){
                        hoverNode = button;
                        rebuild();
                        float right = infoTable.getRight();
                        if(right > Core.graphics.getWidth()){
                            float moveBy = right - Core.graphics.getWidth();
                            addAction(new RelativeTemporalAction(){
                                {
                                    setDuration(0.1f);
                                    setInterpolation(Interp.fade);
                                }

                                @Override
                                protected void updateRelative(float percentDelta){
                                    panX -= moveBy * percentDelta;
                                }
                            });
                        }
                    }else if(locked(node.node)){
                        selector.get(node.node);
                    }
                    //TODO select it
                    //else if(data.hasItems(node.node.requirements) && locked(node.node)){
                    //    unlock(node.node);
                    //}
                });
                button.hovered(() -> {
                    if(!mobile && hoverNode != button && node.visible){
                        hoverNode = button;
                        rebuild();
                    }
                });
                button.exited(() -> {
                    if(!mobile && hoverNode == button && !infoTable.hasMouse() && !hoverNode.hasMouse()){
                        hoverNode = null;
                        rebuild();
                    }
                });
                button.touchable(() -> !node.visible ? Touchable.disabled : Touchable.enabled);
                button.userObject = (node.node);
                button.setSize(nodeSize);
                button.update(() -> {
                    float offset = (Core.graphics.getHeight() % 2) / 2f;
                    button.setPosition(node.x + panX + width / 2f, node.y + panY + height / 2f + offset, Align.center);
                    button.getStyle().up = !locked(node.node) ? buttonOver : Tex.button;
                    ((TextureRegionDrawable)button.getStyle().imageUp)
                            .setRegion(node.visible ? node.node.content.uiIcon : Icon.lock.getRegion());
                    button.getImage().setColor(!locked(node.node) ? Color.white : Color.gray);
                });
                addChild(button);
            }

            if(mobile){
                tapped(() -> {
                    Element e = Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true);

                    if(e == this){
                        hoverNode = null;
                        rebuild();
                    }
                });
            }

            setOrigin(Align.center);
            setTransform(true);
            released(() -> moved = false);
        }

        void clamp(){
            float pad = nodeSize;

            float ox = width/2f, oy = height/2f;
            float rx = bounds.x + panX + ox, ry = panY + oy + bounds.y;
            float rw = bounds.width, rh = bounds.height;
            rx = Mathf.clamp(rx, -rw + pad, Core.graphics.getWidth() - pad);
            ry = Mathf.clamp(ry, -rh + pad, Core.graphics.getHeight() - pad);
            panX = rx - bounds.x - ox;
            panY = ry - bounds.y - oy;
        }

        /*
        void unlock(TechNode node){
            data.unlockContent(node.content);
            //TODO this should not happen
            //data.removeItems(node.requirements);
            showToast(Core.bundle.format("researched", node.content.localizedName));
            checkNodes(root);
            hoverNode = null;
            treeLayout();
            rebuild();
            Core.scene.act();
            Sounds.unlock.play();
            Events.fire(new ResearchEvent(node.content));
        }*/
        
        void SelectNode(TechNode node){
            selectorNode = node;
            UIExtended.Techtree.hide();
        }

        void rebuild(){
            ImageButton button = hoverNode;

            infoTable.remove();
            infoTable.clear();
            infoTable.update(null);

            if(button == null) return;

            TechNode node = (TechNode)button.userObject;

            infoTable.exited(() -> {
                if(hoverNode == button && !infoTable.hasMouse() && !hoverNode.hasMouse()){
                    hoverNode = null;
                    rebuild();
                }
            });

            infoTable.update(() -> infoTable.setPosition(button.x + button.getWidth(), button.y + button.getHeight(), Align.topLeft));

            infoTable.left();
            infoTable.background(Tex.button).margin(8f);

            infoTable.table(b -> {
                b.margin(0).left().defaults().left();

                b.button(Icon.info, Styles.cleari, () -> ui.content.show(node.content)).growY().width(50f);
                b.add().grow();
                b.table(desc -> {
                    desc.left().defaults().left();
                    desc.add(node.content.localizedName);
                    desc.row();
                    if(locked(node)){
                        desc.table(t -> {
                            t.left();
                            for(ItemStack req : node.requirements){
                                t.table(list -> {
                                    list.left();
                                    list.image(req.item.uiIcon).size(8 * 3).padRight(3);
                                    list.add(req.item.localizedName).color(Color.lightGray);
                                    list.label(() -> " " + (player.team().core() != null ? Math.min(player.team().core().items.get(req.item), req.amount) + " / " : "") + req.amount)
                                            .update(l -> {}/*l.setColor(data.has(req.item, req.amount) ? Color.lightGray : Color.scarlet)*/);//TODO
                                }).fillX().left();
                                t.row();
                            }

                            //TODO test if this works
                            if(node.objectives.size > 0){
                                t.table(r -> {
                                    r.add("@complete").colspan(2).left();
                                    r.row();
                                    for(Objective o : node.objectives){
                                        r.image(Icon.right).padRight(4);
                                        r.add(o.display()).color(Color.lightGray);
                                        r.image(o.complete() ? Icon.ok : Icon.cancel, o.complete() ? Color.lightGray : Color.scarlet).padLeft(3);
                                        r.row();
                                    }
                                });
                                t.row();
                            }
                        });
                    }else{
                        desc.add("@completed");
                    }
                }).pad(9);

                if(mobile && locked(node)){
                    b.row();
                    b.button("@research", Icon.ok, () -> SelectNode(node)).growX().height(44f).colspan(3);
                }

                //TODO research select button
                /*
                if(mobile && locked(node)){
                    b.row();
                    b.button("$research", Icon.ok, Styles.nodet, () -> unlock(node))
                    .disabled(i -> !data.hasItems(node.requirements)).growX().height(44f).colspan(3);
                }*/
            });

            infoTable.row();
            if(node.content.description != null){
                infoTable.table(t -> t.margin(3f).left().labelWrap(node.content.displayDescription()).color(Color.lightGray).growX()).fillX();
            }

            addChild(infoTable);
            infoTable.pack();
            infoTable.act(Core.graphics.getDeltaTime());
        }

        @Override
        public void drawChildren(){
            clamp();
            float offsetX = panX + width / 2f, offsetY = panY + height / 2f;
            Draw.sort(true);

            for(TechTreeNode node : nodes){
                if(!node.visible) continue;
                for(TechTreeNode child : node.children){
                    if(!child.visible) continue;

                    Lines.stroke(Scl.scl(4f), locked(node.node) || locked(child.node) ? Pal.gray : Pal.accent);
                    Draw.alpha(parentAlpha);
                    if(Mathf.equal(Math.abs(node.y - child.y), Math.abs(node.x - child.x), 1f) && Mathf.dstm(node.x, node.y, child.x, child.y) <= node.width*3){
                        Lines.line(node.x + offsetX, node.y + offsetY, child.x + offsetX, child.y + offsetY);
                    }else{
                        Lines.line(node.x + offsetX, node.y + offsetY, child.x + offsetX, node.y + offsetY);
                        Lines.line(child.x + offsetX, node.y + offsetY, child.x + offsetX, child.y + offsetY);
                    }
                }
            }

            Draw.sort(false);
            Draw.reset();
            super.drawChildren();
        }
    }
}
