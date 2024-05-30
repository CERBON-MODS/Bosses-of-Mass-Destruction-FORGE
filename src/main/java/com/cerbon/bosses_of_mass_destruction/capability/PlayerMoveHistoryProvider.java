package com.cerbon.bosses_of_mass_destruction.capability;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.data.HistoricalData;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerMoveHistoryProvider implements ICapabilityProvider {
    @CapabilityInject(HistoricalData.class)
    public static final Capability<HistoricalData<Vector3d>> HISTORICAL_DATA = null;

    private HistoricalData<Vector3d> positionalHistory;
    private final LazyOptional<HistoricalData<Vector3d>> optional = LazyOptional.of(this::createHistoricalData);

    private HistoricalData<Vector3d> createHistoricalData(){
        if(this.positionalHistory == null)
            this.positionalHistory = new HistoricalData<>(Vector3d.ZERO, 10);

        return this.positionalHistory;
    }

    @Override
    public @Nonnull <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if(cap == HISTORICAL_DATA)
            return optional.cast();

        return LazyOptional.empty();
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(HistoricalData.class, new Capability.IStorage<HistoricalData>() {

            @Nullable
            @Override
            public INBT writeNBT(Capability<HistoricalData> capability, HistoricalData instance, Direction side) {return null;}

            @Override
            public void readNBT(Capability<HistoricalData> capability, HistoricalData instance, Direction side, INBT nbt) {}

        }, () -> new HistoricalData<>(Vector3d.ZERO, 10));
    }
}
