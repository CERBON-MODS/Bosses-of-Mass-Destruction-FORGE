package com.cerbon.bosses_of_mass_destruction.structure.lich_tower;

import com.cerbon.bosses_of_mass_destruction.BossesOfMassDestruction;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.mojang.serialization.Codec;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class LichTowerStructure extends Structure<NoFeatureConfig> {

    public LichTowerStructure(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return LichTowerStructure.Start::new;
    }

    @Override
    public GenerationStage.Decoration step() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }

    @Override
    protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeProvider biomeSource, long seed, SharedSeedRandom chunkRandom, int chunkX, int chunkZ, Biome biomeIn, ChunkPos chunkPos, NoFeatureConfig featureConfig) {
        for(Biome biome : biomeSource.getBiomesWithin(chunkPos.x * 16 + 9, chunkGenerator.getSeaLevel(), chunkPos.z * 16 + 9, 32)) {
            if (!biome.getGenerationSettings().isValidStart(this))
                return false;
        }

        return true;
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
            int y = chunkGenerator.getBaseHeight(x, z, Heightmap.Type.WORLD_SURFACE_WG) - 7;

            BlockPos blockPos = new BlockPos(x, y, z).offset(-15, 0, -15);

            JigsawManager.addPieces(
                    dynamicRegistryManager,
                    new VillageConfig(() -> dynamicRegistryManager.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY)
                            .get(new ResourceLocation(BMDConstants.MOD_ID, "lich_tower/start_pool")),
                            10),
                    AbstractVillagePiece::new,
                    chunkGenerator,
                    templateManagerIn,
                    blockPos,
                    this.pieces,
                    this.random,
                    false,
                    false);

            this.calculateBoundingBox();

            BossesOfMassDestruction.LOGGER.debug("Lich Tower at " +
                    this.pieces.get(0).getBoundingBox().x0 + " " +
                    this.pieces.get(0).getBoundingBox().y0 + " " +
                    this.pieces.get(0).getBoundingBox().z0);
        }
    }
}
