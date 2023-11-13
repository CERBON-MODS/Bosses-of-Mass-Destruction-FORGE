package com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.entity.util.IEntityTick;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;

public class LightBlockRemover implements IEntityTick<ServerLevel> {
    private final LivingEntity entity;

    public static final float deathMaxAge = 70f;

    public LightBlockRemover(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public void tick(ServerLevel level) {
        entity.deathTime++;
        float interceptedTime = MathUtils.ratioLerp((float) entity.deathTime, 0.5f, deathMaxAge, 0f) * 0.7f;
        level.setBlockAndUpdate(entity.blockPosition(), Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, Math.round((1 - interceptedTime) * 15)));

        if (entity.deathTime == 49)
            BMDUtils.playSound(level, entity.position(), BMDSounds.VOID_BLOSSOM_FALL.get(), SoundSource.HOSTILE, 1.5f, 32, null);

        if (entity.deathTime == deathMaxAge) {
            if (level.getBlockState(entity.blockPosition()).getBlock() == Blocks.LIGHT)
                level.setBlockAndUpdate(entity.blockPosition(), Blocks.AIR.defaultBlockState());

            level.broadcastEntityEvent(entity, (byte) 60);
            entity.remove(Entity.RemovalReason.KILLED);
        }
    }
}
