package me.contaria.anglesnaparrowfix.mixin;

import java.util.UUID;

/**
 * Accessor interface for tracking arrow ownership
 */
public interface ArrowOwnershipAccessor {
    UUID getOwnerUUID();
    void setOwnerUUID(UUID uuid);
}