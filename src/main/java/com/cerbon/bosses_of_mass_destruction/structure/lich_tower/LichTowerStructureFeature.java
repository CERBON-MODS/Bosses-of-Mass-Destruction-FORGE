package com.cerbon.bosses_of_mass_destruction.structure.lich_tower;

import com.cerbon.bosses_of_mass_destruction.structure.BMDStructures;
import com.cerbon.bosses_of_mass_destruction.structure.util.BMDStructurePiece;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.jetbrains.annotations.NotNull;

public class LichTowerStructureFeature extends StructureFeature<NoneFeatureConfiguration> implements FeatureConfiguration {

    public LichTowerStructureFeature(Codec<NoneFeatureConfiguration> codec) {
        super(
                codec,
                PieceGeneratorSupplier.simple(
                        PieceGeneratorSupplier.checkForBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG),
                        LichTowerStructureFeature::addPieces
                )
        );
    }

    @Override
    public GenerationStep.@NotNull Decoration step() {
        return GenerationStep.Decoration.SURFACE_STRUCTURES;
    }

    public static void addPieces(StructurePiecesBuilder collector, PieceGenerator.Context<?> context){
        int x = context.chunkPos().getMinBlockX();
        int z = context.chunkPos().getMinBlockZ();
        int y = context.chunkGenerator().getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor()) - 7;
        Rotation rotation = Rotation.getRandom(context.random());
        BlockPos blockPos = new BlockPos(x, y, z).offset(new BlockPos(-15, 0, -15).rotate(rotation));
        collector.addPiece(
                new BMDStructurePiece(
                        context.structureManager(),
                        blockPos,
                        new ResourceLocation(BMDConstants.MOD_ID, "lich_tower_2"),
                        rotation,
                        BMDStructures.LICH_STRUCTURE_PIECE.get()
                )
        );
        collector.addPiece(
                new BMDStructurePiece(
                        context.structureManager(),
                        blockPos.above(59 - 11),
                        new ResourceLocation(BMDConstants.MOD_ID, "lich_tower_1"),
                        rotation,
                        BMDStructures.LICH_STRUCTURE_PIECE.get()
                )
        );

    }
}
