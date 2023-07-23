package cz.lukasmojzis.telemetrymod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * The main class for the Telemetry Mod.
 * This is the entry point for all mod operations and initializes the necessary components.
 */
@Mod(modid = TelemetryMod.MODID, name = TelemetryMod.NAME, version = TelemetryMod.VERSION, acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber(modid = TelemetryMod.MODID)
public class TelemetryMod {

    public static final String MODID = "telemetrymod";
    public static final String NAME = "Telemetry Mod";
    public static final String AUTHOR = "Lukáš Mojžíš>";
    public static final String VERSION = "1.0";

    static final File GLOBAL_DEATHS_FILE = new File("global_deaths.txt");


    @Mod.Instance(MODID)
    public static TelemetryMod instance;
    public static Logger logger;
    static Minecraft minecraft;
    static MinecraftServer server;
    static World currentWorld;
    static TelemetryWebSocketClient telemetryClient;

    /**
     * Called when the server is starting.
     * Initializes the server, overworld, and calendar instances.
     *
     * @param event The FMLServerStartingEvent.
     */
    @EventHandler
    public static void onServerStarting(FMLServerStartingEvent event) {
        server = FMLCommonHandler.instance().getMinecraftServerInstance();
    }

    /**
     * The sendMessage method sends a message to all connected players or a specific player.
     */
    public static void sendMessage(String message, EntityPlayer player) {
        String msg = TextFormatting.BLUE + "[" + NAME + "] " + TextFormatting.RESET + message;
        TextComponentString component = new TextComponentString(msg);

        if (player == null) {
            // Get the PlayerList object from the MinecraftServer instance
            PlayerList playerList = server.getPlayerList();

            // Get a list of all currently logged in players
            List<EntityPlayerMP> players = playerList.getPlayers();

            // Send the message to each player
            for (EntityPlayerMP currentPlayer : players) {
                currentPlayer.sendMessage(component);
            }
        } else {
            player.sendMessage(component);
        }
    }

    // Death Counting: onPlayerDeath, readGlobalDeathCount, createDeathsFile, and writeGlobalDeathCount are related
    // to recording the number of player deaths. Whenever a player dies, onPlayerDeath increments a death count stored
    // in global_deaths.txt.

    /**
     * Called when a player dies in the game.
     * Increments the global death count and updates the global_deaths.txt file.
     * The death count is used to track the total number of player deaths across sessions.
     */
    public static synchronized void onPlayerDeath() {
        // TODO: Differentiate which player died
        int deathCount = readGlobalDeathCount();
        deathCount++;
        writeGlobalDeathCount(deathCount);
    }

    /**
     * Creates the global_deaths.txt file and initializes the death count to 0.
     */
    static int readGlobalDeathCount() {
        if (!TelemetryMod.GLOBAL_DEATHS_FILE.exists()) {
            createDeathsFile();
            return 0;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(TelemetryMod.GLOBAL_DEATHS_FILE))) {
            String line = reader.readLine();
            if (line != null) {
                return Integer.parseInt(line);
            }
        } catch (IOException e) {
            TelemetryMod.logger.error("Failed to read global death count", e);
        }
        return 0;
    }

    /**
     * Creates the global_deaths.txt file and initializes the death count to 0.
     */
    private static void createDeathsFile() {
        try {
            if (TelemetryMod.GLOBAL_DEATHS_FILE.createNewFile()) {
                writeGlobalDeathCount(0);
            }
        } catch (IOException e) {
            TelemetryMod.logger.error("Failed to create deaths file", e);
        }
    }

    /**
     * Writes the provided death count to the global_deaths.txt file.
     *
     * @param deathCount The death count to be written.
     */
    private static void writeGlobalDeathCount(int deathCount) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TelemetryMod.GLOBAL_DEATHS_FILE))) {
            writer.write(Integer.toString(deathCount));
        } catch (IOException e) {
            TelemetryMod.logger.error("Failed to write global death count", e);
        }
    }

    /**
     * Called during the pre-initialization phase of the mod loading.
     * Initializes the logger and Minecraft instance.
     *
     * @param event The FMLPreInitializationEvent.
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        minecraft = Minecraft.getMinecraft();
    }

    /**
     * Called during the initialization phase of the mod loading.
     * Registers the mod with MinecraftForge event bus and connects to the Telemetry WebSocket server.
     *
     * @param event The FMLInitializationEvent.
     */
    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("The {} by {} has been initialized", NAME, AUTHOR);
        MinecraftForge.EVENT_BUS.register(this);
        try {
            telemetryClient = new TelemetryWebSocketClient(new URI(ModConfig.websocket.uri));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        telemetryClient.connect();
    }

    /**
     * Called when the server is stopped.
     * Resets the server, overworld, and calendar instances to null.
     *
     * @param event The FMLServerStoppedEvent.
     */
    @EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        server = null;
    }

}
