package com.cerbon.bosses_of_mass_destruction.structure;

import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class BMDStructuresFeature {
    public static final StructureFeature<?, ?> LICH_TOWER_FEATURE = BMDStructures.LICH_TOWER_STRUCTURE.get().configured(IFeatureConfig.NONE);
    public static final StructureFeature<?, ?> GAUNTLET_FEATURE = BMDStructures.GAUNTLET_STRUCTURE.get().configured(IFeatureConfig.NONE);
    public static final StructureFeature<?, ?> OBSIDILITH_ARENA_FEATURE = BMDStructures.OBSIDILITH_ARENA_STRUCTURE.get().configured(IFeatureConfig.NONE);
    public static final StructureFeature<?, ?> VOID_BLOSSOM_FEATURE = BMDStructures.VOID_BLOSSOM_STRUCTURE.get().configured(IFeatureConfig.NONE);

    public static StructureRegister LICH_STRUCTURE_REGISTRY = null;
    public static StructureRegister GAUNTLET_REGISTRY = null;
    public static StructureRegister OBSIDILITH_ARENA_REGISTRY = null;
    public static StructureRegister VOID_BLOSSOM_REGISTRY = null;

    public static void registerConfiguredStructures() {
        Registry<StructureFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE;

        Registry.register(registry, new ResourceLocation(BMDConstants.MOD_ID, "lich_tower_feature"), LICH_TOWER_FEATURE);
        Registry.register(registry, new ResourceLocation(BMDConstants.MOD_ID, "gauntlet_feature"), GAUNTLET_FEATURE);
        Registry.register(registry, new ResourceLocation(BMDConstants.MOD_ID, "obsidilith_feature"), OBSIDILITH_ARENA_FEATURE);
        Registry.register(registry, new ResourceLocation(BMDConstants.MOD_ID, "void_blossom_feature"), VOID_BLOSSOM_FEATURE);

        LICH_STRUCTURE_REGISTRY = new StructureRegister(new ResourceLocation(BMDConstants.MOD_ID, "lich_tower_feature"));
        GAUNTLET_REGISTRY = new StructureRegister(new ResourceLocation(BMDConstants.MOD_ID, "gauntlet_feature"));
        OBSIDILITH_ARENA_REGISTRY = new StructureRegister(new ResourceLocation(BMDConstants.MOD_ID, "obsidilith_feature"));
        VOID_BLOSSOM_REGISTRY = new StructureRegister(new ResourceLocation(BMDConstants.MOD_ID, "void_blossom_feature"));

        FlatGenerationSettings.STRUCTURE_FEATURES.put(BMDStructures.LICH_TOWER_STRUCTURE.get(), LICH_TOWER_FEATURE);
        FlatGenerationSettings.STRUCTURE_FEATURES.put(BMDStructures.GAUNTLET_STRUCTURE.get(), GAUNTLET_FEATURE);
        FlatGenerationSettings.STRUCTURE_FEATURES.put(BMDStructures.OBSIDILITH_ARENA_STRUCTURE.get(), OBSIDILITH_ARENA_FEATURE);
        FlatGenerationSettings.STRUCTURE_FEATURES.put(BMDStructures.VOID_BLOSSOM_STRUCTURE.get(), VOID_BLOSSOM_FEATURE);
    }
}
