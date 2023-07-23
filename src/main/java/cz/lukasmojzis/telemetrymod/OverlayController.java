package cz.lukasmojzis.telemetrymod;

import net.minecraft.client.resources.I18n;

/**
 * The OverlayController class manages the rendering of the in-game overlay.
 * This overlay provides various game statistics such as the player's play time,
 * number of deaths, and transaction backlog, and is displayed on the player's
 * screen while they are playing the game.
 */
public class OverlayController {
    /**
     * This constant represents the color white in the RGB color space.
     */
    private static final int COLOR_WHITE = 0xFFFFFF;

    /**
     * This is the method that handles the entire rendering of the overlay.
     * It's responsible for calling other rendering methods and specifying
     * their positions on the screen. Before rendering anything, it checks
     * whether the debug info is already being displayed, and if so, it does
     * nothing, preventing the overlay from obstructing the debug info.
     */
    static void renderGameOverlay() {
        if (TelemetryMod.minecraft.gameSettings.showDebugInfo) return;

        int x = 10;
        int y = 10;
        y = renderPlayTime(x, y);
        y = renderDeaths(x, y);
        y = renderBacklog(x, y);

    }

    /**
     * Renders the current size of the transaction backlog when it's higher
     * than 1 as this indicates connection issues.
     *
     * @param x The x-coordinate where the rendering should start.
     * @param y The y-coordinate where the rendering should start.
     * @return The updated y-coordinate for next item to be rendered.
     */
    private static int renderBacklog(int x, int y) {
        int transactionBundleCount = TransactionController.size();
        if (transactionBundleCount > 0) {
            String transactionBacklogString = String.format("%s: %s", "Backlog", transactionBundleCount);
            TelemetryMod.minecraft.fontRenderer.drawStringWithShadow(transactionBacklogString, x, y, COLOR_WHITE);
            y += 10;
        }
        return y;
    }

    /**
     * Renders the total number of player's deaths. The death count is fetched
     * from the PlayerState of the player.
     *
     * @param x The x-coordinate where the rendering should start.
     * @param y The y-coordinate where the rendering should start.
     * @return The updated y-coordinate for next item to be rendered.
     */
    private static int renderDeaths(int x, int y) {
        String deathsLabel = I18n.format("stat.deaths");
        PlayerStatusTracker playerState = PlayerStateController.getPlayerState(TelemetryMod.minecraft.player);
        if (playerState != null) {
            Object deathCount = playerState.getField(PlayerProperty.DEATH_COUNT, 0);
            String deathCountString = String.format("%s: %s", deathsLabel, deathCount);
            TelemetryMod.minecraft.fontRenderer.drawStringWithShadow(deathCountString, x, y, COLOR_WHITE);
            y += 10;
        }
        return y;
    }

    /**
     * Renders the total playtime of the player. The time is calculated by
     * converting the total number of world ticks into hours, minutes, and seconds.
     *
     * @param x The x-coordinate where the rendering should start.
     * @param y The y-coordinate where the rendering should start.
     * @return The updated y-coordinate for next item to be rendered.
     */
    private static int renderPlayTime(int x, int y) {
        String playTimeLabel = I18n.format("stat.playOneMinute");
        long ticks = WorldCalendar.getTotalWorldTicks();
        long totalMillisPassed = (ticks * 50);
        long totalSeconds = totalMillisPassed / 1000;
        long totalMinutes = totalSeconds / 60;
        long totalHours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        long seconds = totalSeconds % 60;
        String timeStamp = String.format("%02d:%02d:%02d", totalHours, minutes, seconds);
        String playTimeString = String.format("%s: %s", playTimeLabel, timeStamp);
        TelemetryMod.minecraft.fontRenderer.drawStringWithShadow(playTimeString, x, y, COLOR_WHITE);
        y += 10;
        return y;
    }

}
