package com.cerbon.bosses_of_mass_destruction.structure.structure_repair;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.structure.BMDStructures;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class LichStructureRepair implements StructureRepair{

    @Override
    public ResourceKey<ConfiguredStructureFeature<?, ?>> associatedStructure() {
        return BMDStructures.LICH_STRUCTURE_REGISTRY.getConfiguredStructureKey();
    }

    @Override
    public void repairStructure(ServerLevel level, StructureStart structureStart) {
        BlockPos pos = altarCenter(level, structureStart);

        BlockState altar = BMDBlocks.CHISELED_STONE_ALTAR.get().defaultBlockState();
        List<BlockPos> positions = List.of(pos.west(6), pos.east(6), pos.north(6), pos.south(6));

        for (BlockPos altarPos : positions){
            level.setBlockAndUpdate(altarPos, altar);
            BMDUtils.spawnParticle(level, BMDParticles.SOUL_FLAME.get(), VecUtils.asVec3(altarPos).add(0.5, 1.0, 0.5), VecUtils.unit,10, 0);
        }
    }

    @Override
    public boolean shouldRepairStructure(ServerLevel level, StructureStart structureStart) {
        BlockPos pos = altarCenter(level, structureStart);
        boolean hasAltar = level.getBlockState(pos.west(6)).getBlock() == BMDBlocks.CHISELED_STONE_ALTAR.get();
        boolean noBoss = level.getEntities(BMDEntities.LICH.get(), lichEntity -> lichEntity.distanceToSqr(VecUtils.asVec3(pos)) < 100 * 100).isEmpty();
        return !hasAltar && noBoss;
    }

    private BlockPos altarCenter(ServerLevel level, StructureStart structureStart){
        BoundingBox boundingBox = structureStart.getBoundingBox();
        int yPos = boundingBox.getCenter().below(16).getY();
        int centerX = boundingBox.getCenter().getX();
        int centerZ = boundingBox.getCenter().getZ();
        Optional<Pair<Integer, Integer>> gridPos = IntStream.rangeClosed(-2, 2)
                .boxed()
                .flatMap(x -> IntStream.rangeClosed(-2, 2).mapToObj(z -> new Pair<>(x + centerX, z + centerZ)))
                .max(Comparator.comparingInt(xzPair -> countChestsInColumn(boundingBox, level, xzPair)));

        return gridPos.map(xzPair -> new BlockPos(xzPair.getFirst(), yPos, xzPair.getSecond())).orElse(null);
    }

    private int countChestsInColumn(BoundingBox boundingBox, ServerLevel level, Pair<Integer, Integer> xzPair){
        return (int) IntStream.rangeClosed(boundingBox.minY(), boundingBox.maxY())
                .filter(y -> level.getBlockState(new BlockPos(xzPair.getFirst(), y, xzPair.getSecond())).getBlock() == Blocks.CHEST)
                .count();
    }
}
