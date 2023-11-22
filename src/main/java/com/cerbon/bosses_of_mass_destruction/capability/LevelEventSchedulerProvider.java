package com.cerbon.bosses_of_mass_destruction.capability;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AutoRegisterCapability
public class LevelEventSchedulerProvider implements ICapabilityProvider {
    public static final Capability<EventScheduler> EVENT_SCHEDULER = CapabilityManager.get(new CapabilityToken<>() {});

    private EventScheduler eventScheduler;
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
