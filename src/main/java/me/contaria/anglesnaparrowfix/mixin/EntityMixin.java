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
    
    // Target the correct tick method - Entity.tick() exists and has signature ()V
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        // Cast to Entity to access methods
        Entity entity = (Entity)(Object)this;
        
        // Check if this is a ProjectileEntity
        if (entity instanceof ProjectileEntity projectile) {
            // Check if this entity implements our accessor
            if (this instanceof ArrowOwnershipAccessor accessor) {
                if (accessor.getOwnerUUID() == null) {
                    Entity owner = projectile.getOwner();
                    if (owner instanceof PlayerEntity player) {
                        accessor.setOwnerUUID(player.getUuid());
                    }
                }
            }
        }
    }
}