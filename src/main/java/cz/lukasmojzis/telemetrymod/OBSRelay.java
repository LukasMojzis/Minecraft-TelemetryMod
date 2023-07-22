package cz.lukasmojzis.telemetrymod;

import com.google.gson.JsonObject;
import io.obswebsocket.community.client.OBSRemoteController;
import io.obswebsocket.community.client.listener.lifecycle.ReasonThrowable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The OBSRelay class is responsible for interfacing between the Telemetry Mod and the OBS (Open Broadcast Software),
 * a popular streaming and recording software. This class handles the setup of OBS configuration and connection,
 * color correction filters in OBS, and player statuses. It creates a new OBSRelay for each player upon their login.
 * <p>
 * It can update the OBS based on player statuses such as being hurt, dead, burning, poisoned, underwater, or withered.
 * It also handles the connection and disconnection process with the OBS Websocket Server.
 */
public class OBSRelay {

    private static final String COLOR_CORRECTION_FILTER_NAME = String.format("%s %s", TelemetryMod.MODID, "playerState");
    private static final String COLOR_CORRECTION_FILTER_KIND = "color_filter_v2";
    private static final Color COLOR_HURT = new Color(127, 0, 0, 255);
    private static final Color COLOR_POISON = new Color(127, 255, 127, 255);
    private static final Color COLOR_BURNING = new Color(200, 120, 20, 255);
    private static final Color COLOR_WITHER = new Color(63, 63, 63, 255);
    private static final int RECONNECT_TICKS = 60;
    public static OBSRemoteController obsController;
    static String status = "Disconnected";
    static boolean connect = false;
    static Long ticksWhenConnect;
    static boolean connected = false;
    private static JsonObject colorCorrectionFilterSettings = new JsonObject();
    private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    /**
     * Constructs an OBSRelay object for a given player. The constructor initializes an OBSRemoteController object
     * with OBS configuration information (host, port, password, etc.) and set up the lifecycle events of the OBS connection.
     *
     * @param player the EntityPlayer to create OBSRelay for.
     */
    public OBSRelay(EntityPlayer player) {
        status = null;
        obsController = OBSRemoteController.builder().host(ModConfig.OBS.host)                  // Default host
                .port(ModConfig.OBS.port)                         // Default port
                .password(ModConfig.OBS.password)   // Provide your password here
                .connectionTimeout(ModConfig.OBS.connectionTimeout)               // Seconds the client will wait for OBS to respond
                .lifecycle().onConnect(session -> onConnect(player)).onDisconnect(() -> onDisconnect(player)).onReady(() -> onReady(player)).onCommunicatorError(reasonThrowable -> onCommunicatorError(reasonThrowable, player)).and().autoConnect(connect).build();
    }

    /**
     * Handler method for the communication error event. It sets the status to indicate the reason for the error.
     *
     * @param reasonThrowable the ReasonThrowable object that describes the error.
     * @param player          the EntityPlayer who is associated with the OBSRelay.
     */
    private static void onCommunicatorError(ReasonThrowable reasonThrowable, EntityPlayer player) {
        setStatus(reasonThrowable.getReason(), player);
    }

    /**
     * Handler method for the ready event. It sets the status to "ready".
     *
     * @param player the EntityPlayer who is associated with the OBSRelay.
     */
    private static void onReady(EntityPlayer player) {
        setStatus("ready", player);
    }

    /**
     * Handler method for the disconnect event. It sets the connected state to false, sets the status to "disconnected",
     * and then attempts to reconnect.
     *
     * @param player the EntityPlayer who is associated with the OBSRelay.
     */

    private static void onDisconnect(EntityPlayer player) {
        connected = false;
        setStatus("disconnected", player);
        reconnect(player);
    }

    /**
     * Watches for the client state and maintains open connection with the OBS Websocket Server.
     * If connection is enabled, it schedules a reconnect with the OBS Server.
     *
     * @param player the EntityPlayer who is associated with the OBSRelay.
     */
    private static void reconnect(EntityPlayer player) {
        // cancel any previous reconnect attempts
        executor.shutdownNow();

        if (connect) {
            executor = Executors.newSingleThreadScheduledExecutor();
            executor.schedule(() -> {
                setStatus("reconnecting", player);
                obsController.connect();
            }, RECONNECT_TICKS / 20, TimeUnit.SECONDS);
        }
    }

    /**
     * Handler method for the connect event. It sets the connected state to true, sets the status to "connected",
     * records the time of the connection, and then checks or creates the color correction filter.
     *
     * @param player the EntityPlayer who is associated with the OBSRelay.
     */
    private static void onConnect(EntityPlayer player) {
        connected = true;
        setStatus("connected", player);
        ticksWhenConnect = TelemetryMod.calendar.getTotalWorldTicks();
        findOrCreateColorCorrectionFilter();
    }

    /**
     * When a player logs into the game, it creates a new OBSRelay for this player if OBS integration is enabled.
     */
    static void onPlayerLogin() {
        connect = ModConfig.OBS.enabled;
    }

    /**
     * Checks if the color correction filter is already present in the OBS. If not, it creates one.
     */
    private static void findOrCreateColorCorrectionFilter() {
        obsController.getSourceFilter(ModConfig.OBS.cameraSourceName, COLOR_CORRECTION_FILTER_NAME, (getSourceFilterResponse) -> {
            boolean filterFound = getSourceFilterResponse.isSuccessful();
            if (filterFound) {
                TelemetryMod.logger.info("Filter {} found!", COLOR_CORRECTION_FILTER_NAME);
            } else {
                TelemetryMod.logger.info("Filter {} not found; creating!", COLOR_CORRECTION_FILTER_NAME);
                createColorCorrectionFilter();
            }
        });
    }

    /**
     * Creates the color correction filter in OBS using the OBSRemoteController.
     */
    private static void createColorCorrectionFilter() {
        obsController.createSourceFilter(ModConfig.OBS.cameraSourceName, COLOR_CORRECTION_FILTER_NAME, COLOR_CORRECTION_FILTER_KIND, colorCorrectionFilterSettings, (createSourceFilterResponse) -> {
            TelemetryMod.logger.info("Filter {} created!", COLOR_CORRECTION_FILTER_NAME);
            TelemetryMod.logger.info(createSourceFilterResponse);
        });
    }

    /**
     * Method that adjusts the color correction filter in OBS based on the player's status
     * like hurt, dead, burning, poisoned, under water, and withered. It uses OBSController
     * to set the filter settings.
     *
     * @param player the EntityPlayer to adjust color correction filter for.
     */
    static void setColorCorrectionFilterState(EntityPlayer player) {
        JsonObject filterSettings = new JsonObject();

        if (player.hurtTime > 0) {
            filterSettings.addProperty("color_add", convertARGBtoRGBA(COLOR_HURT));
        } else if (!player.isEntityAlive()) {
            filterSettings.addProperty("saturation", -1.0);
        } else {
            if (player.isBurning()) {
                filterSettings.addProperty("color_multiply", convertARGBtoRGBA(COLOR_BURNING));
            } else if (player.isPotionActive(MobEffects.POISON)) {
                filterSettings.addProperty("color_multiply", convertARGBtoRGBA(COLOR_POISON));
            } else if (player.getAir() < 0xFF) {
                filterSettings.addProperty("color_multiply", convertARGBtoRGBA(new Color(Math.min(255, Math.max(64, 64 + player.getAir())), Math.min(255, Math.max(64, 64 + player.getAir())), 255, 100)));
            }
            if (player.isPotionActive(MobEffects.WITHER)) {
                filterSettings.addProperty("color_multiply", convertARGBtoRGBA(COLOR_WITHER));
            }
        }


        if (!colorCorrectionFilterSettings.equals(filterSettings)) {
            obsController.setSourceFilterSettings(ModConfig.OBS.cameraSourceName, COLOR_CORRECTION_FILTER_NAME, filterSettings, false, (setSourceFilterEnabledResponse) -> {
                if (setSourceFilterEnabledResponse.isSuccessful()) {
                    colorCorrectionFilterSettings = filterSettings;
                }
            });
        }
    }

    /**
     * Helper method for converting a Color object into an integer in the RGBA format
     * that OBS can understand.
     *
     * @param color the Color to convert.
     * @return an integer representation of the color in RGBA format.
     */
    static int convertARGBtoRGBA(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int a = color.getAlpha();


        return (a << 24) | (b << 16) | (g << 8) | r;
    }

    /**
     * Handler method for player logout event. It disconnects the OBSRemoteController,
     * removes the player's OBSRelay, and resets the status.
     */
    static void onPlayerLogout() {
        connect = false;
        obsController.disconnect();
        status = null;
    }


    /**
     * Method that sets the status of OBSRelay and sends a status message to the player.
     *
     * @param status the new status to set.
     * @param player the EntityPlayer to send the status message to.
     */
    private static void setStatus(String status, EntityPlayer player) {
        OBSRelay.status = status;
        TelemetryMod.sendMessage("OBS integration: " + status, player);
    }


}

