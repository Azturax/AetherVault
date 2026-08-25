package com.aethervault.gui;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

/**
 * A single node in the rune program graph editor.
 *
 * <p>Pure data class - rendering is handled by the platform screen; routing
 * semantics live in {@code com.aethervault.logic}.</p>
 */
public class GraphNode {

    public enum Kind {
        INPUT, FILTER, OUTPUT
    }

    /** Known filter condition descriptors selectable in the editor. */
    public enum Condition {
        NONE("None"),
        ITEM_TAG("Item Tag"),
        DURABILITY_BELOW("Durability Below %");

        private final String label;

        Condition(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private final int id;
    private Kind kind;
    private float x;
    private float y;
    private Condition condition = Condition.NONE;
    private String parameter = "";
    private boolean active;

    public GraphNode(int id, Kind kind, float x, float y) {
        this.id = id;
        this.kind = kind;
        this.x = x;
        this.y = y;
    }

    // ----------------------------------------------------------- accessors ---

    public int getId() {
        return id;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void moveBy(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter == null ? "" : parameter;
    }

    /**
     * Active nodes glow gold in the editor and are highlighted during evaluation.
     */
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * True when the given canvas-space point is inside this node's body.
     */
    public boolean contains(float px, float py, float width, float height) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }

    // ------------------------------------------------------------- NBT ---

    private static final String TAG_ID = "Id";
    private static final String TAG_KIND = "Kind";
    private static final String TAG_X = "X";
    private static final String TAG_Y = "Y";
    private static final String TAG_CONDITION = "Condition";
    private static final String TAG_PARAMETER = "Parameter";

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(TAG_ID, id);
        tag.putString(TAG_KIND, kind.name());
        tag.putFloat(TAG_X, x);
        tag.putFloat(TAG_Y, y);
        tag.putString(TAG_CONDITION, condition.name());
        tag.putString(TAG_PARAMETER, parameter);
        return tag;
    }

    public static GraphNode load(CompoundTag tag) {
        Kind kind;
        try {
            kind = Kind.valueOf(tag.getString(TAG_KIND));
        } catch (IllegalArgumentException ex) {
            kind = Kind.FILTER;
        }
        GraphNode node = new GraphNode(tag.getInt(TAG_ID), kind,
                tag.getFloat(TAG_X), tag.getFloat(TAG_Y));
        try {
            node.condition = Condition.valueOf(tag.getString(TAG_CONDITION));
        } catch (IllegalArgumentException ignored) {
            node.condition = Condition.NONE;
        }
        if (tag.contains(TAG_PARAMETER, Tag.TAG_STRING)) {
            node.parameter = tag.getString(TAG_PARAMETER);
        }
        return node;
    }
}
