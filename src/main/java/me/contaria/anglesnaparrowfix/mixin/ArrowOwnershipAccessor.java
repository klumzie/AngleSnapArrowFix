package me.contaria.anglesnaparrowfix.mixin;

import java.util.UUID;

public interface ArrowOwnershipAccessor {
    UUID getOwnerUUID();
    void setOwnerUUID(UUID uuid);
}