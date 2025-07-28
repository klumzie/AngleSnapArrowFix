package me.contaria.anglesnaparrowfix;

import java.util.UUID;

/**
 * This interface acts as a bridge, allowing our main mod logic
 * to safely access the custom fields we add to the arrow entity via mixins.
 */
public interface ArrowOwnershipAccessor {
    /**
     * Gets the UUID of the arrow's persistent owner.
     * @return The owner's UUID, or null if not set.
     */
    UUID getOwnerUUID();

    /**
     * Sets the UUID of the arrow's persistent owner.
     * @param uuid The UUID of the player who shot the arrow.
     */
    void setOwnerUUID(UUID uuid);
}