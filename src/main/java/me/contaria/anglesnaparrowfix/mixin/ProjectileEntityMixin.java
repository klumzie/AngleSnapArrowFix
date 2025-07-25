package me.contaria.anglesnaparrowfix.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public class ProjectileEntityMixin {

    @Inject(method = "setOwner", at = @At("HEAD"))
    private void capturePlayerOwner(Entity entity, CallbackInfo ci) {
        // Check if this projectile is one of ours (implements the accessor)
        // and if the new owner is a player.
        if (this instanceof ArrowOwnershipAccessor accessor && entity instanceof PlayerEntity player) {
            accessor.setOwnerUUID(player.getUuid());
        }
    }
}