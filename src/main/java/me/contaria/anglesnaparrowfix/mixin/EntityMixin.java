package me.contaria.anglesnaparrowfix.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    // Inject into the base tick method for all entities
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        // First, check if this entity is one of our arrows (that implements the accessor)
        if (this instanceof ArrowOwnershipAccessor accessor) {
            
            // Only run the logic if the owner hasn't been set yet
            if (accessor.getOwnerUUID() == null) {
                Entity self = (Entity) (Object) this;
                
                // This logic is the same as your original "failsafe"
                Entity owner = self.getOwner();
                if (owner instanceof PlayerEntity player) {
                    accessor.setOwnerUUID(player.getUuid());
                }
            }
        }
    }
}