package com.aethervault.gui;

import java.awt.Color;
import java.util.List;

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

        // Checks if a given canvas coordinate falls within this port's bounding box (simplified)
        public boolean contains(float mouseX, float mouseY) {
            return mouseX >= xOffset - 5 && mouseX <= xOffset + 5 && mouseY >= yOffset - 5 && mouseY <= yOffset + 5;
        }
    }

    protected List<Port> inputPorts = new ArrayList<>();
    protected List<Port> outputPorts = new ArrayList<>();
    public Node(String nodeId) {
        this.nodeId = nodeId;
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