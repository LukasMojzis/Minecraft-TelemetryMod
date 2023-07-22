package cz.lukasmojzis.telemetrymod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * The PlayerStateController class provides functionality to track and manage the state of players in the game.
 * It allows for monitoring and updating player states, enabling telemetry and event notifications related to players.
 */
public class PlayerStateController {
    // A map to store player states with their corresponding UUIDs as keys.
    private static final HashMap<UUID, PlayerStatusTracker> playerStates = new HashMap<>();

    /**
     * Called when a player logs in to the game.
     * Initializes and adds a new PlayerStatusTracker to track the player's state.
     *
     * @param player The player who logged in.
     */
    static void onPlayerLogin(EntityPlayer player) {
        UUID playerUUID = player.getUniqueID();
        PlayerStatusTracker playerState = new PlayerStatusTracker(player);
        playerStates.put(playerUUID, playerState);
    }

    /**
     * Called when a player logs out of the game.
     * Removes the corresponding PlayerStatusTracker from the playerStates map.
     *
     * @param event The PlayerLoggedOutEvent triggered upon player logout.
     */

    static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID playerUUID = event.player.getUniqueID();
        playerStates.remove(playerUUID);
    }

    /**
     * Polls and updates the state of a player.
     * This method is typically called periodically to keep track of player state changes.
     *
     * @param player The player to poll the state for.
     */
    static void pollPlayerState(EntityPlayer player) {
        UUID playerUUID = player.getUniqueID();
        PlayerStatusTracker playerState = playerStates.get(playerUUID);

        if (playerState != null) {
            playerState.updateState(player);
        } else {
            // PlayerState doesn't exist for this playerUUID.
            // Handle this situation appropriately, maybe by logging an error.
            TelemetryMod.logger.error("Failed to poll player state. No PlayerState exists for UUID: " + playerUUID);
        }
    }

    /**
     * Retrieves the PlayerStatusTracker associated with a specific player.
     *
     * @param player The player for which to get the PlayerStatusTracker.
     * @return The PlayerStatusTracker for the specified player, or null if not found.
     */
    public static PlayerStatusTracker getPlayerState(EntityPlayer player) {
        return playerStates.get(player.getUniqueID());
    }

}
