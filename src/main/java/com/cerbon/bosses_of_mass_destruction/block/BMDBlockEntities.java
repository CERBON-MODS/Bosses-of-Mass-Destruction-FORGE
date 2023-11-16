package com.cerbon.bosses_of_mass_destruction.block;

import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BMDBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCKS_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BMDConstants.MOD_ID);

    public static void register(IEventBus eventBus){
        BLOCKS_ENTITIES.register(eventBus);
    }
}
