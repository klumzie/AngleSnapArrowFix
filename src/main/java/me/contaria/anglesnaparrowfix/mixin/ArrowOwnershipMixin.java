package me.contaria.anglesnaparrowfix.mixin;

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

    // The tick injection has been moved to EntityMixin.java

    // FIX: Removed the specific method signature to silence compiler warnings.
    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void saveOwner(NbtCompound nbt, CallbackInfo ci) {
        if (ownerUUID != null) {
            nbt.putString("AngleSnapFixOwnerUUID", ownerUUID.toString());
        }
    }

    // FIX: Removed the specific method signature here as well.
    @Inject(method = "readNbt", at = @At("TAIL"))
    private void loadOwner(NbtCompound nbt, CallbackInfo ci) {
        try {
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