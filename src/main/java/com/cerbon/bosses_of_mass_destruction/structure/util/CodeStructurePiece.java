package com.cerbon.bosses_of_mass_destruction.structure.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class CodeStructurePiece extends StructurePiece implements IStructurePiece {
    private final IPieceGenerator pieceGenerator;

    public CodeStructurePiece(StructurePieceType type, BoundingBox boundingBox, IPieceGenerator structurePieceData) {
        super(type, 0, boundingBox);
        this.pieceGenerator = structurePieceData;
        setOrientation(Direction.NORTH);
    }

    public CodeStructurePiece(StructurePieceType type, CompoundTag tag, IPieceGenerator structurePieceData) {
        super(type, tag);
        this.pieceGenerator = structurePieceData;
        setOrientation(Direction.NORTH);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext context, @NotNull CompoundTag tag) {}


    @Override
    public void postProcess(@NotNull WorldGenLevel level, @NotNull StructureFeatureManager structureManager, @NotNull ChunkGenerator generator, @NotNull Random random, @NotNull BoundingBox box, @NotNull ChunkPos chunkPos, @NotNull BlockPos pos) {
        pieceGenerator.generate(
                level,
                structureManager,
                generator,
                random,
                box,
                chunkPos,
                pos,
                this
        );
    }

    @Override
    public void placeBlock(WorldGenLevel level, BlockState block, BlockPos pos, BoundingBox box) {
        super.placeBlock(level, block, pos.getX(), pos.getY(), pos.getZ(), box);
    }

    @Override
    public @NotNull Rotation getRotation() {
        return Rotation.NONE;
    }

    @Override
    public @NotNull Mirror getMirror() {
        return Mirror.NONE;
    }

    @Nullable
    @Override
    public Direction getOrientation() {
        return null;
    }
}
