package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet.GauntletEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;

public class ChangeHitboxS2CPacket {
    private final int entityId;
    private final boolean open;

    public ChangeHitboxS2CPacket(int entityId, boolean open) {
        this.entityId = entityId;
        this.open = open;
    }

    public ChangeHitboxS2CPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.open = buf.readBoolean();
    }

    public void write(FriendlyByteBuf buf){
        buf.writeInt(entityId);
        buf.writeBoolean(open);
    }

    public void handle(CustomPayloadEvent.Context ctx){
        ctx.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            ClientLevel level = client.level;
            if (level == null) return;

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client.execute(() -> {
                Entity entity = level.getEntity(entityId);
                if (entity instanceof GauntletEntity)
                    if (open) ((GauntletEntity) entity).hitboxHelper.setOpenHandHitbox(); else ((GauntletEntity) entity).hitboxHelper.setClosedFistHitbox();
            }));
        });
        ctx.setPacketHandled(true);
    }
}