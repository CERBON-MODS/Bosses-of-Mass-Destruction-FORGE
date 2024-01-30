package com.cerbon.bosses_of_mass_destruction.neoforge.attachment;

import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.cerbon.cerbons_api.api.general.data.HistoricalData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

public class BMDAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, BMDConstants.MOD_ID);

    public static final Supplier<AttachmentType<HistoricalData<Vec3>>> HISTORICAL_DATA = ATTACHMENT_TYPES.register( "historical_data", () ->
            AttachmentType.builder(() -> new HistoricalData<>(Vec3.ZERO, 10)).build());

    public static List<Vec3> getPlayerPositions(ServerPlayer player) {
        return player.getData(HISTORICAL_DATA).getAll();
    }

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
