package com.cerbon.bosses_of_mass_destruction.capability;

import com.cerbon.cerbons_api.api.general.data.HistoricalData;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AutoRegisterCapability
public class PlayerMoveHistoryProvider implements ICapabilityProvider {
    public static final Capability<HistoricalData<Vec3>> HISTORICAL_DATA = CapabilityManager.get(new CapabilityToken<>() {});

    private HistoricalData<Vec3> positionalHistory;
    private final LazyOptional<HistoricalData<Vec3>> optional = LazyOptional.of(this::createHistoricalData);

    private HistoricalData<Vec3> createHistoricalData(){
        if(this.positionalHistory == null)
            this.positionalHistory = new HistoricalData<>(Vec3.ZERO, 10);

        return this.positionalHistory;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == HISTORICAL_DATA)
            return optional.cast();

        return LazyOptional.empty();
    }
}
