package cz.lukasmojzis.telemetrymod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * The EventDispatcher class is responsible for listening to and handling various game events in Minecraft.
 * These events include client ticks, player logins, player updates, player deaths, player logouts, and configuration changes.
 * Upon an event being fired, the EventDispatcher runs the corresponding routines, such as syncing configuration changes, rendering game overlay, etc.
 * <p>
 * Each method in this class is annotated with @SubscribeEvent, meaning they're automatically subscribed to the relevant event in the Forge Mod event system.
 *
 * @see net.minecraftforge.fml.common.Mod.EventBusSubscriber
 * @see net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
@Mod.EventBusSubscriber(modid = TelemetryMod.MODID)
public class EventDispatcher {

    /**
     * Handles the client tick event.
     * This event fires once per frame and is responsible for updating various aspects of the game's state.
     *
     * @param event - ClientTickEvent
     * @see net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
//            PlayerStateController.pollPollablePlayers();
            TransactionController.onClientTick();
        }
    }

    /**
     * Handles the player login event.
     * This event fires when a player logs in to the game.
     *
     * @param event - PlayerLoggedInEvent
     * @see net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        PlayerStateController.onPlayerLogin(player);
        OBSRelay.onPlayerLogin();
    }

    /**
     * Handles the player update event.
     * This event fires every tick for each living entity, and can be used to implement changes to player entities.
     *
     * @param event - LivingUpdateEvent
     * @see net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent
     */
    @SubscribeEvent
    public static void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            PlayerStateController.pollPlayerState(player);
        }
    }

    /**
     * Handles the player death event.
     * This event fires when an EntityPlayer dies.
     *
     * @param event - LivingDeathEvent
     * @see net.minecraftforge.event.entity.living.LivingDeathEvent
     */
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (player != null) {
                TelemetryMod.onPlayerDeath();
            }
        }
    }

    /**
     * Handles the player logout event.
     * This event fires when a player logs out of the game.
     *
     * @param event - PlayerLoggedOutEvent
     * @see net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent
     */
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerStateController.onPlayerLogout(event);
        OBSRelay.onPlayerLogout();
    }

    /**
     * Handles the configuration change event.
     * This event fires when the configuration of the game changes.
     *
     * @param event - OnConfigChangedEvent
     * @see net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent
     */
    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        ModConfig.sync(event);
    }

    /**
     * Handles the configuration post-change event.
     * This event fires after the configuration of the game changes.
     *
     * @param event - PostConfigChangedEvent
     * @see net.minecraftforge.fml.client.event.ConfigChangedEvent.PostConfigChangedEvent
     */
    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.PostConfigChangedEvent event) {
        ModConfig.sync(event);
    }

    /**
     * Handles the game overlay rendering event.
     * This event fires when the game overlay is rendered, which can be used to render additional custom elements on top of the game.
     *
     * @param event - RenderGameOverlayEvent.Text
     * @see net.minecraftforge.client.event.RenderGameOverlayEvent.Text
     */
    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        OverlayController.renderGameOverlay();
    }

}
