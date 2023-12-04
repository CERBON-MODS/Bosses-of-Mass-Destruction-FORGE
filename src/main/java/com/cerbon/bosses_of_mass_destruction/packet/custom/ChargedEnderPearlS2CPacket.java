package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.static_utilities.PacketUtils;
import com.cerbon.bosses_of_mass_destruction.item.custom.ChargedEnderPearlEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;

import java.util.function.Supplier;

public class ChargedEnderPearlS2CPacket {
    private final Vec3 pos;

    public ChargedEnderPearlS2CPacket(Vec3 pos){
        this.pos = pos;
    }

    public ChargedEnderPearlS2CPacket(FriendlyByteBuf buf){
        this.pos = PacketUtils.readVec3(buf);
    }

    public void write(FriendlyByteBuf buf){
        PacketUtils.writeVec3(buf, this.pos);
    }
    
    public void handle(Supplier<CustomPayloadEvent.Context> supplier){
        CustomPayloadEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            ClientLevel level = client.level;
            if (level == null) return;

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client.execute(() -> ChargedEnderPearlEntity.handlePearlImpact(this.pos)));
        });
        ctx.setPacketHandled(true);
    }
}
