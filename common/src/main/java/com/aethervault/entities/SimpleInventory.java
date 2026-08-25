package com.aethervault.entities;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * A basic inventory implementation for Familiars and other entities.
 *
 * <p>Slots never hold null; empty slots contain {@link ItemStack#EMPTY}.
 * {@link #addItem(ItemStack)} merges into matching stacks before filling
 * empty slots, mirroring vanilla inventory semantics.</p>
 */
public class SimpleInventory {
    private final ItemStack[] slots;

    public SimpleInventory(int size) {
        this.slots = new ItemStack[size];
        java.util.Arrays.fill(this.slots, ItemStack.EMPTY);
    }

    public int getSize() {
        return slots.length;
    }

    /**
     * Adds an item to the inventory, merging stacks where possible.
     *
     * @param stack the stack to add; the source is not modified
     * @return true if the whole stack fit, false otherwise
     */
    public boolean addItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return true;
        }
        int remaining = stack.getCount();

        // Pass 1: merge into existing compatible stacks.
        if (stack.isStackable()) {
            for (int i = 0; i < slots.length && remaining > 0; i++) {
                ItemStack slot = slots[i];
                if (!slot.isEmpty() && ItemStack.isSameItemSameComponents(slot, stack)) {
                    int space = Math.min(stack.getMaxStackSize(), slot.getMaxStackSize()) - slot.getCount();
                    if (space > 0) {
                        int moved = Math.min(space, remaining);
                        slot.grow(moved);
                        remaining -= moved;
                    }
                }
            }
        }

        // Pass 2: fill empty slots.
        for (int i = 0; i < slots.length && remaining > 0; i++) {
            if (slots[i].isEmpty()) {
                int moved = Math.min(remaining, stack.getMaxStackSize());
                ItemStack copy = stack.copyWithCount(moved);
                slots[i] = copy;
                remaining -= moved;
            }
        }

        return remaining == 0;
    }

    /**
     * Removes and returns the stack in a slot, leaving the slot empty.
     */
    public ItemStack removeItem(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < slots.length && !slots[slotIndex].isEmpty()) {
            ItemStack item = slots[slotIndex];
            slots[slotIndex] = ItemStack.EMPTY;
            return item;
        }
        return ItemStack.EMPTY;
    }

    /**
     * The stack in a slot, or {@link ItemStack#EMPTY}. Never null.
     */
    public ItemStack getStack(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < slots.length) {
            return slots[slotIndex];
        }
        return ItemStack.EMPTY;
    }

    /**
     * Number of non-empty slots.
     */
    public int getOccupiedSlots() {
        int count = 0;
        for (ItemStack stack : slots) {
            if (!stack.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Dumps every stored stack (used when the owner entity dies).
     */
    public List<ItemStack> removeAllItems() {
        List<ItemStack> items = new java.util.ArrayList<>();
        for (int i = 0; i < slots.length; i++) {
            if (!slots[i].isEmpty()) {
                items.add(slots[i]);
                slots[i] = ItemStack.EMPTY;
            }
        }
        return items;
    }

    // ------------------------------------------------------------- NBT ---

    /**
     * Serializes all occupied slots to a list tag.
     */
    public ListTag saveAll(Provider registries) {
        ListTag list = new ListTag();
        for (ItemStack stack : slots) {
            if (!stack.isEmpty()) {
                Tag tag = stack.save(registries);
                if (tag != null) {
                    list.add(tag);
                }
            }
        }
        return list;
    }

    /**
     * Deserializes stacks from a list tag into consecutive slots.
     */
    public void loadAll(Provider registries, ListTag list) {
        java.util.Arrays.fill(slots, ItemStack.EMPTY);
        int index = 0;
        for (int i = 0; i < list.size() && index < slots.length; i++) {
            ItemStack stack = ItemStack.parse(registries, list.getCompound(i))
                    .orElse(ItemStack.EMPTY);
            if (!stack.isEmpty()) {
                slots[index++] = stack;
            }
        }
    }
}