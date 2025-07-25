package me.contaria.anglesnaparrowfix;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import me.contaria.anglesnaparrowfix.mixin.ArrowOwnershipAccessor;

import java.util.UUID;

public class AngleSnapArrowFix implements ModInitializer {
    public static final String MOD_ID = "anglesnaparrowfix";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("AngleSnapArrowFix initialized.");

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            UUID playerUUID = player.getUuid();
            
            LOGGER.info("Player {} joined, scanning all loaded chunks for owned arrows...", player.getName().getString());

            // Run in next tick to ensure player is fully loaded
            server.execute(() -> {
                try {
                    int restoredCount = 0;
                    int totalArrowsChecked = 0;
                    
                    for (ServerWorld world : server.getWorlds()) {
                        LOGGER.debug("Checking world: {}", world.getRegistryKey().getValue());
                        
                        // Iterate through ALL loaded entities (since chunks are kept loaded)
                        for (Entity entity : world.iterateEntities()) {
                            if (entity instanceof PersistentProjectileEntity arrow) {
                                totalArrowsChecked++;
                                
                                try {
                                    ArrowOwnershipAccessor accessor = (ArrowOwnershipAccessor) arrow;
                                    UUID arrowOwner = accessor.getOwnerUUID();
                                    
                                    if (arrowOwner != null && arrowOwner.equals(playerUUID)) {
                                        // Check if arrow needs ownership restoration
                                        if (arrow.getOwner() == null) {
                                            arrow.setOwner(player);
                                            restoredCount++;
                                            LOGGER.debug("Restored arrow at {} in {}", 
                                                       arrow.getPos(), world.getRegistryKey().getValue());
                                        } else {
                                            LOGGER.debug("Arrow already has correct owner: {}", 
                                                       arrow.getOwner().getName().getString());
                                        }
                                    }
                                } catch (ClassCastException e) {
                                    // This shouldn't happen with our mixin, but just in case
                                    LOGGER.warn("Found arrow without ownership mixin at {}", entity.getPos());
                                }
                            }
                        }
                    }
                    
                    LOGGER.info("Arrow ownership scan complete for {}: checked {} arrows, restored {} arrows", 
                              player.getName().getString(), totalArrowsChecked, restoredCount);
                    
                } catch (Exception e) {
                    LOGGER.error("Error during arrow ownership restoration for player: {}", 
                               player.getName().getString(), e);
                }
            });
        });
    }
}