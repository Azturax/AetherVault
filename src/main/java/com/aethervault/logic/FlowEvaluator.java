package com.aethervault.logic;

import net.minecraft.world.item.ItemStack;
import java.util.Map;

/**
 * Service responsible for traversing a RuneProgram graph and routing items based on defined conditions.
 */
public class FlowEvaluator {
    private final Map<String, StorageNode> programNodes; // The entire graph structure

    public FlowEvaluator(RuneProgram program) {
        this.programNodes = program.getNodes();
    }

    /**
     * Initiates the evaluation process for an incoming item.
     * @param item The ItemStack to be routed through the rune program.
     */
    public void evaluate(ItemStack item) {
        // Find the designated entry point (Input Node).
        StorageNode startNode = findEntryNode(); 

        if (startNode == null) {
            System.err.println("Error: RuneProgram has no defined entry node.");
            return;
        }

        routeItem(item, startNode);
    }

    /**
     * Recursively traverses the graph to route an item.
     */
    private void routeItem(ItemStack item, StorageNode currentNode) {
        if (currentNode == null) return; // Should not happen in a well-formed program

        // 1. Check if the current node is a Filter
        if (currentNode instanceof FilterNode filterNode) {
            RuneCondition condition = filterNode.getCondition();
            
            if (condition.matches(item)) {
                // Condition met: follow the 'success' path
                StorageNode nextNode = filterNode.getNextSuccessNode();
                routeItem(item, nextNode);
            } else {
                // Condition failed: follow the 'failure/default' path
                StorageNode nextNode = filterNode.getNextFailureNode();
                routeItem(item, nextNode);
            }
        } 
        // 2. Check if the current node is an Output (Destination)
        else if (currentNode instanceof OutputNode outputNode) {
            outputNode.storeItem(item); // Final destination: store or process item
        }
        // 3. Handle Input/Start Node logic here...
    }

    /**
     * Finds the designated starting point of the program graph.
     */
    private StorageNode findEntryNode() {
        for (StorageNode node : programNodes.values()) {
            if (node instanceof InputNode inputNode) {
                return inputNode; // Assuming only one entry point per program
            }
        }
        return null;
    }

    // --- Helper classes for Filter/Output nodes to make the evaluator work ---

    /**
     * Represents a node that applies a condition and routes based on result.
     */
    public static class FilterNode extends StorageNode {
        private final RuneCondition condition;
        private final StorageNode successPath; // Where to go if condition is TRUE
        private final StorageNode failurePath; // Where to go if condition is FALSE

        public FilterNode(String nodeId, RuneCondition condition, StorageNode successPath, StorageNode failurePath) {
            super(nodeId);
            this.condition = condition;
            this.successPath = successPath;
            this.failurePath = failurePath;
        }

        @Override
        public NodeType getType() { return NodeType.FILTER; }

        public RuneCondition getCondition() { return condition; }
        public StorageNode getNextSuccessNode() { return successPath; }
        public StorageNode getNextFailureNode() { return failurePath; }
    }

    /**
     * Represents a node that acts as the final destination for an item.
     */
    public static class OutputNode extends StorageNode {
        private final IAetherStorage storageTarget; // The actual storage mechanism (Lattice, Echo, etc.)

        public OutputNode(String nodeId, IAetherStorage target) {
            super(nodeId);
            this.storageTarget = target;
        }

        @Override
        public NodeType getType() { return NodeType.OUTPUT; }

        /**
         * Stores the item in the designated storage mechanism.
         */
        public void storeItem(ItemStack item) {
            // In a real scenario, we'd generate a unique ID for retrieval later.
            storageTarget.store(item); 
            System.out.println("Item routed to output node: " + getNodeId());
        }
    }

    /**
     * Represents the starting point of the flow graph.
     */
    public static class InputNode extends StorageNode {
        // In a real mod, this would handle item ingestion from world events (e.g., dropped items).
        public InputNode(String nodeId) {
            super(nodeId);
        }

        @Override
        public NodeType getType() { return NodeType.INPUT; }
    }
}