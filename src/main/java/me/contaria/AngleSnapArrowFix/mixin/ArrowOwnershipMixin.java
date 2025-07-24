package me.contaria.anglesnapserver.mixin;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PersistentProjectileEntity.class)
public class ArrowOwnershipMixin {

    @Unique
    private UUID ownerUUID;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(Entity entity, World world, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            this.ownerUUID = player.getUuid();
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void saveOwner(NbtCompound nbt, CallbackInfo ci) {
        if (ownerUUID != null) {
            nbt.putUuid("OwnerUUID", ownerUUID);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void loadOwner(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.containsUuid("OwnerUUID")) {
            this.ownerUUID = nbt.getUuid("OwnerUUID");
        }
    }

    @Unique
    public UUID getOwnerUUID() {
        return ownerUUID;
    }
}