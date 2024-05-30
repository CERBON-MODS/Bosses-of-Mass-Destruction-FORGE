package com.cerbon.bosses_of_mass_destruction.block.custom;

import net.minecraft.block.FlowerBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class VoidLilyBlock extends FlowerBlock implements ITileEntityProvider {

    public VoidLilyBlock(Properties properties) {
        super(Effects.GLOWING, 0, properties);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader blockReader) {
        return new VoidLilyBlockEntity();
    }

//    @Nullable
//    @Override
//    public <T extends TileEntity> BlockEntityTicker<T> getTicker(@Nonnull World level, @Nonnull BlockState state, @Nonnull TileEntityType<T> blockEntityType) {
//        return (level1, pos, state1, blockEntity) -> {
//            if (blockEntity instanceof VoidLilyBlockEntity)
//                VoidLilyBlockEntity.tick(level1, pos, state1, (VoidLilyBlockEntity) blockEntity);
//        };
//    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable IBlockReader level, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        tooltip.add(new TranslationTextComponent("block.bosses_of_mass_destruction.void_lily.tooltip").withStyle(TextFormatting.DARK_GRAY));
    }
}
