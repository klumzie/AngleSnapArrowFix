package me.contaria.anglesnaparrowfix.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin implements ArrowOwnershipAccessor {
    
    @Unique
    private UUID anglesnaparrowfix$ownerUUID;
    
    @Override
    public UUID getOwnerUUID() {
        return this.anglesnaparrowfix$ownerUUID;
    }
    
    @Override
    public void setOwnerUUID(UUID uuid) {
        this.anglesnaparrowfix$ownerUUID = uuid;
    }
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (this.anglesnaparrowfix$ownerUUID == null) {
            ProjectileEntity projectile = (ProjectileEntity)(Object)this;
            Entity owner = projectile.getOwner();
            if (owner instanceof PlayerEntity player) {
                this.anglesnaparrowfix$ownerUUID = player.getUuid();
            }
        }
    }
}