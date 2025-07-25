package me.contaria.anglesnaparrowfix.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PersistentProjectileEntity.class)
public class ArrowOwnershipMixin implements ArrowOwnershipAccessor {

    @Unique
    private UUID ownerUUID;

    // The setOwner injection has been moved to ProjectileEntityMixin.
    // This class will now receive the UUID via the accessor.

    // Failsafe: capture from shooter field if owner is set by other means
    @Inject(method = "tick", at = @At("HEAD"))
    private void captureOwnerFromShooter(CallbackInfo ci) {
        if (ownerUUID == null) {
            PersistentProjectileEntity arrow = (PersistentProjectileEntity) (Object) this;
            Entity owner = arrow.getOwner();
            if (owner instanceof PlayerEntity player) {
                this.ownerUUID = player.getUuid();
            }
        }
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void saveOwner(NbtCompound nbt, CallbackInfo ci) {
        if (ownerUUID != null) {
            // Use a more specific key to avoid potential conflicts
            nbt.putString("AngleSnapFixOwnerUUID", ownerUUID.toString());
        }
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void loadOwner(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("AngleSnapFixOwnerUUID")) {
            try {
                this.ownerUUID = UUID.fromString(nbt.getString("AngleSnapFixOwnerUUID"));
            } catch (IllegalArgumentException e) {
                this.ownerUUID = null;
            }
        }
    }

    @Override
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    @Override
    public void setOwnerUUID(UUID uuid) {
        this.ownerUUID = uuid;
    }
}