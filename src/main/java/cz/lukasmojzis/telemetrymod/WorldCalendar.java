package cz.lukasmojzis.telemetrymod;

/**
 * The WorldCalendar class provides functionality related to world time ticks from the world.
 */
public class WorldCalendar {
    /**
     * Returns the total number of world time ticks from the world.
     *
     * @return the total world time ticks
     */
    public long getTotalWorldTicks() {
        return TelemetryMod.currentWorld.getTotalWorldTime();
    }

}
