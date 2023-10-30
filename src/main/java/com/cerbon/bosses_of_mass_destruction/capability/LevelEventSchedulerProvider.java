package com.cerbon.bosses_of_mass_destruction.capability;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LevelEventSchedulerProvider implements ICapabilityProvider {
    public static Capability<EventScheduler> EVENT_SCHEDULER = CapabilityManager.get(new CapabilityToken<>() {});

    private EventScheduler eventScheduler = null;
    private final LazyOptional<EventScheduler> optional = LazyOptional.of(this::createEventScheduler);

    private EventScheduler createEventScheduler(){
        if (this.eventScheduler == null)
            this.eventScheduler = new EventScheduler();

        return this.eventScheduler;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == EVENT_SCHEDULER)
            return optional.cast();

        return LazyOptional.empty();
    }
}
