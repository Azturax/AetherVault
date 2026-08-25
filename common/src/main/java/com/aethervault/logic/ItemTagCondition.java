package com.aethervault.logic;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * A concrete implementation of RuneCondition that checks if an item belongs to a
 * specific item tag (e.g., {@code minecraft:tools}).
 */
public class ItemTagCondition implements RuneCondition {
    private final TagKey<Item> requiredTag;

    public ItemTagCondition(TagKey<Item> requiredTag) {
        this.requiredTag = requiredTag;
    }

    /**
     * Convenience constructor accepting a tag id string such as
     * {@code "minecraft:tools"} or {@code "aethervault:rune_components"}.
     */
    public ItemTagCondition(String requiredTagId) {
        this(TagKey.create(Registries.ITEM, ResourceLocation.parse(requiredTagId)));
    }

    @Override
    public boolean matches(ItemStack item) {
        return !item.isEmpty() && item.is(requiredTag);
    }

    @Override
    public String getDescription() {
        return "Has Tag: '" + requiredTag.location() + "'";
    }
}