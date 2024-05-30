package com.cerbon.bosses_of_mass_destruction.structure.lich_tower;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;

public class LichTowerStructureFeature extends Structure<NoFeatureConfig> implements IFeatureConfig {
    public LichTowerStructureFeature(Codec<NoFeatureConfig> pCodec) {
        super(pCodec);
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return null;
    }

//    public LichTowerStructureFeature(Codec<NoFeatureConfig> codec) {
//        super(
//                codec,
//                PieceGeneratorSupplier.simple(
//                        PieceGeneratorSupplier.checkForBiomeOnTop(Heightmap.Type.WORLD_SURFACE_WG),
//                        LichTowerStructureFeature::addPieces
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
//        int x = context.chunkPos().getMinBlockX();
//        int z = context.chunkPos().getMinBlockZ();
//        int y = context.chunkGenerator().getBaseHeight(x, z, Heightmap.Type.WORLD_SURFACE_WG, context.heightAccessor()) - 7;
//        Rotation rotation = Rotation.getRandom(context.random());
//        BlockPos blockPos = new BlockPos(x, y, z).offset(new BlockPos(-15, 0, -15).rotate(rotation));
//        collector.addPiece(
//                new BMDStructurePiece(
//                        context.structureManager(),
//                        blockPos,
//                        new ResourceLocation(BMDConstants.MOD_ID, "lich_tower_2"),
//                        rotation,
//                        BMDStructures.LICH_STRUCTURE_PIECE.get()
//                )
//        );
//        collector.addPiece(
//                new BMDStructurePiece(
//                        context.structureManager(),
//                        blockPos.above(59 - 11),
//                        new ResourceLocation(BMDConstants.MOD_ID, "lich_tower_1"),
//                        rotation,
//                        BMDStructures.LICH_STRUCTURE_PIECE.get()
//                )
//        );
//
//    }
}
