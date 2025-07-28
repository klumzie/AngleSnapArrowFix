package me.contaria.anglesnaparrowfix.mixin;

import me.contaria.anglesnaparrowfix.ArrowOwnershipAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PersistentProjectileEntity.class)
public abstract class ArrowOwnershipMixin extends Entity implements ArrowOwnershipAccessor {
    @Unique
    private UUID ownerUUID;

    // Required constructor, unchanged
    public ArrowOwnershipMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    // Interface implementation, unchanged
    @Override
    @Unique
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    @Override
    @Unique
    public void setOwnerUUID(UUID uuid) {
        this.ownerUUID = uuid;
    }

    // --- Mixin Injections ---
    // This injection is correct and has not changed.
    @Inject(method = "setOwner(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
    private void onSetOwner(Entity owner, CallbackInfo ci) {
        if (owner != null) {
            this.setOwnerUUID(owner.getUuid());
        }
    }

    /**
     * Saves our custom owner UUID to the arrow's data.
     * Using writeCustomData method from PersistentProjectileEntity.
     */
    @Inject(method = "writeCustomData(Lnet/minecraft/storage/WriteView;)V", at = @At("TAIL"))
    private void writeOwnerToData(WriteView view, CallbackInfo ci) {
        if (this.ownerUUID != null) {
            // Use string representation for UUID storage
            view.putString("PersistentOwnerUUID", this.ownerUUID.toString());
        }
    }

    /**
     * Reads the custom owner UUID from the arrow's data.
     * Using readCustomData method from PersistentProjectileEntity.
     */
    @Inject(method = "readCustomData(Lnet/minecraft/storage/ReadView;)V", at = @At("TAIL"))
    private void readOwnerFromData(ReadView view, CallbackInfo ci) {
        // Use getOptionalString which returns Optional<String>
        view.getOptionalString("PersistentOwnerUUID").ifPresent(uuidString -> {
            try {
                if (!uuidString.isEmpty()) {
                    this.ownerUUID = UUID.fromString(uuidString);
                }
            } catch (IllegalArgumentException e) {
                // Handle invalid UUID format gracefully
                this.ownerUUID = null;
            }
        });
    }
}