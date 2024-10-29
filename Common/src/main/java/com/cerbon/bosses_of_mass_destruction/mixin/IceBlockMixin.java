package com.cerbon.bosses_of_mass_destruction.mixin;

import com.cerbon.bosses_of_mass_destruction.structure.BMDStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IceBlock.class)
public class IceBlockMixin {

    @Inject(method = "playerDestroy", at = @At("TAIL"))
    private void onAfterBreak(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack, CallbackInfo ci) {
        var lookup = level.registryAccess().lookup(Registries.ENCHANTMENT).get();
        if (EnchantmentHelper.getItemEnchantmentLevel(lookup.getOrThrow(Enchantments.SILK_TOUCH), stack) == 0 && level instanceof ServerLevel serverLevel && serverLevel.structureManager().getStructureAt(pos, serverLevel.structureManager().registryAccess().registryOrThrow(Registries.STRUCTURE).getOrThrow(BMDStructures.LICH_STRUCTURE_REGISTRY.getConfiguredStructureKey())).isValid())
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }
}
