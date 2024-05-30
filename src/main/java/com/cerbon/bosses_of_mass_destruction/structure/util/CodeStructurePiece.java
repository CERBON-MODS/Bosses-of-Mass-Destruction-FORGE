package com.cerbon.bosses_of_mass_destruction.structure.util;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;

import java.util.Random;

public class CodeStructurePiece extends StructurePiece implements IStructurePiece {
    protected CodeStructurePiece(IStructurePieceType p_i51342_1_, int p_i51342_2_) {
        super(p_i51342_1_, p_i51342_2_);
    }

    @Override
    public void placeBlock(ISeedReader level, BlockState block, BlockPos pos, MutableBoundingBox box) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {

    }

    @Override
    public boolean postProcess(ISeedReader pLevel, StructureManager pStructureManager, ChunkGenerator pChunkGenerator, Random pRandom, MutableBoundingBox pBox, ChunkPos pChunkPos, BlockPos pPos) {
        return false;
    }
//    private final IPieceGenerator pieceGenerator;
//
//    public CodeStructurePiece(StructurePieceType type, MutableBoundingBox boundingBox, IPieceGenerator structurePieceData) {
//        super(type, 0, boundingBox);
//        this.pieceGenerator = structurePieceData;
//        setOrientation(Direction.NORTH);
//    }
//
//    public CodeStructurePiece(StructurePieceType type, CompoundNBT tag, IPieceGenerator structurePieceData) {
//        super(type, tag);
//        this.pieceGenerator = structurePieceData;
//        setOrientation(Direction.NORTH);
//    }
//
//    @Override
//    protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundNBT tag) {}
//
//
//    @Override
//    public void postProcess(@Nonnull ISeedReader level, @Nonnull StructureManager structureManager, @Nonnull ChunkGenerator generator, @Nonnull Random random, @Nonnull MutableBoundingBox box, @Nonnull ChunkPos chunkPos, @Nonnull BlockPos pos) {
//        pieceGenerator.generate(
//                level,
//                structureManager,
//                generator,
//                random,
//                box,
//                chunkPos,
//                pos,
//                this
//        );
//    }
//
//    @Override
//    public void placeBlock(ISeedReader level, BlockState block, BlockPos pos, MutableBoundingBox box) {
//        super.placeBlock(level, block, pos.getX(), pos.getY(), pos.getZ(), box);
//    }
//
//    @Override
//    public @Nonnull Rotation getRotation() {
//        return Rotation.NONE;
//    }
//
//    @Override
//    public @Nonnull Mirror getMirror() {
//        return Mirror.NONE;
//    }
//
//    @Nullable
//    @Override
//    public Direction getOrientation() {
//        return null;
//    }
}
