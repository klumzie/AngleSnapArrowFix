package me.contaria.anglesnaparrowfix.mixin;

import me.contaria.anglesnaparrowfix.ArrowOwnershipAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin is the LOGIC controller. It detects when an owner is set
 * and uses the accessor to store the owner's UUID.
 */
@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin {

    /**
     * This is the primary way we capture ownership. It fires when a projectile's
     * owner is first set.
     */
    @Inject(method = "setOwner", at = @At("HEAD"))
    private void capturePlayerOwnerOnSet(Entity owner, CallbackInfo ci) {
        // Check if this projectile can have a UUID owner and if the owner is a player
        if (this instanceof ArrowOwnershipAccessor accessor && owner instanceof PlayerEntity player) {
            accessor.setOwnerUUID(player.getUuid());
        }
    }

    /**
     * This is a failsafe. It runs every tick to catch any edge cases where the
     * owner might have been set in a way that our first injection missed.
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void capturePlayerOwnerOnTick(CallbackInfo ci) {
        if (this instanceof ArrowOwnershipAccessor accessor) {
            // Only run if the UUID hasn't been set yet
            if (accessor.getOwnerUUID() == null) {
                ProjectileEntity projectile = (ProjectileEntity)(Object)this;
                Entity owner = projectile.getOwner();
                if (owner instanceof PlayerEntity player) {
                    accessor.setOwnerUUID(player.getUuid());
                }
            }
        }
    }
}