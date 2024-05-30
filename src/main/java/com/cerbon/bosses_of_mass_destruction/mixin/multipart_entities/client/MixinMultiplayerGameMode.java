package com.cerbon.bosses_of_mass_destruction.mixin.multipart_entities.client;

import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.client.PlayerInteractMultipartEntity;
import com.cerbon.bosses_of_mass_destruction.api.multipart_entities.entity.MultipartAwareEntity;
import com.cerbon.bosses_of_mass_destruction.packet.BMDPacketHandler;
import com.cerbon.bosses_of_mass_destruction.packet.custom.multipart_entities.MultipartEntityInteractionC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameType;
import net.minecraft.util.math.vector.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerController.class)
public abstract class MixinMultiplayerGameMode {

    @Shadow private GameType localPlayerMode;

    @Shadow protected abstract void ensureHasSentCarriedItem();

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void attackHook(final PlayerEntity player, final Entity target, final CallbackInfo ci) {
        if (target instanceof MultipartAwareEntity) {
            ensureHasSentCarriedItem();

            Minecraft client = Minecraft.getInstance();
            final Vector3d pos = client.cameraEntity.getEyePosition(client.getFrameTime());
            final Vector3d dir = client.cameraEntity.getViewVector(client.getFrameTime());
            final double reach = client.gameMode.getPickRange();
            String part = ((MultipartAwareEntity) target).getBounds().raycast(pos, pos.add(dir.scale(reach)));
            if (part == null) return;

            BMDPacketHandler.sendToServer(new MultipartEntityInteractionC2SPacket(target.getId(), part, Hand.MAIN_HAND, client.cameraEntity.isShiftKeyDown(), PlayerInteractMultipartEntity.InteractionType.ATTACK));

            if (localPlayerMode != GameType.SPECTATOR) {
                ((MultipartAwareEntity) target).setNextDamagedPart(part);
                player.attack(target);
                player.resetAttackStrengthTicker();
            }
            ci.cancel();
        }
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void interactHook(final PlayerEntity player, final Entity entity, final Hand hand, final CallbackInfoReturnable<ActionResultType> cir) {
        if (entity instanceof MultipartAwareEntity) {
            ensureHasSentCarriedItem();

            Minecraft client = Minecraft.getInstance();
            final Vector3d pos = client.cameraEntity.getEyePosition(client.getFrameTime());
            final Vector3d dir = client.cameraEntity.getViewVector(client.getFrameTime());
            final double reach = client.gameMode.getPickRange();
            String part = ((MultipartAwareEntity) entity).getBounds().raycast(pos, pos.add(dir.scale(reach)));
            if (part == null) return;

            BMDPacketHandler.sendToServer(new MultipartEntityInteractionC2SPacket(entity.getId(), part, hand, client.cameraEntity.isShiftKeyDown(), PlayerInteractMultipartEntity.InteractionType.INTERACT));

            if (localPlayerMode != GameType.SPECTATOR)
                cir.setReturnValue(((MultipartAwareEntity) entity).interact(player, hand, part));

            cir.setReturnValue(ActionResultType.PASS);
        }
    }
}
