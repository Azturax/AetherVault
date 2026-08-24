package com.aethervault.gui;

import java.util.ArrayList;
import java.util.List;

/**
 * The main canvas for visualizing and editing the Rune Program Graph.
 */
public class RuneProgramGraphCanvas {
    private final List<Node> nodes = new ArrayList<>();
    private Node selectedNode = null;
    private boolean isDragging = false;
    private FilterParameterUI parameterUi;

    // Theme colors (Indigo/Teal/Gold) - defined in Node base class but repeated for context
    protected static final Color THEME_INDIGO = new java.awt.Color(75, 0, 130);
    protected static final Color THEME_TEAL = new java.awt.Color(0, 200, 180);
    protected static final Color THEME_GOLD = new java.awt.Color(255, 215, 0);

    public void addNode(Node node) {
        nodes.add(node);
    }

    /**
     * Handles mouse click events to select nodes or initiate drag operations.
     */
    public boolean handleMouseClick(float mouseX, float mouseY) {
        // Check if any node was clicked (iterate backwards so top-most/last added is selected first)
        for (int i = nodes.size() - 1; i >= 0; i--) {
            Node node = nodes.get(i);
            if (node.handleMouseClick(mouseX, mouseY)) {
                selectedNode = node;
                isDragging = true;
                dragOffsetX = mouseX - node.getX();
                dragOffsetY = mouseY - node.getY();
                return true; // Node was clicked and selected/started dragging
            }
        }

        // Clicked on empty canvas space
        selectedNode = null;
        isDragging = false;
        return false;
    }

    /**
     * Handles mouse movement while a node is being dragged.
     */
    public void handleMouseMove(float mouseX, float mouseY) {
        if (isDragging && selectedNode != null) {
            // Calculate new position based on initial click offset and current mouse position
            selectedNode.setX(mouseX - dragOffsetX);
            selectedNode.setY(mouseY - dragOffsetY);
        }
    }

    /**
     * Handles mouse release to end dragging or initiate connection drawing (future feature).
     */
    public void handleMouseRelease() {
        isDragging = false;
    }

    /**
     * Draws all nodes and the connecting edges on the screen.
     */
    public void drawGraph() {
        // 1. Draw Edges (Mana Conduits) first, so they appear behind nodes
        drawEdges();

        // 2. Draw Nodes
        for (Node node : nodes) {
            node.draw();
        }
    }

    /**
     * Draws the "glowing mana conduits" between connected nodes.
     */
    private void drawEdges() {
        System.out.println("Drawing all edges...");
        // In a real GUI, this would iterate through all nodes and their outgoing connections
        for (Node source : nodes) {
            // We need access to the underlying StorageNode logic here to get actual connections.
            // For now, we simulate drawing an edge if it's connected to something.
            if (source instanceof com.aethervault.logic.StorageNode) {
                com.aethervault.logic.StorageNode storageSource = (com.aethervault.logic.StorageNode) source;

                for (com.aethervault.logic.StorageNode target : storageSource.getOutgoingNodes()) {
                    // Simulate drawing a glowing line from source to target
                    System.out.println("  -> Drawing edge: " + source.getNodeId() + " -> " + target.getNodeId());
                }
            }
        }
    }

    public Node getSelectedNode() {
        return selectedNode;
    }

    public List<Node> getAllNodes() {
        return nodes;
    }
}