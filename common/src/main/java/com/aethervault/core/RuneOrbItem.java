package com.aethervault.core;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

/**
 * The Rune Orb item: a portable vessel that can hold a compiled rune program.
 *
 * <p>Each orb instance is tracked by a unique id (assigned on first use) so that
 * programs stored inside orbs can be referenced by Echo Vault echoes and lattice
 * cells.</p>
 */
public class RuneOrbItem extends Item {

    public RuneOrbItem(Properties properties) {
        super(properties);
    }

    /**
     * Derives the tracking id for an orb stack. Stable per stack: orbs with the
     * same count/components share program storage semantics.
     */
    public static UUID orbIdFor(ItemStack stack) {
        return UUID.nameUUIDFromBytes(("orb|" + stack.getItem() + "|" + stack.getCount()).getBytes());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        // Runes shimmer with arcane energy.
        return true;
    }
}