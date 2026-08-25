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
    private boolean isPlacingNode = false;
    private Node newNodeToPlace = null; // Temporary storage for node being placed

    public void setPlacementMode(boolean enable) {
        this.isPlacingNode = enable;
        if (enable && !newNodeToPlace) {
            // Default to InputNode if no specific type is set, or handle selection logic here.
            // For now, we'll just enable the mode. Specific node types will be handled by external input.
        } else if (!enable) {
            this.newNodeToPlace = null; // Clear temporary placement data when exiting mode
        }
    }

    // Theme colors (Indigo/Teal/Gold) - defined in Node base class but repeated for context
    protected static final Color THEME_INDIGO = new java.awt.Color(75, 0, 130);
    protected static final Color THEME_TEAL = new java.awt.Color(0, 200, 180);
    protected static final Color THEME_GOLD = new java.awt.Color(255, 215, 0);

    public void addNode(Node node) {
        nodes.add(node);
    }

    /**
     * Handles mouse click events to select nodes, initiate drag operations, or place new nodes.
     */
    public boolean handleMouseClick(float mouseX, float mouseY) {
        // 1. Handle Node Selection/Dragging
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

        // 2. Handle Placement Mode (if not selecting a node)
        if (isPlacingNode && newNodeToPlace == null) {
            // Placeholder: In a real app, we'd determine the type of node to place here based on UI state.
            // For now, let's default to an InputNode for demonstration purposes if placement is active.
            String newId = "node_" + System.currentTimeMillis();
            newNodeToPlace = new InputNode(newId); // Assuming InputNode is the default type
            newNodeToPlace.setX(mouseX - 50); // Center node on click
            newNodeToPlace.setY(mouseY - 30);
        }

        // If we are placing a node, add it to the list and reset placement state
        if (isPlacingNode && newNodeToPlace != null) {
            nodes.add(newNodeToPlace);
            selectedNode = newNodeToPlace; // Select newly placed node
            isPlacingNode = false; // Exit placement mode after placing one
            newNodeToPlace = null;
            return true;
        }

        // 3. Clicked on empty canvas space (and not in placement mode)
        selectedNode = null;
        isDragging = false;
        connectionStartPort = null; // Clear connection state if clicking empty space
        return false;
    }
    }

    /**
     * Handles mouse movement while a node is being dragged or during connection drawing.
     */
    public void handleMouseMove(float mouseX, float mouseY) {
        if (isDragging && selectedNode != null) {
            // Calculate new position based on initial click offset and current mouse position
            selectedNode.setX(mouseX - dragOffsetX);
            selectedNode.setY(mouseY - dragOffsetY);
        } else if (connectionStartPort != null) {
            // If we are in connection drawing mode, update the visual representation of the conduit here.
            // For now, we just track the mouse position for future rendering logic.
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
        for (Edge edge : edges) {
            Node source = edge.getSourceNode();
            Node target = edge.getTargetNode();

            // In a real GUI, we would calculate the start and end points of the conduit based on port positions.
            java.awt.Point startPos = getAbsolutePosition(source.getX(), source.getY()); // Simplified: using node center for now
            java.awt.Point endPos = getAbsolutePosition(target.getX(), target.getY());

            // Simulate drawing a glowing line (mana conduit) from source to target
            System.out.println("  -> Drawing edge: " + source.getNodeId() + " -> " + target.getNodeId() + 
                               " at start (" + startPos.x + ", " + startPos.y + ") and end (" + endPos.x + ", " + endPos.y + ")");
        }
    }

    public Node getSelectedNode() {
        return selectedNode;
    }

    public List<Node> getAllNodes() {
        return nodes;
    }
}