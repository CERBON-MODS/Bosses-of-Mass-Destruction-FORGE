package com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;

public class VoidBlossomArenaStructureFeature extends Structure<NoFeatureConfig> implements IFeatureConfig {
    public VoidBlossomArenaStructureFeature(Codec<NoFeatureConfig> pCodec) {
        super(pCodec);
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return null;
    }
//    public VoidBlossomArenaStructureFeature(Codec<NoFeatureConfig> codec) {
//        super(
//                codec,
//                PieceGeneratorSupplier.simple(
//                        PieceGeneratorSupplier.checkForBiomeOnTop(Heightmap.Type.WORLD_SURFACE_WG),
//                        VoidBlossomArenaStructureFeature::addPieces)
//        );
//    }
//
//    @Override
//    public GenerationStage.@Nonnull Decoration step() {
//        return GenerationStage.Decoration.SURFACE_STRUCTURES;
//    }
//
//    public static void addPieces(StructurePiecesBuilder collector, PieceGenerator.Context<?> context){
//        int x = context.chunkPos().getMinBlockX();
//        int z = context.chunkPos().getMinBlockZ();
//        int y = 35 + context.chunkGenerator().getMinY();
//
//        collector.addPiece(
//                new CodeStructurePiece(
//                        BMDStructures.VOID_BLOSSOM_CAVERN_PIECE.get(),
//                        new MutableBoundingBox(new BlockPos(x, y, z)).inflatedBy(32),
//                        new VoidBlossomCavernPieceGenerator()
//                )
//        );
//    }
}
