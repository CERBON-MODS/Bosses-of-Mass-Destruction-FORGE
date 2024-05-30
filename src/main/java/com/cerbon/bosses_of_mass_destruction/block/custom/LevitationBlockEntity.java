package com.cerbon.bosses_of_mass_destruction.block.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.RandomUtils;
import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.VecUtils;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlockEntities;
import com.cerbon.bosses_of_mass_destruction.block.BMDBlocks;
import com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCache;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.config.BMDConfig;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.particle.ClientParticleBuilder;
import com.cerbon.bosses_of_mass_destruction.util.AnimationUtils;
import com.cerbon.bosses_of_mass_destruction.util.BMDColors;
import com.google.common.collect.Lists;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.*;
import java.util.stream.Collectors;

public class LevitationBlockEntity extends ChunkCacheBlockEntity implements IAnimatable, IAnimationTickable {
    private static final double tableOfElevationRadius = AutoConfig.getConfigHolder(BMDConfig.class).getConfig().generalConfig.tableOfElevationRadius;
    private AnimationFactory animationFactory;
    public int animationAge = 0;

    private static final HashSet<ServerPlayerEntity> flight = new HashSet<>();

    public LevitationBlockEntity() {
        super(BMDBlocks.LEVITATION_BLOCK.get(), BMDBlockEntities.LEVITATION_BLOCK_ENTITY.get());
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(
                new AnimationController<>(
                        this,
                        "idle",
                        0,
                        AnimationUtils.createIdlePredicate("rotate")
                )
        );
    }

    @Override
    public AnimationFactory getFactory() {
        if (animationFactory == null)
            animationFactory = new AnimationFactory(this);

        return animationFactory;
    }

    public static void tick(World level, BlockPos pos, BlockState state, LevitationBlockEntity entity){
        ChunkCacheBlockEntity.tick(level, pos, state, entity);
        if (level.isClientSide){
            entity.animationAge++;

            AxisAlignedBB box = getAffectingBox(level, VecUtils.asVec3(pos));
            List<PlayerEntity> playersInBox = level.getEntitiesOfClass(PlayerEntity.class, box);

            for (PlayerEntity player : playersInBox) {

                for (double x : Lists.newArrayList(box.minX, box.maxX)) {
                    double zRand = box.getCenter().z + box.getZsize() * RandomUtils.randomDouble(0.5);
                    Particles.particlesFactory.build(randYPos(x, player, zRand), Vector3d.ZERO);
                }

                for (double z : Lists.newArrayList(box.minZ, box.maxZ)) {
                    double xRand = box.getCenter().x + box.getXsize() * RandomUtils.randomDouble(0.5);
                    Particles.particlesFactory.build(randYPos(xRand, player, z), Vector3d.ZERO);
                }
            }
        }
    }

    private static Vector3d randYPos(double x, PlayerEntity player, double z){
        return new Vector3d(x, player.getY() + RandomUtils.randomDouble(0.5) + 1, z);
    }

    public static void tickFlight(ServerPlayerEntity player){
        List<BlockPos> blockToCheck = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++)
                blockToCheck.add(new BlockPos(x * (int) tableOfElevationRadius, 0, z * (int) tableOfElevationRadius));
        }
        Set<ChunkPos> chunksToCheck = blockToCheck.stream().map(pos -> new ChunkPos(pos.offset(player.blockPosition()))).collect(Collectors.toSet());
        boolean hasLevitationBlock = chunksToCheck.stream().anyMatch(chunkPos -> {
            Optional<ChunkBlockCache> blockCache = BMDCapabilities.getChunkBlockCache(player.level);

            if (blockCache.isPresent()){
                List<BlockPos> blocks = blockCache.get().getBlocksFromChunk(chunkPos, BMDBlocks.LEVITATION_BLOCK.get());
                return blocks.stream().anyMatch(pos -> getAffectingBox(player.level, VecUtils.asVec3(pos)).contains(player.position()));
            } else
                return false;
        });

        /*
          Known bugs:
          - does not have persistence (e.g. loading why flying will fall next time it is opened, though without fall damage)
          - does not handle changing gamemodes that change these abilities (e.g. changing to survival or losing the flying ability
          from taking another modded item off will not work as expected.)
          - However, it will be more-or-less able to work with other mods that mess with the flight ability provided that
          they do not set it every tick
         */
        if (!hasLevitationBlock){
            if (flight.contains(player)) {
                if (!player.isCreative() && !player.isSpectator()) {
                    player.abilities.mayfly = false;
                    player.abilities.flying = false;
                    player.connection.send(new SPlayerAbilitiesPacket(player.abilities));
                }
                flight.remove(player);
            }
        } else if (!flight.contains(player)){
            flight.add(player);
            if (!player.abilities.mayfly){
                player.abilities.mayfly = true;
                player.connection.send(new SPlayerAbilitiesPacket(player.abilities));
            }
        }
    }

    private static AxisAlignedBB getAffectingBox(World level, Vector3d pos){
        return new AxisAlignedBB(pos.x, 0, pos.z, (pos.x + 1), level.getHeight(), (pos.z + 1)).inflate(tableOfElevationRadius, 0.0, tableOfElevationRadius);
    }

    @Override
    public void tick() {}

    @Override
    public int tickTimer() {
        return animationAge;
    }

    private static class Particles {
        private static final ClientParticleBuilder particlesFactory = new ClientParticleBuilder(BMDParticles.LINE.get())
                .color(BMDColors.COMET_BLUE)
                .brightness(BMDParticles.FULL_BRIGHT)
                .colorVariation(0.5)
                .scale(0.075f);
    }
}
