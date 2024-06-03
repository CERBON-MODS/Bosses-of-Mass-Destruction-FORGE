package com.cerbon.bosses_of_mass_destruction.structure.structure_repair;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.structure.BMDStructuresFeature;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class LichStructureRepair implements StructureRepair{

    @Override
    public RegistryKey<StructureFeature<?, ?>> associatedStructure() {
        return BMDStructuresFeature.LICH_STRUCTURE_REGISTRY.getConfiguredStructureKey();
    }

    @Override
    public void repairStructure(ServerWorld level, StructureStart structureStart) {
        BlockPos pos = altarCenter(level, structureStart);

        BlockState altar = BMDBlocks.CHISELED_STONE_ALTAR.get().defaultBlockState();
        List<BlockPos> positions = Lists.newArrayList(pos.west(6), pos.east(6), pos.north(6), pos.south(6));

        for (BlockPos altarPos : positions){
            level.setBlockAndUpdate(altarPos, altar);
            BMDUtils.spawnParticle(level, BMDParticles.SOUL_FLAME.get(), VecUtils.asVec3(altarPos).add(0.5, 1.0, 0.5), VecUtils.unit,10, 0);
        }
    }

    @Override
    public boolean shouldRepairStructure(ServerWorld level, StructureStart structureStart) {
        BlockPos pos = altarCenter(level, structureStart);
        boolean hasAltar = level.getBlockState(pos.west(6)).getBlock() == BMDBlocks.CHISELED_STONE_ALTAR.get();
        boolean noBoss = level.getEntities(BMDEntities.LICH.get(), lichEntity -> lichEntity.distanceToSqr(VecUtils.asVec3(pos)) < 100 * 100).isEmpty();
        return !hasAltar && noBoss;
    }

    private BlockPos altarCenter(ServerWorld level, StructureStart structureStart){
        MutableBoundingBox boundingBox = structureStart.getBoundingBox();
        int yPos = boundingBox.getCenter().below(16).getY();
        int centerX = boundingBox.getCenter().getX();
        int centerZ = boundingBox.getCenter().getZ();
        Optional<Pair<Integer, Integer>> gridPos = IntStream.rangeClosed(-2, 2)
                .boxed()
                .flatMap(x -> IntStream.rangeClosed(-2, 2).mapToObj(z -> new Pair<>(x + centerX, z + centerZ)))
                .max(Comparator.comparingInt(xzPair -> countChestsInColumn(boundingBox, level, xzPair)));

        return gridPos.map(xzPair -> new BlockPos(xzPair.getFirst(), yPos, xzPair.getSecond())).orElse(null);
    }

    private int countChestsInColumn(MutableBoundingBox boundingBox, ServerWorld level, Pair<Integer, Integer> xzPair){
        return (int) IntStream.rangeClosed(boundingBox.y0, boundingBox.y1)
                .filter(y -> level.getBlockState(new BlockPos(xzPair.getFirst(), y, xzPair.getSecond())).getBlock() == Blocks.CHEST)
                .count();
    }
}
