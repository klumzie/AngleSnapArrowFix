package me.contaria.anglesnaparrowfix.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        // Check if this entity implements our accessor
        if (this instanceof ArrowOwnershipAccessor accessor) {

            // FIX: Also check if the entity is a ProjectileEntity before getting the owner.
            // This uses a modern Java feature to check the type and create a new variable (`projectile`).
            if ((Object)this instanceof ProjectileEntity projectile) {

                if (accessor.getOwnerUUID() == null) {
                    Entity owner = projectile.getOwner(); // We call getOwner() on the projectile, not the base entity.
                    if (owner instanceof PlayerEntity player) {
                        accessor.setOwnerUUID(player.getUuid());
                    }
                }
            }
        }
    }
}