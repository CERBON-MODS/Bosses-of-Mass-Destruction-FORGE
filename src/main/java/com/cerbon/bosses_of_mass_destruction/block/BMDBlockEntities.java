package com.cerbon.bosses_of_mass_destruction.block;

import com.cerbon.bosses_of_mass_destruction.block.custom.MobWardBlockEntity;
import com.cerbon.bosses_of_mass_destruction.block.custom.MonolithBlockEntity;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BMDBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCKS_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BMDConstants.MOD_ID);

    public static final RegistryObject<BlockEntityType<MobWardBlockEntity>> MOB_WARD = BLOCKS_ENTITIES.register("mob_ward",
            () -> BlockEntityType.Builder.of(MobWardBlockEntity::new, BMDBlocks.MOB_WARD.get()).build(null));

    public static final RegistryObject<BlockEntityType<MonolithBlockEntity>> MONOLITH_BLOCK_ENTITY = BLOCKS_ENTITIES.register("monolith_block",
            () -> BlockEntityType.Builder.of(MonolithBlockEntity::new, BMDBlocks.MONOLITH_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus){
        BLOCKS_ENTITIES.register(eventBus);
    }
}
