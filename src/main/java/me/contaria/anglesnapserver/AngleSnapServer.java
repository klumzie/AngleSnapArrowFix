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
            
            LOGGER.info("Player {} joined, checking for owned arrows...", player.getName().getString());

            for (ServerWorld world : server.getWorlds()) {
                world.iterateEntities().forEach(entity -> {
                    if (entity instanceof PersistentProjectileEntity arrow) {
                        if (arrow instanceof ArrowOwnershipAccessor accessor) {
                            UUID arrowOwner = accessor.getOwnerUUID();
                            if (arrowOwner != null && arrowOwner.equals(playerUUID)) {
                                if (arrow.getOwner() == null) {
                                    arrow.setOwner(player);
                                    LOGGER.info("Restored arrow ownership for player: {}", player.getName().getString());
                                }
                            }
                        }
                    }
                });
            }
        });
    }
}