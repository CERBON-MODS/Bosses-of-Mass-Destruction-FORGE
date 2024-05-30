package com.cerbon.bosses_of_mass_destruction.structure.obsidilith_arena;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;

public class ObsidilithArenaStructureFeature extends Structure<NoFeatureConfig> implements IFeatureConfig {
    public ObsidilithArenaStructureFeature(Codec<NoFeatureConfig> pCodec) {
        super(pCodec);
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return null;
    }
//    private static final ResourceLocation template = new ResourceLocation(BMDConstants.MOD_ID, "obsidilith_arena");
//
//    public ObsidilithArenaStructureFeature(Codec<NoFeatureConfig> codec, ObsidilithConfig obsidilithConfig) {
//        super(
//                codec,
//                PieceGeneratorSupplier.simple(
//                        PieceGeneratorSupplier.checkForBiomeOnTop(Heightmap.Type.WORLD_SURFACE_WG),
//                        (collector, context) -> ObsidilithArenaStructureFeature.addPieces(collector, context, obsidilithConfig)
//                )
//        );
//    }
//
//    @Override
//    public GenerationStage.@Nonnull Decoration step() {
//        return GenerationStage.Decoration.SURFACE_STRUCTURES;
//    }
//
//    public static void addPieces(StructurePiecesBuilder collector, PieceGenerator.Context<?> context, ObsidilithConfig obsidilithConfig){
//        int x = context.chunkPos().getMinBlockX();
//        int z = context.chunkPos().getMinBlockZ();
//        int y = obsidilithConfig.arenaGeneration.generationHeight;
//        BlockPos blockPos = new BlockPos(x, y, z);
//        Rotation rotation = Rotation.getRandom(context.random());
//        collector.addPiece(new BMDStructurePiece(context.structureManager(), blockPos, template, rotation, BMDStructures.OBSIDILITH_STRUCTURE_PIECE.get()));
//    }
}
