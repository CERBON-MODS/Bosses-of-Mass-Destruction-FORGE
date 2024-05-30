package com.cerbon.bosses_of_mass_destruction.structure;

public class BMDStructures {
//    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES = DeferredRegister.create(Registry.STRUCTURE_PIECE_REGISTRY, BMDConstants.MOD_ID);
//    public static final DeferredRegister<Structure<?>> STRUCTURE_FEATURES = DeferredRegister.create(Registry.STRUCTURE_FEATURE_REGISTRY, BMDConstants.MOD_ID);
//
//    public static final BMDConfig config = AutoConfig.getConfigHolder(BMDConfig.class).getConfig();
//
//    public static final StructureRegister OBSIDILITH_STRUCTURE_REGISTRY = new StructureRegister(new ResourceLocation(BMDConstants.MOD_ID, "obsidilith_arena"));
//    public static final StructureRegister VOID_BLOSSOM_STRUCTURE_REGISTRY = new StructureRegister(new ResourceLocation(BMDConstants.MOD_ID, "void_blossom"));
//    public static final StructureRegister GAUNTLET_STRUCTURE_REGISTRY = new StructureRegister(new ResourceLocation(BMDConstants.MOD_ID, "gauntlet_arena"));
//    public static final StructureRegister LICH_STRUCTURE_REGISTRY = new StructureRegister(new ResourceLocation(BMDConstants.MOD_ID, "lich_tower"));
//
//    public static final RegistryObject<Structure<NoFeatureConfig>> OBSIDILITH_ARENA_STRUCTURE_FEATURE = STRUCTURE_FEATURES.register("obsidilith_arena", () -> new ObsidilithArenaStructureFeature(NoFeatureConfig.CODEC, config.obsidilithConfig));
//    public static final RegistryObject<Structure<NoFeatureConfig>> VOID_BLOSSOM_STRUCTURE_FEATURE = STRUCTURE_FEATURES.register("void_blossom", () -> new VoidBlossomArenaStructureFeature(NoFeatureConfig.CODEC));
//    public static final RegistryObject<Structure<NoFeatureConfig>> GAUNTLET_STRUCTURE_FEATURE = STRUCTURE_FEATURES.register("gauntlet_arena", () -> new GauntletArenaStructureFeature(NoFeatureConfig.CODEC));
//    public static final RegistryObject<Structure<NoFeatureConfig>> LICH_TOWER_STRUCTURE_FEATURE = STRUCTURE_FEATURES.register("lich_tower", () -> new LichTowerStructureFeature(NoFeatureConfig.CODEC));
//
//    public static final RegistryObject<StructurePieceType> OBSIDILITH_STRUCTURE_PIECE = STRUCTURE_PIECES.register("obsidilith_arena", () -> StructureFactories.OBSIDILITH_ARENA);
//    public static final RegistryObject<StructurePieceType> VOID_BLOSSOM_CAVERN_PIECE = STRUCTURE_PIECES.register("void_blossom_piece", () -> StructureFactories.VOID_BLOSSOM);
//    public static final RegistryObject<StructurePieceType> GAUNTLET_STRUCTURE_PIECE = STRUCTURE_PIECES.register("gauntlet_arena_piece", () -> StructureFactories.GAUNTLET_ARENA);
//    public static final RegistryObject<StructurePieceType> LICH_STRUCTURE_PIECE = STRUCTURE_PIECES.register("lich_tower_piece", () -> StructureFactories.LICH_TOWER);
//
//    public static final TagKey<StructureFeature<?, ?>> SOUL_STAR_STRUCTURE_KEY = TagKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation(BMDConstants.MOD_ID, "soul_star_target"));
//    public static final TagKey<StructureFeature<?, ?>> VOID_LILY_STRUCTURE_KEY = TagKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation(BMDConstants.MOD_ID, "void_lily_target"));
//
//    public static void register(IEventBus eventBus){
//        STRUCTURE_FEATURES.register(eventBus);
//        STRUCTURE_PIECES.register(eventBus);
//    }
//
//    private static class StructureFactories {
//        public static final StructurePieceType OBSIDILITH_ARENA = (context, tag) -> new BMDStructurePiece(context.structureManager(), tag,BMDStructures.OBSIDILITH_STRUCTURE_PIECE.get());
//        public static final StructurePieceType VOID_BLOSSOM = (context, tag) -> new CodeStructurePiece(BMDStructures.VOID_BLOSSOM_CAVERN_PIECE.get(), tag, new VoidBlossomCavernPieceGenerator());
//        public static final StructurePieceType GAUNTLET_ARENA = (context, tag) -> new BMDStructurePiece(context.structureManager(), tag, BMDStructures.GAUNTLET_STRUCTURE_PIECE.get());
//        public static final StructurePieceType LICH_TOWER = (context, tag) -> new BMDStructurePiece(context.structureManager(), tag, BMDStructures.LICH_STRUCTURE_PIECE.get());
//    }
}
