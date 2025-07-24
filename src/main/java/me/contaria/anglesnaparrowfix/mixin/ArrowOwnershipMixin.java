package me.contaria.anglesnaparrowfix.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PersistentProjectileEntity.class)
public abstract class ArrowOwnershipMixin extends net.minecraft.entity.Entity {

    @Unique
    private UUID ownerUUID;

    public ArrowOwnershipMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeOwnerToNbt(NbtCompound nbt, CallbackInfo ci) {
        if (ownerUUID != null) {
            nbt.putUuid("OwnerUUID", ownerUUID);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readOwnerFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.containsUuid("OwnerUUID")) {
            this.ownerUUID = nbt.getUuid("OwnerUUID");
        }
    }

    @Unique
    public void setOwnerUUID(UUID uuid) {
        this.ownerUUID = uuid;
    }

    @Unique
    public UUID getOwnerUUID() {
        return ownerUUID;
    }
}
