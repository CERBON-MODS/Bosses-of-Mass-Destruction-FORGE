package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlockEntities;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.VoidBlossomEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class VoidBlossomSummonBlockEntity extends BlockEntity {
    private int age = 0;

    public VoidBlossomSummonBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMDBlockEntities.VOID_BLOSSOM_SUMMON_BLOCK_ENTITY.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VoidBlossomSummonBlockEntity entity){
        if (!level.isClientSide && entity.age % 20 == 0){
            boolean playersInBox = !level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(40.0)).isEmpty();
            if (playersInBox){
                Vec3 spawnPos = VecUtils.asVec3(pos).add(new Vec3(0.5, 0.0, 0.5));
                VoidBlossomEntity boss = BMDEntities.VOID_BLOSSOM.get().create(level);
                if (boss == null) return;
                boss.absMoveTo(spawnPos.x, spawnPos.y, spawnPos.z);
                level.addFreshEntity(boss);
                level.removeBlock(pos, false);
            }
        }
        entity.age++;
    }
}
