package com.aethervault.block;

import java.util.function.Supplier;

import com.aethervault.storage.echo.EchoVaultBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * The Echo Vault block: a bronze-banded temporal storage vessel. Its block entity
 * records stored items as decaying temporal echoes.
 */
public class EchoVaultBlock extends Block implements EntityBlock {

    private final Supplier<BlockEntityType<EchoVaultBlockEntity>> typeSupplier;

    public EchoVaultBlock(Supplier<BlockEntityType<EchoVaultBlockEntity>> typeSupplier, Properties properties) {
        super(properties);
        this.typeSupplier = typeSupplier;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EchoVaultBlockEntity(typeSupplier.get(), pos);
    }

    /**
     * Right-click with an item records it as a temporal echo; sneak-right-click
     * materializes the newest stored echo into the player's hand.
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof EchoVaultBlockEntity vault) {
            ItemStack held = player.getMainHandItem();
            if (player.isShiftKeyDown()) {
                // Retrieve the latest echo.
                vault.retrieveLatest().ifPresent(stack -> {
                    if (!player.getInventory().add(stack)) {
                        player.drop(stack, false);
                    }
                });
            } else if (!held.isEmpty()) {
                // Store one item from the held stack as an echo.
                ItemStack single = held.split(1);
                if (!vault.store(single)) {
                    held.grow(1); // Vault full/refused: return the item.
                    player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, held);
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}