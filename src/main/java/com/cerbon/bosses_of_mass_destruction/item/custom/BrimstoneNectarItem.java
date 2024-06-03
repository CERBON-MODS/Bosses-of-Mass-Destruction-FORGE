package com.cerbon.bosses_of_mass_destruction.item.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.TimedEvent;
import com.cerbon.bosses_of_mass_destruction.capability.util.BMDCapabilities;
import com.cerbon.bosses_of_mass_destruction.packet.BMDPacketHandler;
import com.cerbon.bosses_of_mass_destruction.packet.custom.SendVec3S2CPacket;
import com.cerbon.bosses_of_mass_destruction.sound.BMDSounds;
import com.cerbon.bosses_of_mass_destruction.structure.structure_repair.StructureRepair;
import com.cerbon.bosses_of_mass_destruction.util.BMDUtils;
import com.cerbon.bosses_of_mass_destruction.util.VecId;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class BrimstoneNectarItem extends Item {
    private final List<StructureRepair> structureRepairs;

    public BrimstoneNectarItem(Properties properties, List<StructureRepair> structureRepairs) {
        super(properties);
        this.structureRepairs = structureRepairs;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World level, List<ITextComponent> tooltipComponents, @Nonnull ITooltipFlag isAdvanced) {
        tooltipComponents.add(new TranslationTextComponent("item.bosses_of_mass_destruction.brimstone_nectar.tooltip").withStyle(TextFormatting.DARK_GRAY));
    }

    @Override
    public @Nonnull ActionResult<ItemStack> use(World level, PlayerEntity user, @Nonnull Hand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (!level.isClientSide && level instanceof ServerWorld) {
            ServerWorld serverLevel = (ServerWorld) level;

            BlockPos usePos = user.blockPosition();
            List<StructureRepair> structuresToRepair = findStructuresToRepair(serverLevel, usePos);

            if (!structuresToRepair.isEmpty()){
                scheduleStructureRepair(serverLevel, structuresToRepair, usePos);
                playSound(serverLevel, user);
                user.getCooldowns().addCooldown(this, 80);
                BMDPacketHandler.sendToAllPlayersTrackingChunk(new SendVec3S2CPacket(user.position(), VecId.BrimstoneParticleEffect.ordinal()), serverLevel, user.position());

                if (!user.abilities.instabuild)
                    itemStack.shrink(1);

                user.awardStat(Stats.ITEM_USED.get(this));
                return ActionResult.sidedSuccess(itemStack, false);
            }
        }

        return ActionResult.pass(itemStack);
    }

    private List<StructureRepair> findStructuresToRepair(ServerWorld level, BlockPos usePos){
        return structureRepairs.stream().filter(structure -> {
            StructureStart structureStart = getStructureStart(level, usePos, structure);
            return structureStart.isValid() && structure.shouldRepairStructure(level, structureStart);
        }).collect(Collectors.toList());
    }

    private void scheduleStructureRepair(ServerWorld level, List<StructureRepair> structureToRepair, BlockPos usePos){
        BMDCapabilities.getLevelEventScheduler(level).addEvent(
                new TimedEvent(
                        () -> structureToRepair.forEach(structure -> structure.repairStructure(level, getStructureStart(level, usePos, structure))),
                        30
        ));
    }

    private StructureStart<?> getStructureStart(ServerWorld level, BlockPos blockPos, StructureRepair it){
        return level.structureFeatureManager().getStructureAt(blockPos, false, level.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).getOrThrow(it.associatedStructure()).feature);
    }

    private void playSound(World level, PlayerEntity user){
        level.playSound(
                null,
                user.getX(),
                user.getY(),
                user.getZ(),
                BMDSounds.BRIMSTONE.get(),
                SoundCategory.NEUTRAL,
                1.0f,
                BMDUtils.randomPitch(level.getRandom())
        );
    }
}
