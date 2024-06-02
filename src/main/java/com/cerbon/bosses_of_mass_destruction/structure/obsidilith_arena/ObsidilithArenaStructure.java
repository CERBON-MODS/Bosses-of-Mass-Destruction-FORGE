package com.cerbon.bosses_of_mass_destruction.structure.obsidilith_arena;

import com.cerbon.bosses_of_mass_destruction.BossesOfMassDestruction;
import com.cerbon.bosses_of_mass_destruction.config.mob.ObsidilithConfig;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.mojang.serialization.Codec;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class ObsidilithArenaStructure extends Structure<NoFeatureConfig> implements IFeatureConfig {

    private final ObsidilithConfig config;

    public ObsidilithArenaStructure(Codec<NoFeatureConfig> codec, ObsidilithConfig config) {
        super(codec);
        this.config = config;
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return ObsidilithArenaStructure.Start::new;
    }

    @Override
    public GenerationStage.Decoration step() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }

    public class Start extends StructureStart<NoFeatureConfig> {

        public Start(Structure<NoFeatureConfig> structureIn, int chunkX, int chunkZ, MutableBoundingBox mutableBoundingBox, int referenceIn, long seedIn) {
            super(structureIn, chunkX, chunkZ, mutableBoundingBox, referenceIn, seedIn);
        }

        @Override
        public void generatePieces(DynamicRegistries dynamicRegistryManager, ChunkGenerator chunkGenerator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, NoFeatureConfig config) {
            ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);

            int x = chunkPos.getMinBlockX();
            int z = chunkPos.getMinBlockZ();
            int y = ObsidilithArenaStructure.this.config.arenaGeneration.generationHeight;

            BlockPos blockPos = new BlockPos(x, y, z);

            JigsawManager.addPieces(
                    dynamicRegistryManager,
                    new VillageConfig(() -> dynamicRegistryManager.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY)
                            .get(new ResourceLocation(BMDConstants.MOD_ID, "obsidilith_arena/start_pool")),
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

            BossesOfMassDestruction.LOGGER.debug("Obsidilith arena at " +
                    this.pieces.get(0).getBoundingBox().x0 + " " +
                    this.pieces.get(0).getBoundingBox().y0 + " " +
                    this.pieces.get(0).getBoundingBox().z0);
        }
    }
}
