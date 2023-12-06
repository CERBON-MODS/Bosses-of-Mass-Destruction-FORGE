package com.cerbon.bosses_of_mass_destruction.structure;

import com.cerbon.bosses_of_mass_destruction.config.BMDConfig;
import com.cerbon.bosses_of_mass_destruction.structure.gauntlet_arena.GauntletArenaStructureFeature;
import com.cerbon.bosses_of_mass_destruction.structure.lich_tower.LichTowerStructureFeature;
import com.cerbon.bosses_of_mass_destruction.structure.obsidilith_arena.ObsidilithArenaStructureFeature;
import com.cerbon.bosses_of_mass_destruction.structure.util.BMDStructurePiece;
import com.cerbon.bosses_of_mass_destruction.structure.util.CodeStructurePiece;
import com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern.VoidBlossomArenaStructureFeature;
import com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern.VoidBlossomCavernPieceGenerator;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class BMDStructures {
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES = DeferredRegister.create(Registry.STRUCTURE_PIECE_REGISTRY, BMDConstants.MOD_ID);
    public static final DeferredRegister<StructureFeature<?>> STRUCTURE_FEATURES = DeferredRegister.create(Registry.STRUCTURE_FEATURE_REGISTRY, BMDConstants.MOD_ID);

    public static final BMDConfig config = AutoConfig.getConfigHolder(BMDConfig.class).getConfig();

    public static final StructureRegister OBSIDILITH_STRUCTURE_REGISTRY = new StructureRegister(new ResourceLocation(BMDConstants.MOD_ID, "obsidilith_arena"));
    public static final StructureRegister VOID_BLOSSOM_STRUCTURE_REGISTRY = new StructureRegister(new ResourceLocation(BMDConstants.MOD_ID, "void_blossom"));
    public static final StructureRegister GAUNTLET_STRUCTURE_REGISTRY = new StructureRegister(new ResourceLocation(BMDConstants.MOD_ID, "gauntlet_arena"));
    public static final StructureRegister LICH_STRUCTURE_REGISTRY = new StructureRegister(new ResourceLocation(BMDConstants.MOD_ID, "lich_tower"));

    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> OBSIDILITH_ARENA_STRUCTURE_FEATURE = STRUCTURE_FEATURES.register("obsidilith_arena", () -> new ObsidilithArenaStructureFeature(NoneFeatureConfiguration.CODEC, config.obsidilithConfig));
    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> VOID_BLOSSOM_STRUCTURE_FEATURE = STRUCTURE_FEATURES.register("void_blossom", () -> new VoidBlossomArenaStructureFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> GAUNTLET_STRUCTURE_FEATURE = STRUCTURE_FEATURES.register("gauntlet_arena", () -> new GauntletArenaStructureFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> LICH_TOWER_STRUCTURE_FEATURE = STRUCTURE_FEATURES.register("lich_tower", () -> new LichTowerStructureFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<StructurePieceType> OBSIDILITH_STRUCTURE_PIECE = STRUCTURE_PIECES.register("obsidilith_arena", () -> StructureFactories.OBSIDILITH_ARENA);
    public static final RegistryObject<StructurePieceType> VOID_BLOSSOM_CAVERN_PIECE = STRUCTURE_PIECES.register("void_blossom_piece", () -> StructureFactories.VOID_BLOSSOM);
    public static final RegistryObject<StructurePieceType> GAUNTLET_STRUCTURE_PIECE = STRUCTURE_PIECES.register("gauntlet_arena_piece", () -> StructureFactories.GAUNTLET_ARENA);
    public static final RegistryObject<StructurePieceType> LICH_STRUCTURE_PIECE = STRUCTURE_PIECES.register("lich_tower_piece", () -> StructureFactories.LICH_TOWER);

    public static final TagKey<ConfiguredStructureFeature<?, ?>> SOUL_STAR_STRUCTURE_KEY = TagKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation(BMDConstants.MOD_ID, "soul_star_target"));
    public static final TagKey<ConfiguredStructureFeature<?, ?>> VOID_LILY_STRUCTURE_KEY = TagKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation(BMDConstants.MOD_ID, "void_lily_target"));

    public static void register(IEventBus eventBus){
        STRUCTURE_FEATURES.register(eventBus);
        STRUCTURE_PIECES.register(eventBus);
    }

    private static class StructureFactories {
        public static final StructurePieceType OBSIDILITH_ARENA = (context, tag) -> new BMDStructurePiece(context.structureManager(), tag,BMDStructures.OBSIDILITH_STRUCTURE_PIECE.get());
        public static final StructurePieceType VOID_BLOSSOM = (context, tag) -> new CodeStructurePiece(BMDStructures.VOID_BLOSSOM_CAVERN_PIECE.get(), tag, new VoidBlossomCavernPieceGenerator());
        public static final StructurePieceType GAUNTLET_ARENA = (context, tag) -> new BMDStructurePiece(context.structureManager(), tag, BMDStructures.GAUNTLET_STRUCTURE_PIECE.get());
        public static final StructurePieceType LICH_TOWER = (context, tag) -> new BMDStructurePiece(context.structureManager(), tag, BMDStructures.LICH_STRUCTURE_PIECE.get());
    }
}
