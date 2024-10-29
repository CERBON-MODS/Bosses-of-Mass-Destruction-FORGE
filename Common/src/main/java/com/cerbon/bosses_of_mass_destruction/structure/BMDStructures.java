package com.cerbon.bosses_of_mass_destruction.structure;

import com.cerbon.bosses_of_mass_destruction.structure.util.CodeStructurePiece;
import com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern.VoidBlossomArenaStructureFeature;
import com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern.VoidBlossomCavernPieceGenerator;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.cerbons_api.api.registry.RegistryEntry;
import com.cerbon.cerbons_api.api.registry.ResourcefulRegistries;
import com.cerbon.cerbons_api.api.registry.ResourcefulRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class BMDStructures {
    public static final ResourcefulRegistry<StructurePieceType> STRUCTURE_PIECES = ResourcefulRegistries.create(BuiltInRegistries.STRUCTURE_PIECE, BMDConstants.MOD_ID);
    public static final ResourcefulRegistry<StructureType<?>> STRUCTURE_TYPES = ResourcefulRegistries.create(BuiltInRegistries.STRUCTURE_TYPE, BMDConstants.MOD_ID);

    public static final StructureRegister OBSIDILITH_STRUCTURE_REGISTRY = new StructureRegister(ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "obsidilith_arena"));
    public static final StructureRegister VOID_BLOSSOM_STRUCTURE_REGISTRY = new StructureRegister(ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "void_blossom"));
    public static final StructureRegister GAUNTLET_STRUCTURE_REGISTRY = new StructureRegister(ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "gauntlet_arena"));
    public static final StructureRegister LICH_STRUCTURE_REGISTRY = new StructureRegister(ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "lich_tower"));

    public static final RegistryEntry<StructureType<VoidBlossomArenaStructureFeature>> VOID_BLOSSOM_STRUCTURE_TYPE = STRUCTURE_TYPES.register("void_blossom", () -> Registry.register(BuiltInRegistries.STRUCTURE_TYPE, ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "void_blossom"), () -> VoidBlossomArenaStructureFeature.CODEC));
    public static final RegistryEntry<StructurePieceType> VOID_BLOSSOM_CAVERN_PIECE = STRUCTURE_PIECES.register("void_blossom_piece", () -> StructureFactories.VOID_BLOSSOM);

    public static final TagKey<Structure> SOUL_STAR_STRUCTURE_KEY = TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "soul_star_target"));
    public static final TagKey<Structure> VOID_LILY_STRUCTURE_KEY = TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(BMDConstants.MOD_ID, "void_lily_target"));

    public static void register() {
        STRUCTURE_TYPES.register();
        STRUCTURE_PIECES.register();
    }

    private static class StructureFactories {
        public static final StructurePieceType VOID_BLOSSOM = (context, tag) -> new CodeStructurePiece(BMDStructures.VOID_BLOSSOM_CAVERN_PIECE.get(), tag, new VoidBlossomCavernPieceGenerator());
    }
}
