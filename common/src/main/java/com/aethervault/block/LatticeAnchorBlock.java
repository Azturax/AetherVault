package com.aethervault.block;

import java.util.function.Supplier;

import com.aethervault.storage.lattice.LatticeAnchorBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
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
 * The Lattice Anchor block: anchors a bounded voxel lattice storage volume.
 * Its block entity manages the lattice cell grid.
 */
public class LatticeAnchorBlock extends Block implements EntityBlock {

    private final Supplier<BlockEntityType<LatticeAnchorBlockEntity>> typeSupplier;

    public LatticeAnchorBlock(Supplier<BlockEntityType<LatticeAnchorBlockEntity>> typeSupplier, Properties properties) {
        super(properties);
        this.typeSupplier = typeSupplier;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LatticeAnchorBlockEntity(typeSupplier.get(), pos);
    }

    /**
     * Right-click with an item inserts it into the lattice; sneak-right-click
     * pops the most recently stored item back into the player's inventory.
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof LatticeAnchorBlockEntity lattice) {
            ItemStack held = player.getMainHandItem();
            if (player.isShiftKeyDown()) {
                lattice.retrieveLatest().ifPresent(stack -> {
                    if (!player.getInventory().add(stack)) {
                        player.drop(stack, false);
                    }
                });
            } else if (!held.isEmpty()) {
                ItemStack single = held.split(1);
                if (!lattice.store(single)) {
                    held.grow(1); // Lattice full: return the item.
                    player.setItemInHand(InteractionHand.MAIN_HAND, held);
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}