package com.cerbon.bosses_of_mass_destruction.structure;

import com.cerbon.bosses_of_mass_destruction.structure.util.CodeStructurePiece;
import com.cerbon.bosses_of_mass_destruction.structure.void_blossom_cavern.VoidBlossomCavernPieceGenerator;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;

public class BMDStructurePieces {
    public static IStructurePieceType VOID_BLOSSOM_PIECE = null;

    public static void registerStructurePieces() {
        VOID_BLOSSOM_PIECE = IStructurePieceType.setPieceId((templateManager, tag) -> new CodeStructurePiece(BMDStructurePieces.VOID_BLOSSOM_PIECE, tag, new VoidBlossomCavernPieceGenerator()), "void_blossom");
    }
}
