package com.aethervault.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single point in the RuneGraph flow. It can be an input, a filter (condition), or an output node.
 */
public abstract class StorageNode {
    private final String nodeId;
    protected final List<StorageNode> outgoingNodes = new ArrayList<>();

    public StorageNode(String nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * Adds a connection from this node to another node in the graph.
     */
    public void addOutgoingNode(StorageNode node) {
        outgoingNodes.add(node);
    }

    public String getNodeId() {
        return nodeId;
    }

    public List<StorageNode> getOutgoingNodes() {
        return outgoingNodes;
    }

    /**
     * Abstract method to determine the type of node (Filter, Input, Output).
     */
    public abstract NodeType getType();

    public enum NodeType {
        INPUT, FILTER, OUTPUT
    }
}