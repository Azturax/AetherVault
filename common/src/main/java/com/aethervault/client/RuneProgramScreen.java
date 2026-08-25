package com.aethervault.client;

import com.aethervault.gui.GraphEdge;
import com.aethervault.gui.GraphModel;
import com.aethervault.gui.GraphNode;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * The Rune Program Tablet workspace: a node-graph editor rendered entirely with
 * theme fills (navy panels, cyan conduits, gold active state).
 *
 * <p>Controls:</p>
 * <ul>
 *   <li>1 / 2 / 3 - arm placement of Input / Filter / Output node</li>
 *   <li>Click empty space while armed to place</li>
 *   <li>Drag nodes to move; click empty space to deselect</li>
 *   <li>C then click another node - connect ("mana conduit")</li>
 *   <li>T - cycle condition on selected filter; Delete - remove node</li>
 * </ul>
 */
public class RuneProgramScreen extends Screen {

    private static final int NODE_W = 64;
    private static final int NODE_H = 24;
    private static final int HEADER_H = 36;

    // Theme (ARGB)
    private static final int COL_BG = 0xF20D1B2A;
    private static final int COL_PANEL = 0xFF0D1B2A;
    private static final int COL_NAVY_BORDER = 0xFF2E8FA3;
    private static final int COL_CYAN = 0xFF4FE3E3;
    private static final int COL_CYAN_DIM = 0xFF1F6B6B;
    private static final int COL_GOLD = 0xFFFFD700;
    private static final int COL_TEXT = 0xFFC8E8EE;

    private GraphModel model = new GraphModel();
    private GraphNode.Kind armKind = null;
    private float dragOffsetX;
    private float dragOffsetY;
    private boolean pendingConnectClick;

    public RuneProgramScreen() {
        super(Component.translatable("title.aethervault.rune_program"));
    }

    @Override
    protected void init() {
        // Fresh workspace per open; programs persist in a future NBT-backed tablet item.
        model = new GraphModel();
        model.addNode(GraphNode.Kind.INPUT, 30, 70);
        model.addNode(GraphNode.Kind.FILTER, 150, 70);
        model.addNode(GraphNode.Kind.OUTPUT, 270, 70);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g, mouseX, mouseY, partialTick);
        g.fill(0, 0, this.width, this.height, COL_BG);

        // Header panel.
        g.fill(0, 0, this.width, HEADER_H, COL_PANEL);
        g.fill(0, HEADER_H - 1, this.width, HEADER_H, COL_NAVY_BORDER);
        g.drawCenteredString(this.font, this.title, this.width / 2, 8, COL_GOLD);
        g.drawCenteredString(this.font,
                Component.translatable("hint.aethervault.rune_program").withStyle(ChatFormatting.DARK_GRAY),
                this.width / 2, 22, COL_TEXT);

        // Edges behind nodes.
        for (GraphEdge edge : model.getEdges()) {
            drawEdge(g, edge);
        }

        // Pending connect rubber band.
        GraphNode from = lastClickedForConnect();
        if (from != null) {
            g.fill((int) (from.getX() + NODE_W), (int) (from.getY() + NODE_H / 2),
                    Math.max(from.intX() + NODE_W, mouseX), (int) (from.getY() + NODE_H / 2) + 2, COL_CYAN_DIM);
        }

        // Nodes.
        for (GraphNode node : model.getNodes()) {
            drawNode(g, node, node == model.getSelected());
        }
    }

    private GraphNode lastClickedForConnect() {
        return pendingConnectClick ? model.getSelected() : null;
    }

    private void drawNode(GuiGraphics g, GraphNode node, boolean isSelected) {
        int x = (int) node.getX();
        int y = (int) node.getY();

        // Body + border; gold frame when active or selected.
        g.fill(x - 1, y - 1, x + NODE_W + 1, y + NODE_H + 1,
                node.isActive() || isSelected ? COL_GOLD : COL_NAVY_BORDER);
        g.fill(x, y, x + NODE_W, y + NODE_H, COL_PANEL);

        // Kind accent stripe + label.
        int accent = switch (node.getKind()) {
            case INPUT -> COL_CYAN;
            case FILTER -> 0xFFE8A33D;
            case OUTPUT -> COL_GOLD;
        };
        g.fill(x, y, x + 4, y + NODE_H, accent);
        String label = node.getKind().name().charAt(0)
                + node.getKind().name().substring(1).toLowerCase(java.util.Locale.ROOT);
        g.drawString(this.font, label + " #" + node.getId(), x + 8, y + 4, COL_TEXT);

        // Filter condition line.
        if (node.getKind() == GraphNode.Kind.FILTER) {
            String cond = node.getCondition() == GraphNode.Condition.NONE
                    ? "-" : node.getCondition().getLabel();
            g.drawString(this.font, cond, x + 8, y + 14, COL_CYAN);
        }
    }

    private void drawEdge(GuiGraphics g, GraphEdge edge) {
        GraphNode from = model.getNodeById(edge.fromId());
        GraphNode to = model.getNodeById(edge.toId());
        if (from == null || to == null) {
            return;
        }
        int y = (int) (from.getY() + NODE_H / 2);
        int x1 = (int) (from.getX() + NODE_W);
        int x2 = (int) to.getX();
        int left = Math.min(x1, x2);
        int right = Math.max(x1, x2);
        // Glowing conduit: bright core over dim halo.
        g.fill(left, y - 2, right, y + 3, COL_CYAN_DIM);
        g.fill(left, y - 1, right, y + 1, COL_CYAN);
    }

    // ---------------------------------------------------------------- input ---

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float mx = (float) mouseX;
        float my = (float) mouseY;

        // Topmost node hit-test.
        GraphNode hit = null;
        for (int i = model.getNodes().size() - 1; i >= 0; i--) {
            GraphNode n = model.getNodes().get(i);
            if (n.contains(mx, my, NODE_W, NODE_H)) {
                hit = n;
                break;
            }
        }

        if (pendingConnectClick && hit != null) {
            boolean created = model.completeConnect(hit);
            pendingConnectClick = false;
            return created;
        }
        pendingConnectClick = false;

        if (hit != null) {
            model.select(hit);
            model.setDragging(true);
            dragOffsetX = mx - hit.getX();
            dragOffsetY = my - hit.getY();
            return true;
        }

        if (armKind != null) {
            GraphNode placed = model.addNode(armKind, mx - NODE_W / 2f, my - NODE_H / 2f);
            armKind = null;
            model.select(placed);
            return true;
        }

        model.select(null);
        model.cancelConnect();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        GraphNode selected = model.getSelected();
        if (model.isDragging() && selected != null) {
            selected.setPosition((float) mouseX - dragOffsetX, (float) mouseY - dragOffsetY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        model.setDragging(false);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        switch (keyCode) {
            case org.lwjgl.glfw.GLFW.GLFW_KEY_1 -> armKind = GraphNode.Kind.INPUT;
            case org.lwjgl.glfw.GLFW.GLFW_KEY_2 -> armKind = GraphNode.Kind.FILTER;
            case org.lwjgl.glfw.GLFW.GLFW_KEY_3 -> armKind = GraphNode.Kind.OUTPUT;
            case org.lwjgl.glfw.GLFW.GLFW_KEY_C -> {
                if (model.getSelected() != null) {
                    model.startConnect(model.getSelected());
                    pendingConnectClick = true;
                }
            }
            case org.lwjgl.glfw.GLFW.GLFW_KEY_T -> {
                GraphNode sel = model.getSelected();
                if (sel != null && sel.getKind() == GraphNode.Kind.FILTER) {
                    GraphNode.Condition[] all = GraphNode.Condition.values();
                    sel.setCondition(all[(sel.getCondition().ordinal() + 1) % all.length]);
                }
            }
            case org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE, org.lwjgl.glfw.GLFW.GLFW_KEY_X ->
                    model.deleteSelected();
            default -> {
                return super.keyPressed(keyCode, scanCode, modifiers);
            }
        }
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

