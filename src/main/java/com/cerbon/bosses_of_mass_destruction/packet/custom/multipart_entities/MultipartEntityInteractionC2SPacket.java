package com.cerbon.bosses_of_mass_destruction.packet.custom.multipart_entities;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.client.PlayerInteractMultipartEntity;
import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.MultipartAwareEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MultipartEntityInteractionC2SPacket {
    private final int entityId;
    private final String part;
    private final Hand hand;
    private final boolean isSneaking;
    private final PlayerInteractMultipartEntity.InteractionType interactionType;

    public MultipartEntityInteractionC2SPacket(int entityId, String part, Hand hand, boolean isSneaking, PlayerInteractMultipartEntity.InteractionType interactionType){
        this.entityId = entityId;
        this.part = part;
        this.hand = hand;
        this.isSneaking = isSneaking;
        this.interactionType = interactionType;
    }

    public MultipartEntityInteractionC2SPacket(PacketBuffer buf){
        this.entityId = buf.readInt();
        this.part = buf.readUtf(32767);
        this.hand = buf.readEnum(Hand.class);
        this.isSneaking = buf.readBoolean();
        this.interactionType = buf.readEnum(PlayerInteractMultipartEntity.InteractionType.class);
}

    public void write(PacketBuffer buf){
        buf.writeInt(entityId);
        buf.writeUtf(part);
        buf.writeEnum(hand);
        buf.writeBoolean(isSneaking);
        buf.writeEnum(interactionType);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayerEntity serverPlayer = ctx.getSender();
            if (serverPlayer == null) return;

            serverPlayer.setShiftKeyDown(isSneaking);
            ServerWorld serverLevel = serverPlayer.getLevel();
            Entity entity = serverLevel.getEntity(entityId);
            if (entity == null) return;

            if (interactionType == PlayerInteractMultipartEntity.InteractionType.INTERACT)
                entity.interact(serverPlayer, hand);

            else if (interactionType == PlayerInteractMultipartEntity.InteractionType.ATTACK){
                if (entity instanceof MultipartAwareEntity) {
                    MultipartAwareEntity multipartAwareEntity = (MultipartAwareEntity) entity;
                    multipartAwareEntity.setNextDamagedPart(part);
                }

                serverPlayer.attack(entity);
            }else
                throw new RuntimeException();
        });
        ctx.setPacketHandled(true);
    }
}
