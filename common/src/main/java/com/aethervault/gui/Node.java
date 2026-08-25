package com.aethervault.gui;

import java.awt.Color;
import java.util.ArrayList;

/**
 * Base abstract class for all nodes in the Rune Program Graph GUI.
 */
public abstract class Node {
    protected String nodeId;
    protected float x, y; // Position on the canvas
    protected int width = 100;
    protected int height = 50;

    protected enum PortType { INPUT, OUTPUT }

    protected static class Port {
        private final String id;
        private final float xOffset; // Relative to node's top-left corner
        private final float yOffset; // Relative to node's top-left corner
        private final PortType type;

        public Port(String id, float xOffset, float yOffset, PortType type) {
            this.id = id;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.type = type;
        }

        public String getId() { return id; }
        public float getXOffset() { return xOffset; }
        public float getYOffset() { return yOffset; }
        public PortType getType() { return type; }

            /**
     * Calculates the absolute screen coordinates of a port relative to the canvas origin.
     */
    public java.awt.Point getAbsolutePosition(float nodeX, float nodeY) {
        return new java.awt.Point((int)(nodeX + xOffset), (int)(nodeY + yOffset));
    }
    }

    protected List<Port> inputPorts = new ArrayList<>();
    protected List<Port> outputPorts = new ArrayList<>();
    protected Node parent; // Reference to the owning node

    public Node(String nodeId) {
        this.nodeId = nodeId;
        this.parent = null;
    }

    // ... existing methods ...

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public String getNodeId() {
        return nodeId;
    }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    /**
     * Abstract method to draw the node on the screen.
     */
    public abstract void draw();

    /**
     * Handles mouse click events for interaction (e.g., selection, dragging start).
     * @return true if the node was clicked and should be selected/dragged.
     */
    public boolean handleMouseClick(float mouseX, float mouseY) {
        // Simple bounding box check
        return mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height);
    }

    /**
     * Handles dragging logic.
     */
    public void handleDrag(float deltaX, float deltaY) {
        this.x += deltaX;
        this.y += deltaY;
    }
}