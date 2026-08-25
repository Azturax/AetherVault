package com.aethervault.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

/**
 * Editable model of a rune program graph: nodes, directed edges between them,
 * and editor interaction state (selection, drag).
 */
public class GraphModel {

    private final List<GraphNode> nodes = new ArrayList<>();
    private final List<GraphEdge> edges = new ArrayList<>();
    private int nextId = 1;

    private GraphNode selected;
    private GraphNode connectFrom;
    private boolean dragging;

    // ----------------------------------------------------------- node CRUD ---

    public GraphNode addNode(GraphNode.Kind kind, float x, float y) {
        GraphNode node = new GraphNode(nextId++, kind, x, y);
        nodes.add(node);
        return node;
    }

    public void removeNode(GraphNode node) {
        nodes.remove(node);
        edges.removeIf(e -> e.fromId() == node.getId() || e.toId() == node.getId());
        if (selected == node) {
            selected = null;
        }
        if (connectFrom == node) {
            connectFrom = null;
        }
    }

    public List<GraphNode> getNodes() {
        return nodes;
    }

    public List<GraphEdge> getEdges() {
        return edges;
    }

    public GraphNode getNodeById(int id) {
        for (GraphNode n : nodes) {
            if (n.getId() == id) {
                return n;
            }
        }
        return null;
    }

    public int countKind(GraphNode.Kind kind) {
        int c = 0;
        for (GraphNode n : nodes) {
            if (n.getKind() == kind) {
                c++;
            }
        }
        return c;
    }

    // ---------------------------------------------------------- connections ---

    /**
     * Begins a pending connection from the given node.
     */
    public void startConnect(GraphNode from) {
        this.connectFrom = from;
    }

    public boolean isConnecting() {
        return connectFrom != null;
    }

    /**
     * Completes a pending connection to the given node. A connection from an
     * OUTPUT node or into the same node is rejected. Returns true when created.
     */
    public boolean completeConnect(GraphNode to) {
        if (connectFrom == null || to == null || to == connectFrom
                || connectFrom.getKind() == GraphNode.Kind.OUTPUT
                || to.getKind() == GraphNode.Kind.INPUT) {
            connectFrom = null;
            return false;
        }
        GraphEdge edge = new GraphEdge(connectFrom.getId(), to.getId());
        boolean added = !edges.contains(edge);
        if (added) {
            edges.add(edge);
        }
        connectFrom = null;
        return added;
    }

    public void cancelConnect() {
        connectFrom = null;
    }

    // ------------------------------------------------------- selection/drag ---

    public GraphNode getSelected() {
        return selected;
    }

    public void select(GraphNode node) {
        this.selected = node;
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public void deleteSelected() {
        if (selected != null) {
            removeNode(selected);
        }
    }

    /**
     * Removes the outgoing edge(s) of the selected filter's first connection
     * (used by the "disconnect" editor action).
     */
    public void disconnectSelected() {
        if (selected != null) {
            edges.removeIf(e -> e.fromId() == selected.getId());
        }
    }

    // ------------------------------------------------------------- NBT ---

    private static final String TAG_NODES = "Nodes";
    private static final String TAG_EDGES = "Edges";
    private static final String TAG_NEXT_ID = "NextId";

    public CompoundTag save() {
        CompoundTag root = new CompoundTag();
        root.putInt(TAG_NEXT_ID, nextId);
        ListTag nodeList = new ListTag();
        for (GraphNode node : nodes) {
            nodeList.add(node.save());
        }
        root.put(TAG_NODES, nodeList);
        ListTag edgeList = new ListTag();
        for (GraphEdge edge : edges) {
            edgeList.add(edge.save());
        }
        root.put(TAG_EDGES, edgeList);
        return root;
    }

    public static GraphModel load(CompoundTag root) {
        GraphModel model = new GraphModel();
        model.nextId = Math.max(1, root.getInt(TAG_NEXT_ID));
        ListTag nodeList = root.getList(TAG_NODES, Tag.TAG_COMPOUND);
        for (int i = 0; i < nodeList.size(); i++) {
            model.nodes.add(GraphNode.load(nodeList.getCompound(i)));
        }
        ListTag edgeList = root.getList(TAG_EDGES, Tag.TAG_COMPOUND);
        for (int i = 0; i < edgeList.size(); i++) {
            model.edges.add(GraphEdge.load(edgeList.getCompound(i)));
        }
        return model;
    }
}
