package com.cerbon.bosses_of_mass_destruction.item.custom;

import com.cerbon.bosses_of_mass_destruction.util.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.util.maelstrom.static_utilities.VecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Consumer;

public class WallTeleport {
    private final ServerLevel level;
    private final Entity entity;
    private final double startRange = 3.0;
    private final double endRange = 20.0;

    public WallTeleport(ServerLevel level, Entity entity) {
        this.level = level;
        this.entity = entity;
    }

    public boolean tryTeleport(Vec3 direction, Vec3 position) {
        return tryTeleport(direction, position, this::teleportTo);
    }

    public boolean tryTeleport(Vec3 direction, Vec3 position, Consumer<BlockPos> action){
        Context context = new Context(direction, position);
        BlockPos teleportStart = getTeleportStart(context);
        if (teleportStart != null){
            BlockPos teleportEnd = getTeleportEnd(context, teleportStart);
            if (teleportEnd != null){
                action.accept(teleportEnd);
                return true;
            }
        }
        return false;
    }

    private BlockPos getTeleportStart(Context context){
        BlockPos startPos = BlockPos.containing(context.position);
        BlockPos endPos = BlockPos.containing(context.position.add(context.direction.multiply(startRange, startRange, startRange)));
        List<BlockPos> blocksToCheck = MathUtils.getBlocksInLine(startPos, endPos);

        for (BlockPos pos : blocksToCheck){
            if (level.getBlockState(pos).isRedstoneConductor(level, pos))
                return pos;
        }
        return null;
    }

    private BlockPos getTeleportEnd(Context context, BlockPos startPos){
        BlockPos endPos = startPos.offset(BlockPos.containing(context.direction.multiply(endRange, endRange, endRange)));
        List<BlockPos> blocksToCheck = MathUtils.getBlocksInLine(startPos, endPos);

        for (BlockPos pos : blocksToCheck){
            BlockState blockState = level.getBlockState(pos);
            if (blockState.isAir() && level.getBlockState(pos.above()).isAir())
                return pos;

            if (blockState.getBlock().defaultDestroyTime() < 0)
                return null;
        }
        return null;
    }

    private void teleportTo(BlockPos teleportPos){
        Vec3 pos = VecUtils.asVec3d(teleportPos).add(new Vec3(0.5, 0.0, 0.5));
        entity.teleportTo(pos.x, pos.y, pos.z);
    }

    private static class Context {
        private final Vec3 direction;
        private final Vec3 position;

        public Context(Vec3 direction, Vec3 position) {
            this.direction = direction;
            this.position = position;
        }
    }
}
