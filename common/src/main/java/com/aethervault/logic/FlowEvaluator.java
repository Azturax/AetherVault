package com.aethervault.logic;

import com.aethervault.core.IAetherStorage;

import net.minecraft.world.item.ItemStack;

import java.util.Map;

/**
 * Service responsible for traversing a RuneProgram graph and routing items based on defined conditions.
 *
 * <p>Routing semantics for {@code FilterNode}: the first outgoing connection is the
 * <em>success</em> path, the second outgoing connection is the <em>failure</em> path.
 * This matches the order in which {@link RuneProgram#connectNodes(String, String)}
 * calls are made when building the graph.</p>
 */
public class FlowEvaluator {
    private final Map<String, StorageNode> programNodes; // The entire graph structure

    /**
     * Creates an evaluator over an empty program. Useful as a default; routing
     * will simply report a missing entry node until a program is assigned.
     */
    public FlowEvaluator() {
        this(new RuneProgram("default"));
    }

    public FlowEvaluator(RuneProgram program) {
        this.programNodes = program.getNodes();
    }

    /**
     * Convenience entry point used by event hooks for ad-hoc item evaluation.
     */
    public void evaluateItem(ItemStack item) {
        System.out.println("FlowEvaluator: starting evaluation for item.");
        evaluate(item);
    }

    /**
     * Initiates the evaluation process for an incoming item.
     *
     * @param item the ItemStack to be routed through the rune program
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
        if (currentNode == null) {
            // Reached a dead end (e.g., unconnected failure path): item falls through.
            System.out.println("FlowEvaluator: item fell through at a dead-end node.");
            return;
        }

        // 1. Filter nodes route along success/failure connections.
        if (currentNode instanceof FilterNode filterNode) {
            RuneCondition condition = filterNode.getCondition();

            if (condition.matches(item)) {
                routeItem(item, filterNode.getSuccessPath());
            } else {
                routeItem(item, filterNode.getFailurePath());
            }
        }
        // 2. Output nodes are final destinations.
        else if (currentNode instanceof OutputNode outputNode) {
            outputNode.storeItem(item);
        }
        // 3. Input nodes simply pass the item onward.
        else if (currentNode instanceof InputNode) {
            StorageNode next = currentNode.getOutgoingNodes().isEmpty()
                    ? null
                    : currentNode.getOutgoingNodes().get(0);
            routeItem(item, next);
        }
    }

    /**
     * Finds the designated starting point of the program graph.
     */
    private StorageNode findEntryNode() {
        for (StorageNode node : programNodes.values()) {
            if (node instanceof InputNode) {
                return node; // Assuming only one entry point per program
            }
        }
        return null;
    }

    // --- Node implementations for the evaluator ---

    /**
     * A node that applies a condition and routes based on the result.
     * First outgoing connection = success path; second = failure path.
     */
    public static class FilterNode extends StorageNode {
        private final RuneCondition condition;

        public FilterNode(String nodeId, RuneCondition condition) {
            super(nodeId);
            this.condition = condition;
        }

        @Override
        public NodeType getType() {
            return NodeType.FILTER;
        }

        public RuneCondition getCondition() {
            return condition;
        }

        public StorageNode getSuccessPath() {
            return getOutgoingNodes().size() > 0 ? getOutgoingNodes().get(0) : null;
        }

        public StorageNode getFailurePath() {
            return getOutgoingNodes().size() > 1 ? getOutgoingNodes().get(1) : null;
        }
    }

    /**
     * A node that acts as the final destination for an item.
     */
    public static class OutputNode extends StorageNode {
        private final IAetherStorage storageTarget; // The actual storage mechanism (Lattice, Echo, etc.)

        public OutputNode(String nodeId, IAetherStorage target) {
            super(nodeId);
            this.storageTarget = target;
        }

        @Override
        public NodeType getType() {
            return NodeType.OUTPUT;
        }

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
     * The starting point of the flow graph. Handles item ingestion from world
     * events (e.g., dropped items) in a full implementation.
     */
    public static class InputNode extends StorageNode {
        public InputNode(String nodeId) {
            super(nodeId);
        }

        @Override
        public NodeType getType() {
            return NodeType.INPUT;
        }
    }
}