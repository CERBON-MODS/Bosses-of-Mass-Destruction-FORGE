package com.cerbon.bosses_of_mass_destruction.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.IceBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(IceBlock.class)
public class IceBlockMixin {

    @Inject(method = "playerDestroy", at = @At("TAIL"))
    private void onAfterBreak(World level, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity blockEntity, ItemStack stack, CallbackInfo ci) {
//        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0 && level instanceof ServerWorld serverLevel && serverLevel.structureFeatureManager().getStructureAt(pos, serverLevel.structureFeatureManager().registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).getOrThrow(BMDStructures.LICH_STRUCTURE_REGISTRY.getConfiguredStructureKey())).isValid())
//            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }
}
