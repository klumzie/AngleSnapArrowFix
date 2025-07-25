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

    // This adds a new field to the arrow class at runtime.
    @Unique
    private UUID ownerUUID;

    /**
     * Implements the getOwnerUUID method from our accessor interface.
     * @return The UUID of the arrow's owner, or null if not set.
     */
    @Override
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    /**
     * Implements the setOwnerUUID method from our accessor interface.
     * @param uuid The UUID of the owner to set.
     */
    @Override
    public void setOwnerUUID(UUID uuid) {
        this.ownerUUID = uuid;
    }

    /**
     * Injects into the method that saves the arrow's data to disk.
     * This ensures our custom UUID field is included in the save data.
     */
    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void saveOwnerUUID(NbtCompound nbt, CallbackInfo ci) {
        if (this.ownerUUID != null) {
            // Use a unique key to prevent conflicts with other mods or vanilla data.
            nbt.putUuid("AngleSnapFixOwnerUUID", this.ownerUUID);
        }
    }

    /**
     * Injects into the method that reads the arrow's data from disk.
     * This ensures our custom UUID field is loaded back when the arrow is loaded.
     */
    @Inject(method = "readNbt", at = @At("TAIL"))
    private void loadOwnerUUID(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.containsUuid("AngleSnapFixOwnerUUID")) {
            this.ownerUUID = nbt.getUuid("AngleSnapFixOwnerUUID");
        }
    }
}
