package com.cerbon.bosses_of_mass_destruction.structure.obsidilith_arena;

import com.cerbon.bosses_of_mass_destruction.config.mob.ObsidilithConfig;
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

public class ObsidilithArenaStructureFeature extends StructureFeature<NoneFeatureConfiguration> implements FeatureConfiguration {
    private static final ResourceLocation template = new ResourceLocation(BMDConstants.MOD_ID, "obsidilith_arena");

    public ObsidilithArenaStructureFeature(Codec<NoneFeatureConfiguration> codec, ObsidilithConfig obsidilithConfig) {
        super(
                codec,
                PieceGeneratorSupplier.simple(
                        PieceGeneratorSupplier.checkForBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG),
                        (collector, context) -> ObsidilithArenaStructureFeature.addPieces(collector, context, obsidilithConfig)
                )
        );
    }

    @Override
    public GenerationStep.@NotNull Decoration step() {
        return GenerationStep.Decoration.SURFACE_STRUCTURES;
    }

    public static void addPieces(StructurePiecesBuilder collector, PieceGenerator.Context<?> context, ObsidilithConfig obsidilithConfig){
        int x = context.chunkPos().getMinBlockX();
        int z = context.chunkPos().getMinBlockZ();
        int y = obsidilithConfig.arenaGeneration.generationHeight;
        BlockPos blockPos = new BlockPos(x, y, z);
        Rotation rotation = Rotation.getRandom(context.random());
        collector.addPiece(new BMDStructurePiece(context.structureManager(), blockPos, template, rotation, BMDStructures.OBSIDILITH_STRUCTURE_PIECE.get()));
    }
}
