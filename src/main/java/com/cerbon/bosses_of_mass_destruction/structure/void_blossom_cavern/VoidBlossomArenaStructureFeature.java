package com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern;

import com.cerbon.bosses_of_mass_destruction.structure.BMDStructures;
import com.cerbon.bosses_of_mass_destruction.structure.util.CodeStructurePiece;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.jetbrains.annotations.NotNull;

public class VoidBlossomArenaStructureFeature extends StructureFeature<NoneFeatureConfiguration> implements FeatureConfiguration {
    public VoidBlossomArenaStructureFeature(Codec<NoneFeatureConfiguration> codec) {
        super(
                codec,
                PieceGeneratorSupplier.simple(
                        PieceGeneratorSupplier.checkForBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG),
                        VoidBlossomArenaStructureFeature::addPieces)
        );
    }

    @Override
    public GenerationStep.@NotNull Decoration step() {
        return GenerationStep.Decoration.SURFACE_STRUCTURES;
    }

    public static void addPieces(StructurePiecesBuilder collector, PieceGenerator.Context<?> context){
        int x = context.chunkPos().getMinBlockX();
        int z = context.chunkPos().getMinBlockZ();
        int y = 35 + context.chunkGenerator().getMinY();

        collector.addPiece(
                new CodeStructurePiece(
                        BMDStructures.VOID_BLOSSOM_CAVERN_PIECE.get(),
                        new BoundingBox(new BlockPos(x, y, z)).inflatedBy(32),
                        new VoidBlossomCavernPieceGenerator()
                )
        );
    }
}
