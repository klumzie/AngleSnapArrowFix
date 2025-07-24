package me.contaria.anglesnaparrowfix.mixin;

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

import java.util.Optional;
import java.util.UUID;

@Mixin(PersistentProjectileEntity.class)
public class ArrowOwnershipMixin {

    @Unique
    private UUID ownerUUID;

    @Inject(method = "<init>(Lnet/minecraft/entity/Entity;Lnet/minecraft/world/World;)V", at = @At("RETURN"))
    private void onInit(Entity entity, World world, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            this.ownerUUID = player.getUuid();
        }
    }

    @Inject(method = "writeCustomDataToTag(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void saveOwner(NbtCompound nbt, CallbackInfo ci) {
        if (ownerUUID != null) {
            nbt.putString("OwnerUUID", ownerUUID.toString());
        }
    }

    @Inject(method = "readCustomDataFromTag(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void loadOwner(NbtCompound nbt, CallbackInfo ci) {
        Optional<String> uuidString = nbt.getString("OwnerUUID");
        uuidString.ifPresent(s -> {
            try {
                this.ownerUUID = UUID.fromString(s);
            } catch (IllegalArgumentException e) {
                this.ownerUUID = null;
            }
        });
    }

    @Unique
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }
}
