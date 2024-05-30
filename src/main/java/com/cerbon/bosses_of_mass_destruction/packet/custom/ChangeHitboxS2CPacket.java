package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet.GauntletEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ChangeHitboxS2CPacket {
    private final int entityId;
    private final boolean open;

    public ChangeHitboxS2CPacket(int entityId, boolean open) {
        this.entityId = entityId;
        this.open = open;
    }

    public ChangeHitboxS2CPacket(PacketBuffer buf) {
        this.entityId = buf.readInt();
        this.open = buf.readBoolean();
    }

    public void write(PacketBuffer buf){
        buf.writeInt(entityId);
        buf.writeBoolean(open);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            ClientWorld level = client.level;
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
