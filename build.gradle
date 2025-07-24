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
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            this.ownerUUID = player.getUuid();
        }
    }

    @Inject(method = "writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void saveOwner(NbtCompound nbt, CallbackInfo ci) {
        if (ownerUUID != null) {
            // Store UUID as string since putUuid methods don't exist in this version
            nbt.putString("OwnerUUID", ownerUUID.toString());
        }
    }

    @Inject(method = "readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void loadOwner(NbtCompound nbt, CallbackInfo ci) {
        // Check if the UUID string exists and parse it
        if (nbt.contains("OwnerUUID", 8)) { // 8 = STRING type
            try {
                this.ownerUUID = UUID.fromString(nbt.getString("OwnerUUID"));
            } catch (IllegalArgumentException e) {
                // Handle invalid UUID format gracefully
                this.ownerUUID = null;
            }
        }
    }

    @Unique
    public UUID getOwnerUUID() {
        return ownerUUID;
    }
}