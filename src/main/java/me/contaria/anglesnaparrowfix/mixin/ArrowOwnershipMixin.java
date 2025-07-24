package me.contaria.anglesnaparrowfix.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
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

    // Capture owner when arrow is shot by a player
    @Inject(method = "setOwner", at = @At("HEAD"))
    private void capturePlayerOwner(LivingEntity entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            this.ownerUUID = player.getUuid();
        }
    }

    // Fallback: capture owner during tick if needed
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

    // Save owner UUID to NBT after vanilla writes other data
    @Inject(method = "writeNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void saveOwner(NbtCompound nbt, CallbackInfo ci) {
        if (ownerUUID != null) {
            nbt.putString("OwnerUUID", ownerUUID.toString());
        }
    }

    // Load owner UUID from NBT after vanilla loads other data
    @Inject(method = "readNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void loadOwner(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("OwnerUUID")) {
            Optional<String> optUuid = nbt.getString("OwnerUUID");
            optUuid.ifPresent(uuidString -> {
                try {
                    this.ownerUUID = UUID.fromString(uuidString);
                    restoreOwnerEntity();
                } catch (IllegalArgumentException e) {
                    this.ownerUUID = null;
                }
            });
        }
    }

    @Unique
    private void restoreOwnerEntity() {
        PersistentProjectileEntity arrow = (PersistentProjectileEntity) (Object) this;
        if (arrow.getWorld() instanceof ServerWorld serverWorld && ownerUUID != null) {
            PlayerEntity player = serverWorld.getPlayerByUuid(ownerUUID);
            if (player != null) {
                arrow.setOwner(player);
            }
        }
    }

    @Unique
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    @Unique
    public void setOwnerUUID(UUID uuid) {
        this.ownerUUID = uuid;
    }
}
