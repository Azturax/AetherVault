package com.aethervault.logic;

import java.util.HashMap;
import java.util.Map;

/**
 * The container for a complete Rune-Program flow graph.
 */
public class RuneProgram {
    private final String programId;
    // Maps node ID to the actual StorageNode instance in the graph.
    private final Map<String, StorageNode> nodes = new HashMap<>();

    public RuneProgram(String programId) {
        this.programId = programId;
    }

    /**
     * Adds a storage node (Input, Filter, or Output) to this program's graph.
     */
    public void addNode(StorageNode node) {
        nodes.put(node.getNodeId(), node);
    }

    /**
     * Connects two nodes in the flow: source -> target.
     */
    public void connectNodes(String sourceNodeId, String targetNodeId) throws IllegalArgumentException {
        StorageNode source = nodes.get(sourceNodeId);
        StorageNode target = nodes.get(targetNodeId);

        if (source == null || target == null) {
            throw new IllegalArgumentException("One or both node IDs not found in the program.");
        }

        // Add the target as an outgoing connection from the source
        source.addOutgoingNode(target);
    }

    public Map<String, StorageNode> getNodes() {
        return nodes;
    }

    public String getProgramId() {
        return programId;
    }
}