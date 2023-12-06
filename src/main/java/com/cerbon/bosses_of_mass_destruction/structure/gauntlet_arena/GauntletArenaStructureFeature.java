package com.cerbon.bosses_of_mass_destruction.structure.gauntlet_arena;

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

public class GauntletArenaStructureFeature extends StructureFeature<NoneFeatureConfiguration> implements FeatureConfiguration {
    private static final ResourceLocation template = new ResourceLocation(BMDConstants.MOD_ID, "gauntlet_arena");

    public GauntletArenaStructureFeature(Codec<NoneFeatureConfiguration> codec) {
        super(
                codec,
                PieceGeneratorSupplier.simple(
                        PieceGeneratorSupplier.checkForBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG),
                        GauntletArenaStructureFeature::addPieces
                )
        );
    }

    @Override
    public GenerationStep.@NotNull Decoration step() {
        return GenerationStep.Decoration.SURFACE_STRUCTURES;
    }

    public static void addPieces(StructurePiecesBuilder collector, PieceGenerator.Context<?> context){
        BlockPos blockPos = new BlockPos(context.chunkPos().getMinBlockX(), 15, context.chunkPos().getMinBlockZ());
        Rotation rotation = Rotation.getRandom(context.random());
        collector.addPiece(new BMDStructurePiece(context.structureManager(), blockPos, template, rotation, BMDStructures.GAUNTLET_STRUCTURE_PIECE.get()));
    }
}
