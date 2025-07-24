package me.contaria.anglesnaparrowfix.mixin;

import net.minecraft.entity.Entity;
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
public abstract class ArrowOwnershipMixin extends Entity {

    @Unique
    private UUID ownerUUID;

    public ArrowOwnershipMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void onWriteNbt(NbtCompound nbt, CallbackInfo ci) {
        if (ownerUUID != null) {
            nbt.putUuid("OwnerUUID", ownerUUID);
        }
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void onReadNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.containsUuid("OwnerUUID")) {
            this.ownerUUID = nbt.getUuid("OwnerUUID");
        }
    }

    @Unique
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Unique
    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }
}
