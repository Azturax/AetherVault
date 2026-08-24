package com.aethervault.gui;

import java.awt.Color;
import java.util.List;

/**
 * Represents an Input Node in the Rune Program Graph GUI.
 */
public class InputNode extends Node {
    private static final Color NODE_COLOR = THEME_INDIGO; // Indigo for input/start node

    public InputNode(String nodeId) {
        super(nodeId);
        this.width = 120;
        this.height = 60;
    }

    @Override
    public void draw() {
        // Draw the main body of the input node (e.g., a rounded rectangle)
        System.out.println("Drawing InputNode " + nodeId + " at (" + x + ", " + y + ") with color " + NODE_COLOR);
        // In a real GUI, this would involve drawing shapes onto a Graphics context.
    }

    @Override
    public boolean handleMouseClick(float mouseX, float mouseY) {
        return super.handleMouseClick(mouseX, mouseY);
    }
}