package me.contaria.anglesnaparrowfix.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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

    // Capture owner when arrow is shot by a player
    @Inject(method = "setOwner", at = @At("HEAD"))
    private void capturePlayerOwner(Entity entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            this.ownerUUID = player.getUuid();
        }
    }

    // Alternative: capture from shooter field if setOwner doesn't work
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

    @Inject(method = "writeNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void saveOwner(NbtCompound nbt, CallbackInfo ci) {
        if (ownerUUID != null) {
            nbt.putString("OwnerUUID", ownerUUID.toString());
        }
    }

    @Inject(method = "readNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void loadOwner(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("OwnerUUID")) {
            try {
                String uuidString = nbt.getString("OwnerUUID").orElse("");
                if (!uuidString.isEmpty()) {
                    this.ownerUUID = UUID.fromString(uuidString);
                }
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