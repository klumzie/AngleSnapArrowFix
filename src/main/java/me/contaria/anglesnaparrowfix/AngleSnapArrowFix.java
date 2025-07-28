package me.contaria.anglesnaparrowfix;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class AngleSnapArrowFix implements ModInitializer {
    public static final String MOD_ID = "anglesnaparrowfix";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("AngleSnapArrowFix initialized. Arrow ownership will now persist.");

        // Register an event that fires every time a player joins the server.
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            UUID playerUUID = player.getUuid();
            
            // We run this on the next server tick to ensure the world is ready.
            server.execute(() -> {
                try {
                    int restoredCount = 0;
                    
                    // Iterate over all worlds on the server.
                    for (ServerWorld world : server.getWorlds()) {
                        // Iterate over every entity in the world.
                        for (Entity entity : world.iterateEntities()) {
                            // Check if the entity is a persistent projectile (like an arrow).
                            if (entity instanceof PersistentProjectileEntity arrow) {
                                // Cast the arrow to our accessor to get the stored UUID.
                                ArrowOwnershipAccessor accessor = (ArrowOwnershipAccessor) arrow;
                                UUID arrowOwnerUUID = accessor.getOwnerUUID();
                                
                                // If the arrow has a stored owner UUID and it matches the joining player...
                                if (playerUUID.equals(arrowOwnerUUID)) {
                                    // ...and if the arrow's current owner entity is null (because the player was offline)...
                                    if (arrow.getOwner() == null) {
                                        // ...then restore ownership to the player who just joined.
                                        arrow.setOwner(player);
                                        restoredCount++;
                                    }
                                }
                            }
                        }
                    }
                    
                    if (restoredCount > 0) {
                        LOGGER.info("Restored ownership of {} arrow(s) for player {}.", restoredCount, player.getName().getString());
                    }
                    
                } catch (Exception e) {
                    LOGGER.error("Failed to restore arrow ownership for player {}:", player.getName().getString(), e);
                }
            });
        });
    }
}