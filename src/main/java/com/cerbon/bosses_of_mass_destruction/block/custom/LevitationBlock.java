package com.cerbon.bosses_of_mass_destruction.block.custom;


import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class LevitationBlock extends ContainerBlock {
    private static final VoxelShape bottomShape = box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    private static final VoxelShape tableShape = box(2.0, 2.0, 2.0, 14.0, 14.0, 14.0);

    public LevitationBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable IBlockReader level, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        tooltip.add(new TranslationTextComponent("item.bosses_of_mass_destruction.levitation_block.tooltip").withStyle(TextFormatting.DARK_GRAY));
    }

    @Override
    public @Nonnull VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader level, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return VoxelShapes.or(bottomShape, tableShape);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader blockReader) {
        return new LevitationBlockEntity();
    }

    @Override
    public @Nonnull BlockRenderType getRenderShape(@Nonnull BlockState state) {
        return BlockRenderType.MODEL;
    }

//    @Nullable
//    @Override
//    public <T extends TileEntity> BlockEntityTicker<T> getTicker(@Nonnull World level, @Nonnull BlockState state, @Nonnull TileEntityType<T> blockEntityType) {
//        return createTickerHelper(blockEntityType, BMDBlockEntities.LEVITATION_BLOCK_ENTITY.get(), LevitationBlockEntity::tick);
//    }
}
