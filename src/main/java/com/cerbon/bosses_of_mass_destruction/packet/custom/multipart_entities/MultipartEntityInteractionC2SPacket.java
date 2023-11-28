package com.cerbon.bosses_of_mass_destruction.packet.custom.multipart_entities;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.client.PlayerInteractMultipartEntity;
import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.MultipartAwareEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MultipartEntityInteractionC2SPacket {
    private final int entityId;
    private final String part;
    private final InteractionHand hand;
    private final boolean isSneaking;
    private final PlayerInteractMultipartEntity.InteractionType interactionType;

    public MultipartEntityInteractionC2SPacket(int entityId, String part, InteractionHand hand, boolean isSneaking, PlayerInteractMultipartEntity.InteractionType interactionType){
        this.entityId = entityId;
        this.part = part;
        this.hand = hand;
        this.isSneaking = isSneaking;
        this.interactionType = interactionType;
    }

    public MultipartEntityInteractionC2SPacket(FriendlyByteBuf buf){
        this.entityId = buf.readInt();
        this.part = buf.readUtf(32767);
        this.hand = buf.readEnum(InteractionHand.class);
        this.isSneaking = buf.readBoolean();
        this.interactionType = buf.readEnum(PlayerInteractMultipartEntity.InteractionType.class);
}

    public void write(FriendlyByteBuf buf){
        buf.writeInt(entityId);
        buf.writeUtf(part);
        buf.writeEnum(hand);
        buf.writeBoolean(isSneaking);
        buf.writeEnum(interactionType);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer serverPlayer = ctx.getSender();
            if (serverPlayer == null) return;

            serverPlayer.setShiftKeyDown(isSneaking);
            ServerLevel serverLevel = serverPlayer.getLevel();
            Entity entity = serverLevel.getEntity(entityId);
            if (entity == null) return;

            if (interactionType == PlayerInteractMultipartEntity.InteractionType.INTERACT)
                entity.interact(serverPlayer, hand);

            else if (interactionType == PlayerInteractMultipartEntity.InteractionType.ATTACK){
                if (entity instanceof MultipartAwareEntity multipartAwareEntity)
                    multipartAwareEntity.setNextDamagedPart(part);

                serverPlayer.attack(entity);
            }else
                throw new RuntimeException();
        });
        ctx.setPacketHandled(true);
    }
}
