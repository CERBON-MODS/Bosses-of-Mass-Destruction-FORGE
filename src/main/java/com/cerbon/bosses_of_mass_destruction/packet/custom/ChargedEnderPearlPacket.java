package com.cerbon.bosses_of_mass_destruction.packet.custom;

import com.cerbon.bosses_of_mass_destruction.item.custom.ChargedEnderPearlEntity;
import com.cerbon.bosses_of_mass_destruction.util.maelstrom.static_utilities.PacketUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ChargedEnderPearlPacket {
    private final Vec3 pos;

    public ChargedEnderPearlPacket(Vec3 pos){
        this.pos = pos;
    }

    public ChargedEnderPearlPacket(FriendlyByteBuf buf){
        this.pos = PacketUtils.readVec3(buf);
    }

    public void write(FriendlyByteBuf buf){
        PacketUtils.writeVec3(buf, this.pos);
    }
    
    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ChargedEnderPearlEntity.handlePearlImpact(this.pos));
        });
        ctx.setPacketHandled(true);
    }
}
