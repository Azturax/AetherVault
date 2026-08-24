package com.aethervault.entities;

import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

/**
 * A basic inventory implementation for Familiars and other entities.
 */
public class SimpleInventory {
    private final ItemStack[] slots;
    private static final int DEFAULT_SIZE = 32;

    public SimpleInventory(int size) {
        this.slots = new ItemStack[size];
    }

    /**
     * Adds an item to the inventory if space is available.
     */
    public boolean addItem(ItemStack stack) {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == null || !stack.isEmpty()) { // Simplified check for empty slot
                // In a real mod, we'd handle stacking and item merging here.
                slots[i] = stack;
                return true;
            }
        }
        return false; // Inventory is full
    }

    /**
     * Removes an item from the inventory at a specific slot index.
     */
    public ItemStack removeItem(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < slots.length) {
            ItemStack item = slots[slotIndex];
            slots[slotIndex] = null; // Clear the slot
            return item;
        }
        return ItemStack.EMPTY;
    }

    public int getSlotCount() {
        int count = 0;
        for (ItemStack stack : slots) {
            if (stack != null && !stack.isEmpty()) {
                count++;
            }
        }
        return count;
    }
}