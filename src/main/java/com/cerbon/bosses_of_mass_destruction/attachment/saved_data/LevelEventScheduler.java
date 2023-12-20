package com.cerbon.bosses_of_mass_destruction.attachment.saved_data;

import com.cerbon.bosses_of_mass_destruction.api.maelstrom.general.event.EventScheduler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class LevelEventScheduler extends SavedData {
    private static final Factory<LevelEventScheduler> FACTORY = new Factory<>(LevelEventScheduler::new, LevelEventScheduler::new);
    public static LevelEventScheduler INSTANCE;
    private static final String DATA_KEY = "bomd_level_event_scheduler";
    private EventScheduler eventScheduler;

    public LevelEventScheduler() {
        this(new CompoundTag());
    }

    public LevelEventScheduler(CompoundTag tag) {}

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        return tag;
    }

    public static EventScheduler get(Level level) {
        LevelEventScheduler levelEventScheduler = getSavedData(level);

        if (levelEventScheduler.eventScheduler == null)
            levelEventScheduler.eventScheduler = new EventScheduler();

        return levelEventScheduler.eventScheduler;
    }

    private static LevelEventScheduler getSavedData(Level level) {
        return level.isClientSide ? INSTANCE : ((ServerLevel) level).getDataStorage().computeIfAbsent(FACTORY, DATA_KEY);
    }
}
