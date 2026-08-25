package com.aethervault.block;

import java.util.function.Supplier;

import com.aethervault.storage.lattice.LatticeAnchorBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

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
}