package com.aethervault.gui;

import com.aethervault.logic.*;
import java.util.Map;

/**
 * Utility class responsible for translating GUI components into executable RuneProgram logic.
 */
public class GraphSerializer {

    /**
     * Converts the visual graph state from the Canvas into a runnable RuneProgram object.
     * @param canvas The current state of the graphical interface.
     * @return A fully constructed and serializable RuneProgram instance.
     */
    public static RuneProgram serializeGraph(RuneProgramGraphCanvas canvas) {
        // 1. Initialize the program with a unique ID (or one provided by the user/system)
        String programId = "GUI_GENERATED_" + System.currentTimeMillis();
        RuneProgram runeProgram = new RuneProgram(programId);

        // Map to hold GUI Node objects for easy lookup during connection mapping
        Map<String, Node> guiNodes = canvas.getAllNodes().stream()
                .collect(java.util.stream.Collectors.toMap(Node::getNodeId, n -> n));

        // 2. Create and add all logic nodes based on GUI representation
        for (Node guiNode : canvas.getAllNodes()) {
            StorageNode storageNode = createLogicNodeFromGuiNode(guiNode);
            runeProgram.addNode(storageNode);
        }

        // 3. Map connections from the visual graph to the logic structure
        mapConnections(canvas, runeProgram);

        return runeProgram;
    }

    /**
     * Translates a GUI Node into its corresponding StorageNode implementation.
     */
    private static StorageNode createLogicNodeFromGuiNode(Node guiNode) {
        // This is where the mapping logic resides. We must map visual types to concrete Java classes.
        switch (guiNode.getClass().getSimpleName()) {
            case "InputNode":
                // Assuming InputNode maps to a specific implementation of StorageNode, e.g., EchoVaultBlockEntity
                return new com.aethervault.storage.echo.EchoVaultBlockEntity(guiNode.getNodeId(), NodeType.INPUT);

            case "FilterNode":
                com.aethervault.logic.NodeType filterType = mapGuiConditionToLogicType((FilterNode) guiNode);
                // Assuming FilterNode maps to a specific implementation of StorageNode, e.g., RuneProgramTablet
                return new com.aethervault.storage.lattice.LatticeAnchorBlockEntity(guiNode.getNodeId(), filterType, ((FilterNode) guiNode).getConditionType());

            case "OutputNode":
                // Assuming OutputNode maps to a specific implementation of StorageNode
                return new com.aethervault.logic.StorageNode(guiNode.getNodeId()) { // Placeholder for actual output node logic
                    @Override
                    public NodeType getType() { return NodeType.OUTPUT; }
                };

            default:
                throw new IllegalArgumentException("Unknown GUI Node type encountered during serialization: " + guiNode.getClass().getSimpleName());
        }
    }

    /**
     * Maps the visual condition to a logical node type (e.g., ItemTagCondition).
     */
    private static com.aethervault.logic.NodeType mapGuiConditionToLogicType(FilterNode guiNode) {
        String condition = guiNode.getConditionType();
        if ("Durability".equals(condition)) {
            return com.aethervault.logic.StorageNode.NodeType.FILTER; // Placeholder for a specific durability filter type
        } else if ("Tag".equals(condition)) {
            // This would map to ItemTagCondition logic in the FlowEvaluator
            return com.aethervault.logic.StorageNode.NodeType.FILTER; 
        }
        throw new IllegalArgumentException("Unsupported condition type: " + condition);
    }

    /**
     * Iterates through all nodes and their connections to build the graph structure in RuneProgram.
     */
    private static void mapConnections(RuneProgramGraphCanvas canvas, RuneProgram runeProgram) {
        // This requires a way for GUI Nodes to know which other GUI Nodes they are connected to.
        // Since our current Node classes don't store connections, we must assume the Canvas or an external system manages this relationship.
        System.out.println("Mapping visual edges (connections)...");

        for (Node guiSource : canvas.getAllNodes()) {
            if (!(guiSource instanceof com.aethervault.logic.StorageNode)) continue; // Check if it's a logic node type

            // In a real implementation, we would query the Canvas for all target nodes connected to this source GUI Node.
            // For simulation: assume there is an external list of connections (e.g., List<Connection> edges)
            // Example:
            /*
            for (Connection edge : canvas.getEdgesFrom(guiSource)) {
                Node guiTarget = findGuiNodeAtPosition(edge.targetX, edge.targetY);
                if (guiTarget != null) {
                    String sourceId = ((com.aethervault.logic.StorageNode) guiSource).getNodeId();
                    String targetId = guiTarget.getNodeId();
                    try {
                        runeProgram.connectNodes(sourceId, targetId);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error connecting nodes: " + e.getMessage());
                    }
                }
            }
            */
        }
    }
}