package com.cerbon.bosses_of_mass_destruction.mixin;

import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.item.custom.ChargedEnderPearlEntity;
import com.cerbon.bosses_of_mass_destruction.item.custom.SoulStarEntity;
import com.cerbon.bosses_of_mass_destruction.projectile.MagicMissileProjectile;
import com.cerbon.bosses_of_mass_destruction.projectile.PetalBladeProjectile;
import com.cerbon.bosses_of_mass_destruction.projectile.SporeBallProjectile;
import com.cerbon.bosses_of_mass_destruction.projectile.comet.CometProjectile;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetHandler.class)
public abstract class ClientPlayNetHandlerMixin {

    @Shadow private ClientWorld level;

    @Inject(method = "handleAddEntity", at = @At("TAIL"))
    private void moveEntity(SSpawnObjectPacket packet, CallbackInfo ci) {
        double d0 = packet.getX();
        double d1 = packet.getY();
        double d2 = packet.getZ();

        EntityType<?> entitytype = packet.getType();
        Entity entity = null;

        if (entitytype == BMDEntities.CHARGED_ENDER_PEARL.get())
            entity = new ChargedEnderPearlEntity(BMDEntities.CHARGED_ENDER_PEARL.get(), level);

        else if (entitytype == BMDEntities.SOUL_STAR.get())
            entity = new SoulStarEntity(BMDEntities.SOUL_STAR.get(), level);

        else if (entitytype == BMDEntities.MAGIC_MISSILE.get())
            entity = new MagicMissileProjectile(BMDEntities.MAGIC_MISSILE.get(), level);

        else if (entitytype == BMDEntities.COMET.get())
            entity = new CometProjectile(BMDEntities.COMET.get(), level);

        else if (entitytype == BMDEntities.PETAL_BLADE.get())
            entity = new PetalBladeProjectile(BMDEntities.PETAL_BLADE.get(), level);

        else if (entitytype == BMDEntities.SPORE_BALL.get())
            entity = new SporeBallProjectile(BMDEntities.SPORE_BALL.get(), level);

        if (entity != null) {
            int i = packet.getId();
            entity.setPacketCoordinates(d0, d1, d2);
            entity.moveTo(d0, d1, d2);
            entity.xRot = (float)(packet.getxRot() * 360) / 256.0F;
            entity.yRot = (float)(packet.getyRot() * 360) / 256.0F;
            entity.setId(i);
            entity.setUUID(packet.getUUID());
            this.level.putNonPlayerEntity(i, entity);
        }
    }
}
