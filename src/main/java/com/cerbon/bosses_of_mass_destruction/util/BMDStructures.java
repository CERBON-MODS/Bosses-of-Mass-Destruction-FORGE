package com.cerbon.bosses_of_mass_destruction.util;

import com.cerbon.bosses_of_mass_destruction.structure.VoidBlossomArenaStructureFeature;
import com.cerbon.bosses_of_mass_destruction.structure.VoidBlossomCavernPieceGenerator;
import com.cerbon.bosses_of_mass_destruction.structure.util.CodeStructurePiece;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class BMDStructures {
    public static final StructureRegister OBSIDILITH_STRUCTURE_REGISTRY = new StructureRegister(new ResourceLocation(BMDConstants.MOD_ID, "obsidilith_arena"));
    public static final StructureRegister GAUNTLET_STRUCTURE_REGISTRY = new StructureRegister(new ResourceLocation(BMDConstants.MOD_ID, "gauntlet_arena"));
    public static final StructureRegister LICH_STRUCTURE_REGISTRY = new StructureRegister(new ResourceLocation(BMDConstants.MOD_ID, "lich_tower"));

    public static final TagKey<Structure> SOUL_STAR_STRUCTURE_KEY = TagKey.create(Registries.STRUCTURE, new ResourceLocation(BMDConstants.MOD_ID, "soul_star_target"));
    public static final TagKey<Structure> VOID_LILY_STRUCTURE_KEY = TagKey.create(Registries.STRUCTURE, new ResourceLocation(BMDConstants.MOD_ID, "void_lily_target"));

    public static final StructurePieceType VOID_BLOSSOM_CAVERN_PIECE = Registry.register(BuiltInRegistries.STRUCTURE_PIECE, new ResourceLocation(BMDConstants.MOD_ID, "void_blossom_piece"), StructureFactories.VOID_BLOSSOM);
    public static final StructureType<VoidBlossomArenaStructureFeature> VOID_BLOSSOM_STRUCTURE_TYPE = Registry.register(BuiltInRegistries.STRUCTURE_TYPE, new ResourceLocation(BMDConstants.MOD_ID, "void_blossom"), () -> VoidBlossomArenaStructureFeature.CODEC);
    public static final StructureRegister VOID_BLOSSOM_STRUCTURE_REGISTRY = new StructureRegister(new ResourceLocation(BMDConstants.MOD_ID, "void_blossom"));

    private static class StructureFactories {
        public static final StructurePieceType VOID_BLOSSOM = (context, tag) -> new CodeStructurePiece(BMDStructures.VOID_BLOSSOM_CAVERN_PIECE, tag, new VoidBlossomCavernPieceGenerator());
    }
}
