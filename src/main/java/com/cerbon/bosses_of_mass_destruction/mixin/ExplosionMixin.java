package com.cerbon.bosses_of_mass_destruction.mixin;

import com.cerbon.bosses_of_mass_destruction.block.custom.MonolithBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.annotation.Nullable;

@Mixin(ServerLevel.class)
public class ExplosionMixin {

    @ModifyVariable(at = @At(value = "HEAD"), method = "explode", argsOnly = true)
    private float Explosion(float g, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction){
        Level level = (Level) (Object) this;
        if (!level.isClientSide)
            return MonolithBlock.getExplosionPower((ServerLevel) level, BlockPos.containing(x, y, z), g);

        return g;
    }
}
