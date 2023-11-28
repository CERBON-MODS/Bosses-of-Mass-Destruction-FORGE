package com.cerbon.bosses_of_mass_destruction.structure;

import com.cerbon.bosses_of_mass_destruction.structure.util.CodeStructurePiece;
import com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern.VoidBlossomArenaStructureFeature;
import com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern.VoidBlossomCavernPieceGenerator;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class BMDStructures {
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES = DeferredRegister.create(Registry.STRUCTURE_PIECE_REGISTRY, BMDConstants.MOD_ID);
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registry.STRUCTURE_TYPE_REGISTRY, BMDConstants.MOD_ID);

    public static final StructureRegister OBSIDILITH_STRUCTURE_REGISTRY = new StructureRegister(new ResourceLocation(BMDConstants.MOD_ID, "obsidilith_arena"));
    public static final StructureRegister VOID_BLOSSOM_STRUCTURE_REGISTRY = new StructureRegister(new ResourceLocation(BMDConstants.MOD_ID, "void_blossom"));
    public static final StructureRegister GAUNTLET_STRUCTURE_REGISTRY = new StructureRegister(new ResourceLocation(BMDConstants.MOD_ID, "gauntlet_arena"));
    public static final StructureRegister LICH_STRUCTURE_REGISTRY = new StructureRegister(new ResourceLocation(BMDConstants.MOD_ID, "lich_tower"));

    public static final RegistryObject<StructureType<VoidBlossomArenaStructureFeature>> VOID_BLOSSOM_STRUCTURE_TYPE = STRUCTURE_TYPES.register("void_blossom", () -> Registry.register(Registry.STRUCTURE_TYPES, new ResourceLocation(BMDConstants.MOD_ID, "void_blossom"), () -> VoidBlossomArenaStructureFeature.CODEC));
    public static final RegistryObject<StructurePieceType> VOID_BLOSSOM_CAVERN_PIECE = STRUCTURE_PIECES.register("void_blossom_piece", () -> StructureFactories.VOID_BLOSSOM);

    public static final TagKey<Structure> SOUL_STAR_STRUCTURE_KEY = TagKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation(BMDConstants.MOD_ID, "soul_star_target"));
    public static final TagKey<Structure> VOID_LILY_STRUCTURE_KEY = TagKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation(BMDConstants.MOD_ID, "void_lily_target"));

    public static void register(IEventBus eventBus){
        STRUCTURE_TYPES.register(eventBus);
        STRUCTURE_PIECES.register(eventBus);
    }

    private static class StructureFactories {
        public static final StructurePieceType VOID_BLOSSOM = (context, tag) -> new CodeStructurePiece(BMDStructures.VOID_BLOSSOM_CAVERN_PIECE.get(), tag, new VoidBlossomCavernPieceGenerator());
    }
}
