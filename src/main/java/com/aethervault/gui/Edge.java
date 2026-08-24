package com.aethervault.gui;

/**
 * Represents a connection (mana conduit) between two nodes in the graph.
 */
public class Edge {
    private final Node sourceNode;
    private final Port sourcePort;
    private final Node targetNode;
    private final Port targetPort;

    public Edge(Node sourceNode, Port sourcePort, Node targetNode, Port targetPort) {
        this.sourceNode = sourceNode;
        this.sourcePort = sourcePort;
        this.targetNode = targetNode;
        this.targetPort = targetPort;
    }

    public Node getSourceNode() { return sourceNode; }
    public Port getSourcePort() { return sourcePort; }
    public Node getTargetNode() { return targetNode; }
    public Port getTargetPort() { return targetPort; }

    // In a real implementation, this method would calculate the start and end coordinates for rendering.
}