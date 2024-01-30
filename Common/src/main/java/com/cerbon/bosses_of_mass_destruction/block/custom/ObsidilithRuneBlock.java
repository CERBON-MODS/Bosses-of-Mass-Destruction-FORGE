package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class ObsidilithRuneBlock extends Block {

    public ObsidilithRuneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        linkToEntities(level, pos);
    }

    private void linkToEntities(ServerLevel level, BlockPos pos){
        level.getEntitiesOfClass(ObsidilithEntity.class, new AABB(pos).inflate(15.0, 40.0, 15.0)).forEach(
                entity -> entity.addActivePillar(pos)
        );
    }

    @Override
    public void onPlace(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean movedByPiston) {
        level.scheduleTick(pos, this, 10);
    }
}
