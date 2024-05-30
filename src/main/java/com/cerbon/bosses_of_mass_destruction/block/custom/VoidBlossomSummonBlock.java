package com.cerbon.bosses_of_mass_destruction.block.custom;

import net.minecraft.block.ContainerBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class VoidBlossomSummonBlock extends ContainerBlock {

    public VoidBlossomSummonBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader blockReader) {
        return new VoidBlossomSummonBlockEntity();
    }

//    @Nullable
//    @Override
//    public <T extends TileEntity> BlockEntityTicker<T> getTicker(@Nonnull World level, @Nonnull BlockState state, @Nonnull TileEntityType<T> blockEntityType) {
//        return createTickerHelper(blockEntityType, BMDBlockEntities.VOID_BLOSSOM_SUMMON_BLOCK_ENTITY.get(), VoidBlossomSummonBlockEntity::tick);
//    }
}
