package cz.lukasmojzis.telemetrymod;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;

/**
 * The ModConfig class holds configuration options for the Telemetry Mod.
 * These options include settings related to OBS (Open Broadcaster Software) integration,
 * Websocket integration, and various game telemetry data that can be tracked, such as position changes,
 * motion changes, distance walked, collisions, and more.
 * <p>
 * This class is decorated with Forge's @Config annotation, which allows these settings to be easily manipulated
 * from the Forge configuration GUI.
 */
@Config(modid = TelemetryMod.MODID)
public class ModConfig {

    @Config.Name("OBS")
    @Config.Comment("You can configure the OBS here")
    public static OBSCategory OBS = new OBSCategory();

    @Config.Name("Websocket")
    @Config.Comment("You can configure the Websocket here")
    public static WebsocketCategory websocket = new WebsocketCategory();
    @Config.Name("Report position changes")
    @Config.Comment("This is make telemetry report position changes")
    public static boolean reportPosition = false;
    @Config.Name("Report Motion changes")
    @Config.Comment("This is make telemetry report Motion changes")
    public static boolean reportMotion = false;
    @Config.Name("Report Distance Walked")
    @Config.Comment("This is make telemetry report Distance Walked")
    public static boolean reportDistanceWalked = false;
    @Config.Name("Report Collision")
    @Config.Comment("This is make telemetry report Collision")
    public static boolean reportCollided = false;
    @Config.Name("Report Chunk Coordinates")
    @Config.Comment("This will make telemetry report Chunk Coordinates")
    public static boolean reportChunkCoords = false;
    @Config.Name("Report Move")
    @Config.Comment("This is make telemetry report Move")
    public static boolean reportMove = false;
    @Config.Name("Report Flight")
    @Config.Comment("This is make telemetry report Flight")
    public static boolean reportFlight = false;
    @Config.Name("Report Width and Height")
    @Config.Comment("This is make telemetry report Width and Height")
    public static boolean reportDimensions = false;
    @Config.Name("Report Dimensions")
    @Config.Comment("This is make telemetry report Dimensions")
    public static boolean reportDimension = false;
    @Config.Name("Report Water")
    @Config.Comment("This is make telemetry report Water")
    public static boolean reportWater = false;

    /**
     * Syncs the configuration each time it's changed.
     *
     * @param event The event that triggers the configuration to sync.
     */
    static void sync(ConfigChangedEvent event) {
        if (event.getModID().equals(TelemetryMod.MODID)) {
            ConfigManager.sync(TelemetryMod.MODID, Config.Type.INSTANCE);
        }
    }

    /**
     * A subcategory for configuring OBS (Open Broadcaster Software) integration settings.
     */
    public static class OBSCategory {

        @Config.Name("Enable integration")
        @Config.RequiresWorldRestart()
        @Config.Comment("Enable this if you want this mod to interact with your OBS")
        public boolean enabled = false;

        @Config.Name("Host")
        @Config.Comment("Hostname of the OBS instance")
        public String host = "localhost";
        @Config.Name("Port")
        @Config.Comment("Port of the OBS instance")
        public int port = 4455;
        @Config.Name("Password")
        @Config.Comment("Password of the OBS instance")
        public String password = "";
        @Config.Name("Connection Timeout")
        @Config.Comment("Timeout Connection if not connected in this amount of seconds")
        public int connectionTimeout = 3;

        @Config.Name("Camera Source Name")
        @Config.Comment("Name of the Camera source within OBS. Only select the final sources and not the utility sources.")
        public String cameraSourceName = "Camera";
    }

    /**
     * A subcategory for configuring Websocket integration settings.
     */
    public static class WebsocketCategory {

        @Config.Name("Enable integration")
        @Config.RequiresWorldRestart()
        @Config.Comment("Enable this if you want this mod to interact with your Websocket server")
        public boolean enabled = false;

        @Config.Name("Host")
        @Config.Comment("URI of the Websocket instance. Must start with ws:// or wss://")
        public String uri = "ws://127.0.0.1:8080";

        @Config.Name("Connection Timeout")
        @Config.Comment("Timeout Connection if not connected in this amount of seconds")
        public int connectionTimeout = 3;
    }
}