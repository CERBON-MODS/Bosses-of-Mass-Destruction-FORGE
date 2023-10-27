package com.cerbon.bosses_of_mass_destruction.entity;

import com.cerbon.bosses_of_mass_destruction.item.custom.ChargedEnderPearlEntity;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BMDEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BMDConstants.MOD_ID);

    public static final RegistryObject<EntityType<ChargedEnderPearlEntity>> CHARGED_ENDER_PEARL = ENTITY_TYPES.register("charged_ender_pearl",
            () -> EntityType.Builder.of(ChargedEnderPearlEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(new ResourceLocation(BMDConstants.MOD_ID, "charged_ender_pearl").toString()));

    public static void initClient(){
        EntityRenderers.register(CHARGED_ENDER_PEARL.get(), ThrownItemRenderer::new);
    }

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}
