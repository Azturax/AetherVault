package com.aethervault.block;

import java.util.function.Supplier;

import com.aethervault.storage.echo.EchoVaultBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

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
}