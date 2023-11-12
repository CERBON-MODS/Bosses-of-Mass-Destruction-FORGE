package com.cerbon.bosses_of_mass_destruction.packet.custom.multipart_entities;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.client.PlayerInteractMultipartEntity;
import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.MultipartAwareEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MultipartEntityInteractC2SPacket {
    private final InteractionHand hand;
    private MultipartAwareEntity entity;
    private final int entityId;

    private String part;
    private PlayerInteractMultipartEntity.InteractionType interactionType;
    private boolean isSneaking;

    public MultipartEntityInteractC2SPacket(InteractionHand hand, final MultipartAwareEntity entity){
        this.hand = hand;
        this.entity = entity;
        this.entityId = ((Entity) entity).getId();
    }

    public MultipartEntityInteractC2SPacket(FriendlyByteBuf buf){
        this.entityId = buf.readVarInt();
        this.part = buf.readUtf(32767);
        this.interactionType = buf.readEnum(PlayerInteractMultipartEntity.InteractionType.class);
        this.hand = buf.readEnum(InteractionHand.class);
        this.isSneaking = buf.readBoolean();
    }

    public void write(FriendlyByteBuf buf){
        Minecraft client = Minecraft.getInstance();
        final Vec3 pos = client.cameraEntity.getEyePosition(client.getFrameTime());
        final Vec3 dir = client.cameraEntity.getViewVector(client.getFrameTime());
        final double reach = client.gameMode.getPickRange();
        this.part = entity.getBounds().raycast(pos, pos.add(dir.multiply(reach, reach, reach)));

        if (part != null) {
            buf.writeVarInt(entityId);
            buf.writeUtf(part);
            buf.writeEnum(PlayerInteractMultipartEntity.InteractionType.INTERACT);
            buf.writeEnum(hand);
            buf.writeBoolean(client.cameraEntity.isShiftKeyDown());
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer serverPlayer = ctx.getSender();
            if (serverPlayer == null) return;

            serverPlayer.setShiftKeyDown(isSneaking);
            final ServerLevel serverLevel = serverPlayer.serverLevel();
            final Entity entity1 = serverLevel.getEntity(entityId);
            if (entity1 == null) return;

            if (interactionType == PlayerInteractMultipartEntity.InteractionType.INTERACT) {
                entity1.interact(serverPlayer, hand);
            }else
                throw new RuntimeException();
        });
        ctx.setPacketHandled(true);
    }
}
