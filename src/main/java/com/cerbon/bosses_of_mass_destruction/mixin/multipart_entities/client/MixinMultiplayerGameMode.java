package com.cerbon.bosses_of_mass_destruction.mixin.multipart_entities.client;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.client.PlayerInteractMultipartEntity;
import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.MultipartAwareEntity;
import com.cerbon.bosses_of_mass_destruction.packet.BMDPacketHandler;
import com.cerbon.bosses_of_mass_destruction.packet.custom.multipart_entities.MultipartEntityAttackC2SPacket;
import com.cerbon.bosses_of_mass_destruction.packet.custom.multipart_entities.MultipartEntityInteractC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MixinMultiplayerGameMode {
    @Shadow
    protected abstract void ensureHasSentCarriedItem();

    @Shadow
    private GameType localPlayerMode;

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void attackHook(final Player player, final Entity target, final CallbackInfo ci) {
        if (target instanceof MultipartAwareEntity) {
            ensureHasSentCarriedItem();
            BMDPacketHandler.sendToServer(new MultipartEntityAttackC2SPacket((MultipartAwareEntity) target));
            if (localPlayerMode != GameType.SPECTATOR) {
                final Minecraft client = Minecraft.getInstance();
                final Vec3 pos = client.cameraEntity.getEyePosition(client.getFrameTime());
                final Vec3 dir = client.cameraEntity.getViewVector(client.getFrameTime());
                final double reach = client.gameMode.getPickRange();
                ((MultipartAwareEntity) target).setNextDamagedPart(((MultipartAwareEntity) target).getBounds().raycast(pos, pos.add(dir.multiply(reach, reach, reach))));
                player.attack(target);
                player.resetAttackStrengthTicker();
            }
            ci.cancel();
        }
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void interactHook(final Player player, final Entity entity, final InteractionHand hand, final CallbackInfoReturnable<InteractionResult> cir) {
        if (entity instanceof MultipartAwareEntity) {
            ensureHasSentCarriedItem();
            BMDPacketHandler.sendToServer(new MultipartEntityInteractC2SPacket(hand, (MultipartAwareEntity) entity));
            if (localPlayerMode != GameType.SPECTATOR){
                final Minecraft client = Minecraft.getInstance();
                final Vec3 pos = client.cameraEntity.getEyePosition(client.getFrameTime());
                final Vec3 dir = client.cameraEntity.getViewVector(client.getFrameTime());
                final double reach = client.gameMode.getPickRange();
                final String part = ((MultipartAwareEntity) entity).getBounds().raycast(pos, pos.add(dir.multiply(reach, reach, reach)));
                if (part != null)
                    cir.setReturnValue(((MultipartAwareEntity) entity).interact(client.cameraEntity, hand, part));
            }
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
