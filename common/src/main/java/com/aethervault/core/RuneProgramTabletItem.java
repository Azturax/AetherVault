package com.aethervault.core;

import java.util.function.BiConsumer;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * The Rune Program Tablet: opens the node-based rune programming workspace.
 *
 * <p>The actual screen opening is loader-specific (menus/screens differ between
 * platforms), so the platform modules inject an opener via
 * {@link #setScreenOpener(BiConsumer)} during registration.</p>
 */
public class RuneProgramTabletItem extends Item {

    private static BiConsumer<Player, ItemStack> screenOpener =
            (player, stack) -> System.out.println("Rune Program Tablet: GUI not yet wired on this platform.");

    public RuneProgramTabletItem(Properties properties) {
        super(properties);
    }

    /**
     * Injects the platform-specific screen opener. Called once during mod construction.
     */
    public static void setScreenOpener(BiConsumer<Player, ItemStack> opener) {
        screenOpener = opener;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            screenOpener.accept(player, stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}