package com.cerbon.bosses_of_mass_destruction.structure.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;

import java.util.Random;

public class BMDStructurePiece extends TemplateStructurePiece {
    public BMDStructurePiece(IStructurePieceType p_i51338_1_, int p_i51338_2_) {
        super(p_i51338_1_, p_i51338_2_);
    }

    @Override
    protected void handleDataMarker(String pFunction, BlockPos pPos, IServerWorld pLevel, Random pRandom, MutableBoundingBox pSbb) {

    }
//    private final Rotation rot;
//
//    public BMDStructurePiece(TemplateManager manager, CompoundNBT tag, StructurePieceType type) {
//        super(
//                type,
//                tag,
//                manager,
//                resourceLocation -> new PlacementSettings()
//                        .setRotation(Rotation.valueOf(tag.getString("Rot")))
//                        .setMirror(Mirror.NONE)
//                        .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK)
//        );
//        this.rot = Rotation.valueOf(tag.getString("Rot"));
//    }
//
//    public BMDStructurePiece(TemplateManager structureManager, BlockPos pos, ResourceLocation template, Rotation rotation, StructurePieceType type) {
//        super(
//                type, 0, structureManager, template, template.toString(),
//                new PlacementSettings()
//                        .setRotation(rotation)
//                        .setMirror(Mirror.NONE)
//                        .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK), pos
//        );
//        this.rot = rotation;
//    }
//
//    @Override
//    protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundNBT tag) {
//        super.addAdditionalSaveData(context, tag);
//        tag.putString("Rot", this.rot.name());
//    }
//
//    @Override
//    protected void handleDataMarker(String marker, BlockPos pos, IServerWorld level, Random random, MutableBoundingBox box) {}
}
