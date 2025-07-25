package me.contaria.anglesnaparrowfix;

import java.util.UUID;

/**
 * This interface acts as a bridge between our mixins and the main mod logic.
 * It defines the custom methods that we "add" to the arrow entity.
 */
public interface ArrowOwnershipAccessor {
    /**
     * Gets the UUID of the arrow's owner.
     * @return The owner's UUID, or null if it hasn't been set.
     */
    UUID getOwnerUUID();

    /**
     * Sets the UUID of the arrow's owner.
     * @param uuid The UUID of the player who shot the arrow.
     */
    void setOwnerUUID(UUID uuid);
}
