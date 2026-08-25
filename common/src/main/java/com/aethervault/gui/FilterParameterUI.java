package com.aethervault.gui;

import java.util.function.Consumer;

/**
 * Manages the parameter UI (modal or side panel) for a selected FilterNode.
 */
public class FilterParameterUI {
    private final FilterNode filterNode;
    private boolean isVisible = false;
    private String currentConditionType;
    private String currentValue;
    private Consumer<String> onParameterChangeCallback;

    public FilterParameterUI(FilterNode filterNode) {
        this.filterNode = filterNode;
        this.currentConditionType = filterNode.getConditionType();
        this.currentValue = filterNode.getParameterValue();
    }

    /**
     * Displays the parameter UI panel for editing node properties.
     */
    public void show() {
        isVisible = true;
        System.out.println("--- Filter Parameter UI Opened ---");
        displayParameters();
    }

    /**
     * Hides the parameter UI panel.
     */
    public void hide() {
        isVisible = false;
        System.out.println("--- Filter Parameter UI Closed ---");
    }

    private void displayParameters() {
        // In a real GUI, this would render input fields and buttons.
        System.out.println("\n[FilterNode Parameters]");
        System.out.println("Condition Type: " + currentConditionType);
        System.out.println("Parameter Value: " + currentValue);

        // Simulate user interaction to change parameters (e.g., via a button click)
        if (currentConditionType.equals("Tag")) {
            System.out.print("\nEnter new Tag string (or press Enter to keep): ");
            String input = readUserInput(); // Simulated input reading
            if (!input.isEmpty()) {
                updateParameter(input);
            }
        } else if (currentConditionType.equals("Durability")) {
             System.out.print("\nEnter new Durability threshold: ");
             String input = readUserInput(); // Simulated input reading
             if (!input.isEmpty()) {
                 updateParameter(input);
             }
        }

        // Simulate saving the changes
        saveChanges();
    }

    /**
     * Updates the internal state and notifies listeners of parameter changes.
     */
    public void updateParameter(String newValue) {
        if (newValue == null || newValue.trim().isEmpty()) return;

        System.out.println("Updating parameter from '" + currentValue + "' to '" + newValue + "'...");
        this.currentValue = newValue;
        // Update the node's state immediately
        filterNode.setParameterValue(newValue);
    }

    private void saveChanges() {
        if (onParameterChangeCallback != null) {
            String updatedState = "Updated parameters for Node " + filterNode.getNodeId();
            onParameterChangeCallback.accept(updatedState);
        }
        hide();
    }

    /**
     * Sets a callback to execute when the user confirms parameter changes.
     */
    public void setOnParameterChangeCallback(Consumer<String> callback) {
        this.onParameterChangeCallback = callback;
    }

    // --- Simulation Helpers (Replace with actual GUI event handling in a real framework) ---
    private String readUserInput() {
        // Since we are not running a full GUI, this simulates reading input from the console/user interaction layer.
        return "NewValue"; // Placeholder for simulation
    }

    public boolean isVisible() { return isVisible; }
}