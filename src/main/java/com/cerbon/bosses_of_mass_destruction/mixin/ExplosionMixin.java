package com.cerbon.bosses_of_mass_destruction.mixin;

import com.cerbon.bosses_of_mass_destruction.block.custom.MonolithBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.annotation.Nullable;

@Mixin(ServerWorld.class)
public class ExplosionMixin {

    @ModifyVariable(at = @At(value = "HEAD"), method = "explode", argsOnly = true)
    private float Explosion(float g, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionContext damageCalculator, double x, double y, double z, float radius, boolean fire, Explosion.Mode explosionInteraction){
        World level = (World) (Object) this;
        if (!level.isClientSide)
            return MonolithBlock.getExplosionPower((ServerWorld) level, new BlockPos(x, y, z), g);

        return g;
    }
}
