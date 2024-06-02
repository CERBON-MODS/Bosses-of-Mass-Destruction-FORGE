package com.cerbon.bosses_of_mass_destruction.structure.util;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;

import javax.annotation.Nullable;
import java.util.Random;

public class CodeStructurePiece extends StructurePiece implements IStructurePiece {

    private final IPieceGenerator pieceGenerator;

    public CodeStructurePiece(IStructurePieceType structurePieceType, CompoundNBT tag, IPieceGenerator pieceGenerator) {
        super(structurePieceType, tag);
        this.pieceGenerator = pieceGenerator;
        setOrientation(Direction.NORTH);
    }

    public CodeStructurePiece(IStructurePieceType structurePieceType, MutableBoundingBox mutableBoundingBox, IPieceGenerator pieceGenerator) {
        super(structurePieceType, 0);
        this.pieceGenerator = pieceGenerator;
        this.boundingBox = mutableBoundingBox;
        setOrientation(Direction.NORTH);
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT tag) {}

    @Override
    public boolean postProcess(ISeedReader level, StructureManager structureManager, ChunkGenerator chunkGenerator, Random random, MutableBoundingBox box, ChunkPos chunkPos, BlockPos pos) {
        return pieceGenerator.generate(
                level,
                structureManager,
                chunkGenerator,
                random,
                box,
                chunkPos,
                pos,
                this
        );
    }

    @Override
    public void placeBlock(ISeedReader level, BlockState block, BlockPos pos, MutableBoundingBox box) {
        super.placeBlock(level, block, pos.getX(), pos.getY(), pos.getZ(), box);
    }

    @Override
    public Rotation getRotation() {
        return Rotation.NONE;
    }

    @Nullable
    @Override
    public Direction getOrientation() {
        return null;
    }
}
