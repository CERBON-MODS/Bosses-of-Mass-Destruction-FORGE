package com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern;

import com.cerbon.bosses_of_mass_destruction.structure.BMDStructurePieces;
import com.cerbon.bosses_of_mass_destruction.structure.util.CodeStructurePiece;
import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class VoidBlossomArenaStructure extends Structure<NoFeatureConfig> {

    public VoidBlossomArenaStructure(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return VoidBlossomArenaStructure.Start::new;
    }

    @Override
    public GenerationStage.Decoration step() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }

    public static class Start extends StructureStart<NoFeatureConfig> {

        public Start(Structure<NoFeatureConfig> structureIn, int chunkX, int chunkZ, MutableBoundingBox mutableBoundingBox, int referenceIn, long seedIn) {
            super(structureIn, chunkX, chunkZ, mutableBoundingBox, referenceIn, seedIn);
        }

        @Override
        public void generatePieces(DynamicRegistries dynamicRegistryManager, ChunkGenerator chunkGenerator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, NoFeatureConfig config) {
            ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);

            int x = chunkPos.getMinBlockX();
            int z = chunkPos.getMinBlockZ();
            int y = 35;

            BlockPos blockPos = new BlockPos(x, y, z);

            this.pieces.add(new CodeStructurePiece(BMDStructurePieces.VOID_BLOSSOM_PIECE, new MutableBoundingBox(blockPos.getX() - 32, blockPos.getY() - 32, blockPos.getZ() - 32, blockPos.getX() + 32, blockPos.getY() + 32, blockPos.getZ() + 32), new VoidBlossomCavernPieceGenerator()));

            this.calculateBoundingBox();
        }
    }
}
