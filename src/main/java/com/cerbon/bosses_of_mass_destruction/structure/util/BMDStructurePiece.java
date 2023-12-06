package com.cerbon.bosses_of_mass_destruction.structure.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BMDStructurePiece extends TemplateStructurePiece {
    private final Rotation rot;

    public BMDStructurePiece(StructureManager manager, CompoundTag tag, StructurePieceType type) {
        super(
                type,
                tag,
                manager,
                resourceLocation -> new StructurePlaceSettings()
                        .setRotation(Rotation.valueOf(tag.getString("Rot")))
                        .setMirror(Mirror.NONE)
                        .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
        );
        this.rot = Rotation.valueOf(tag.getString("Rot"));
    }

    public BMDStructurePiece(StructureManager structureManager, BlockPos pos, ResourceLocation template, Rotation rotation, StructurePieceType type) {
        super(
                type, 0, structureManager, template, template.toString(),
                new StructurePlaceSettings()
                        .setRotation(rotation)
                        .setMirror(Mirror.NONE)
                        .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK), pos
        );
        this.rot = rotation;
    }

    @Override
    protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext context, @NotNull CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putString("Rot", this.rot.name());
    }

    @Override
    protected void handleDataMarker(String marker, BlockPos pos, ServerLevelAccessor level, Random random, BoundingBox box) {}
}
