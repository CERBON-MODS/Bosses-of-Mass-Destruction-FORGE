package com.cerbon.bosses_of_mass_destruction.item.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.MathUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.Entity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.function.Consumer;

public class WallTeleport {
    private final ServerWorld level;
    private final Entity entity;

    public WallTeleport(ServerWorld level, Entity entity) {
        this.level = level;
        this.entity = entity;
    }

    public boolean tryTeleport(Vector3d direction, Vector3d position) {
        return tryTeleport(direction, position, this::teleportTo);
    }

    public boolean tryTeleport(Vector3d direction, Vector3d position, Consumer<BlockPos> action){
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
        BlockPos startPos = new BlockPos(context.position);
        double startRange = 3.0;
        BlockPos endPos = new BlockPos(context.position.add(context.direction.scale(startRange)));
        List<BlockPos> blocksToCheck = MathUtils.getBlocksInLine(startPos, endPos);

        for (BlockPos pos : blocksToCheck){
            if (level.getBlockState(pos).isRedstoneConductor(level, pos))
                return pos;
        }
        return null;
    }

    private BlockPos getTeleportEnd(Context context, BlockPos startPos){
        double endRange = 20.0;
        BlockPos endPos = startPos.offset(new BlockPos(context.direction.scale(endRange)));
        List<BlockPos> blocksToCheck = MathUtils.getBlocksInLine(startPos, endPos);

        for (BlockPos pos : blocksToCheck){
            BlockState blockState = level.getBlockState(pos);

            if (blockState.isAir() && level.getBlockState(pos.above()).isAir())
                return pos;

//            if (blockState.getBlock().defaultDestroyTime() < 0)
//                return null;
        }
        return null;
    }

    private void teleportTo(BlockPos teleportPos){
        Vector3d pos = VecUtils.asVec3(teleportPos).add(new Vector3d(0.5, 0.0, 0.5));
        entity.teleportTo(pos.x, pos.y, pos.z);
    }

    private static class Context {
        private final Vector3d direction;
        private final Vector3d position;

        public Context(Vector3d direction, Vector3d position) {
            this.direction = direction;
            this.position = position;
        }
    }
}
