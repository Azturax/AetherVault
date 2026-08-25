package com.aethervault.gui;

import net.minecraft.nbt.CompoundTag;

/**
 * A directed edge ("mana conduit") between two graph nodes, identified by node ids.
 */
public record GraphEdge(int fromId, int toId) {

    private static final String TAG_FROM = "From";
    private static final String TAG_TO = "To";

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(TAG_FROM, fromId);
        tag.putInt(TAG_TO, toId);
        return tag;
    }

    public static GraphEdge load(CompoundTag tag) {
        return new GraphEdge(tag.getInt(TAG_FROM), tag.getInt(TAG_TO));
    }
}
