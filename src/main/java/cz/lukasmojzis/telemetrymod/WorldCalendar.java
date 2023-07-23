package cz.lukasmojzis.telemetrymod;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * The WorldCalendar class provides functionality related to world time ticks from the world.
 */
public class WorldCalendar {

    /**
     * This method retrieves the creation time of the world currently loaded in the {@link TelemetryMod}.
     *
     * <p>It works by accessing the directory where the world is stored,
     * and fetching the 'creationTime' attribute of the directory itself.
     * This attribute represents the time at which the world directory was created.
     *
     * @return The creation time of the world directory, represented as the number of milliseconds since the Unix Epoch.
     * @throws Error if an exception is encountered while trying to calculate the world creation time.
     *               This is a fatal error as the method is not expected to fail under normal circumstances.
     */
    static long getCreationTime() {
        try {
            File worldDir = TelemetryMod.currentWorld.getSaveHandler().getWorldDirectory();
            BasicFileAttributes attr = Files.readAttributes(worldDir.toPath(), BasicFileAttributes.class);
            return attr.creationTime().toMillis();
        } catch (Exception e) {
            throw new Error("Failed to calculate the world creation time");
        }
    }


    /**
     * Returns the total number of world time ticks from the world.
     *
     * @return the total world time ticks
     */
    public static long getTotalWorldTicks() {
        return TelemetryMod.currentWorld.getTotalWorldTime();
    }

}
