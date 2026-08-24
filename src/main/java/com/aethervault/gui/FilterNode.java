package com.aethervault.gui;

import java.awt.Color;
import java.util.List;

/**
 * Represents a Filter Node in the Rune Program Graph GUI, capable of applying conditions.
 */
public class FilterNode extends Node {
    private String conditionType; // e.g., "Durability", "Tag"
    private String parameterValue; // The value associated with the condition (e.g., tag string)

    // Theme colors
    protected static final Color NODE_COLOR = THEME_TEAL; // Teal for filter/processing node

    public FilterNode(String nodeId, String initialConditionType, String initialParameterValue) {
        super(nodeId);
        this.conditionType = initialConditionType;
        this.parameterValue = initialParameterValue;
        this.width = 150; // Wider to accommodate condition text
    }

    public String getConditionType() { return conditionType; }
    public void setConditionType(String conditionType) { this.conditionType = conditionType; }
    public String getParameterValue() { return parameterValue; }
    public void setParameterValue(String parameterValue) { this.parameterValue = parameterValue; }

    @Override
    public void draw() {
        // Draw the main body of the filter node (e.g., a rounded rectangle)
        System.out.println("Drawing FilterNode " + nodeId + " at (" + x + ", " + y + ") with color " + NODE_COLOR);
        // In a real GUI, this would involve drawing shapes onto a Graphics context.

        // Display condition type and parameter value on the node itself (simplified)
        String display = String.format("%s: %s", conditionType, parameterValue);
        System.out.println("  -> Condition: " + display);
    }

    @Override
    public boolean handleMouseClick(float mouseX, float mouseY) {
        // Check if clicked and trigger selection/parameter panel logic (handled by the main Canvas class)
        return super.handleMouseClick(mouseX, mouseY);
    }
}