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

    // By specifying the method signature, we resolve the compiler warning.
    @Inject(method = "writeNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void saveOwner(NbtCompound nbt, CallbackInfo ci) {
        if (ownerUUID != null) {
            nbt.putString("AngleSnapFixOwnerUUID", ownerUUID.toString());
        }
    }

    // We specify the signature here too and fix the error by handling the Optional<String>.
    @Inject(method = "readNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void loadOwner(NbtCompound nbt, CallbackInfo ci) {
        try {
            // This safely gets the string or an empty one if the tag doesn't exist.
            String uuidString = nbt.getString("AngleSnapFixOwnerUUID").orElse("");
            if (!uuidString.isEmpty()) {
                this.ownerUUID = UUID.fromString(uuidString);
            }
        } catch (IllegalArgumentException e) {
            this.ownerUUID = null;
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