package com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern;

import com.cerbon.bosses_of_mass_destruction.structure.util.CodeStructurePiece;
import com.cerbon.bosses_of_mass_destruction.structure.BMDStructures;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class VoidBlossomArenaStructureFeature extends Structure {
    public static final MapCodec<VoidBlossomArenaStructureFeature> CODEC = simpleCodec(VoidBlossomArenaStructureFeature::new);

    protected VoidBlossomArenaStructureFeature(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(@NotNull GenerationContext context) {
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, collector -> addPieces(collector, context));
    }

    @Override
    public @NotNull StructureType<?> type() {
        return BMDStructures.VOID_BLOSSOM_STRUCTURE_TYPE.get();
    }

    public static void addPieces(StructurePiecesBuilder collector, GenerationContext context){
        int x = context.chunkPos().getMinBlockX();
        int z = context.chunkPos().getMinBlockZ();
        int y = 35 + context.chunkGenerator().getMinY();

        collector.addPiece(
                new CodeStructurePiece(
                        BMDStructures.VOID_BLOSSOM_CAVERN_PIECE.get(),
                        new BoundingBox(new BlockPos(x, y, z)).inflatedBy(32),
                        new VoidBlossomCavernPieceGenerator()
                )
        );
    }
}
