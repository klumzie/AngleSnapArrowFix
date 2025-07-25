package me.contaria.anglesnaparrowfix.mixin;

import me.contaria.anglesnaparrowfix.ArrowOwnershipAccessor;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * This mixin is the DATA container. It adds the UUID field to arrows
 * and handles saving and loading that data to disk.
 */
@Mixin(PersistentProjectileEntity.class)
public class ArrowOwnershipMixin implements ArrowOwnershipAccessor {

    @Unique
    private UUID ownerUUID;

    // These two methods implement our accessor interface.
    @Override
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    @Override
    public void setOwnerUUID(UUID uuid) {
        this.ownerUUID = uuid;
    }

    // Injects into the method that saves entity data to disk.
    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void saveOwnerUUID(NbtCompound nbt, CallbackInfo ci) {
        if (this.ownerUUID != null) {
            nbt.putUuid("AngleSnapFixOwnerUUID", this.ownerUUID);
        }
    }

    // Injects into the method that reads entity data from disk.
    @Inject(method = "readNbt", at = @At("TAIL"))
    private void loadOwnerUUID(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.containsUuid("AngleSnapFixOwnerUUID")) {
            this.ownerUUID = nbt.getUuid("AngleSnapFixOwnerUUID");
        }
    }
}