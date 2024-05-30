package com.cerbon.bosses_of_mass_destruction.mixin;

import com.cerbon.bosses_of_mass_destruction.block.custom.MobWardBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldEntitySpawner.class)
public class NaturalSpawnerMixin {

    @Inject(method = "isValidSpawnPostitionForType", at = @At("RETURN"), cancellable = true)
    private static void canSpawn(ServerWorld level, EntityClassification category, StructureManager structureManager, ChunkGenerator generator, MobSpawnInfo.Spawners data, BlockPos.Mutable pos, double distance, CallbackInfoReturnable<Boolean> cir){
        MobWardBlock.canSpawn(level, pos, cir);
    }
}
