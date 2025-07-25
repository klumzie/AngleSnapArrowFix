package me.contaria.anglesnaparrowfix.mixin;

import me.contaria.anglesnaparrowfix.ArrowOwnershipAccessor;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
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

    @Override
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    @Override
    public void setOwnerUUID(UUID uuid) {
        this.ownerUUID = uuid;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void saveOwnerUUID(NbtCompound nbt, CallbackInfo ci) {
        if (this.ownerUUID != null) {
            nbt.putString("AngleSnapFixOwnerUUID", this.ownerUUID.toString());
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void loadOwnerUUID(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("AngleSnapFixOwnerUUID", NbtElement.STRING_TYPE)) {
            try {
                this.ownerUUID = UUID.fromString(nbt.getString("AngleSnapFixOwnerUUID"));
            } catch (IllegalArgumentException e) {
                this.ownerUUID = null;
            }
        }
    }
}
