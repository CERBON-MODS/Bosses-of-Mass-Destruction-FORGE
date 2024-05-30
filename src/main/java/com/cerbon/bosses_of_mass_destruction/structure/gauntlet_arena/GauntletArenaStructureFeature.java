package com.cerbon.bosses_of_mass_destruction.structure.gauntlet_arena;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;

public class GauntletArenaStructureFeature extends Structure<NoFeatureConfig> implements IFeatureConfig {
    public GauntletArenaStructureFeature(Codec<NoFeatureConfig> pCodec) {
        super(pCodec);
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return null;
    }
//    private static final ResourceLocation template = new ResourceLocation(BMDConstants.MOD_ID, "gauntlet_arena");
//
//    public GauntletArenaStructureFeature(Codec<NoFeatureConfig> codec) {
//        super(
//                codec,
//                PieceGeneratorSupplier.simple(
//                        PieceGeneratorSupplier.checkForBiomeOnTop(Heightmap.Type.WORLD_SURFACE_WG),
//                        GauntletArenaStructureFeature::addPieces
//                )
//        );
//    }
//
//    @Override
//    public GenerationStage.@Nonnull Decoration step() {
//        return GenerationStage.Decoration.SURFACE_STRUCTURES;
//    }
//
//    public static void addPieces(StructurePiecesBuilder collector, PieceGenerator.Context<?> context){
//        BlockPos blockPos = new BlockPos(context.chunkPos().getMinBlockX(), 15, context.chunkPos().getMinBlockZ());
//        Rotation rotation = Rotation.getRandom(context.random());
//        collector.addPiece(new BMDStructurePiece(context.structureManager(), blockPos, template, rotation, BMDStructures.GAUNTLET_STRUCTURE_PIECE.get()));
//    }
}
