package com.cerbon.bosses_of_mass_destruction.capability;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LevelEventSchedulerProvider implements ICapabilityProvider {
    @CapabilityInject(EventScheduler.class)
    public static final Capability<EventScheduler> EVENT_SCHEDULER = null;

    private EventScheduler eventScheduler;
    private final LazyOptional<EventScheduler> optional = LazyOptional.of(this::createEventScheduler);

    private EventScheduler createEventScheduler(){
        if (this.eventScheduler == null)
            this.eventScheduler = new EventScheduler();

        return this.eventScheduler;
    }

    @Override
    public @Nonnull <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == EVENT_SCHEDULER)
            return optional.cast();

        return LazyOptional.empty();
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(EventScheduler.class, new Capability.IStorage<EventScheduler>() {

            @Nullable
            @Override
            public INBT writeNBT(Capability<EventScheduler> capability, EventScheduler instance, Direction side) {return null;}

            @Override
            public void readNBT(Capability<EventScheduler> capability, EventScheduler instance, Direction side, INBT nbt) {}

        }, EventScheduler::new);
    }
}
