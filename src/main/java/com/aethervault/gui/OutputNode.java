package com.aethervault.gui;

import java.awt.Color;
import java.util.List;

/**
 * Represents an Output Node in the Rune Program Graph GUI.
 */
public class OutputNode extends Node {
    private static final Color NODE_COLOR = THEME_GOLD; // Gold for output/end node

    public OutputNode(String nodeId) {
        super(nodeId);
        this.width = 120;
        this.height = 60;
    }

    @Override
    public void draw() {
        // Draw the main body of the output node (e.g., a rounded rectangle)
        System.out.println("Drawing OutputNode " + nodeId + " at (" + x + ", " + y + ") with color " + NODE_COLOR);
        // In a real GUI, this would involve drawing shapes onto a Graphics context.
    }

    @Override
    public boolean handleMouseClick(float mouseX, float mouseY) {
        return super.handleMouseClick(mouseX, mouseY);
    }
}