package cz.lukasmojzis.telemetrymod;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The PlayerStatusTracker class is responsible for tracking and managing the current status of a player in the game.
 * It captures and stores diverse data related to the player's current status and activities in the game.
 * This includes general status, motion, positional data, flight status, dimensional data, water interaction,
 * movement parameters, and other game-specific details. The class is designed to interact with specific capabilities
 * provided by the SimpleDifficulty API to measure a player's thirst and temperature levels.
 * <p>
 * Each instance of this class is tied to a specific player, with methods for updating the state based on
 * the player's actions and retrieving stored state data.
 *
 * @see com.charles445.simpledifficulty.api.SDCapabilities
 * @see com.charles445.simpledifficulty.api.temperature.ITemperatureCapability
 * @see com.charles445.simpledifficulty.api.thirst.IThirstCapability
 * @see net.minecraft.entity.player.EntityPlayer
 */
public class PlayerStatusTracker {
    private final EntityPlayer player;
    private final Map<PlayerProperty, Object> state = new HashMap<>();
    private final IThirstCapability thirst;
    private final ITemperatureCapability temperature;

    /**
     * The PlayerStatusTracker constructor is responsible for initializing an instance of the PlayerStatusTracker.
     * It associates an instance with an EntityPlayer, captures the player's temperature and thirst levels using
     * the SDCapabilities, and updates the player's state.
     *
     * @param player The EntityPlayer whose status is to be tracked.
     */
    public PlayerStatusTracker(EntityPlayer player) {
        this.player = player;
        if (Loader.isModLoaded("simpledifficulty")) {
            thirst = player.getCapability(SDCapabilities.THIRST, null);
            temperature = player.getCapability(SDCapabilities.TEMPERATURE, null);
        } else {
            thirst = null;
            temperature = null;
        }
        updateState(player);
    }

    /**
     * This method updates the state of the player, capturing all the required fields depending on the ModConfig setup.
     * It then compares the newly calculated values with the existing ones in the state map, if any changes are found,
     * these are then logged as a transaction.
     * Note: This method will not function if the world is remotely controlled or if OBSRelay is not connected.
     *
     * @param player The EntityPlayer whose state is to be updated.
     */
    public void updateState(EntityPlayer player) {
        if (!player.world.isRemote) return;
        if (OBSRelay.connected) OBSRelay.setColorCorrectionFilterState(player);

        if (ModConfig.reportCollided) {
            updateField(PlayerProperty.COLLIDED, player.collided);
            updateField(PlayerProperty.COLLIDED_HORIZONTALLY, player.collidedHorizontally);
            updateField(PlayerProperty.COLLIDED_VERTICALLY, player.collidedVertically);
        }
        if (ModConfig.reportDistanceWalked) {
            updateField(PlayerProperty.DISTANCE_WALKED_MODIFIED, player.distanceWalkedModified);
        }
        if (ModConfig.reportMotion) {
            updateField(PlayerProperty.MOTIONX, player.motionX);
            updateField(PlayerProperty.MOTIONY, player.motionY);
            updateField(PlayerProperty.MOTIONZ, player.motionZ);
        }
        if (ModConfig.reportPosition) {
            updateField(PlayerProperty.POSX, player.posX);
            updateField(PlayerProperty.POSY, player.posY);
            updateField(PlayerProperty.POSZ, player.posZ);
        }
        if (ModConfig.reportChunkCoords) {
            updateField(PlayerProperty.CHUNK_COORDS_X, player.chunkCoordX);
            updateField(PlayerProperty.CHUNK_COORDS_Y, player.chunkCoordY);
            updateField(PlayerProperty.CHUNK_COORDS_Z, player.chunkCoordZ);
        }
        if (ModConfig.reportFlight) {
            updateField(PlayerProperty.FALL_DISTANCE, player.fallDistance);
            updateField(PlayerProperty.ISAIRBORNE, player.isAirBorne);
            updateField(PlayerProperty.ONGROUND, player.onGround);
        }
        if (ModConfig.reportDimension) {
            updateField(PlayerProperty.DIMENSION, player.dimension);
        }
        if (ModConfig.reportDimensions) {
            updateField(PlayerProperty.HEIGHT, player.height);
            updateField(PlayerProperty.WIDTH, player.width);
        }
        if (ModConfig.reportWater) {
            updateField(PlayerProperty.ISINWATER, player.isInWater());
            updateField(PlayerProperty.ISOVERWATER, player.isOverWater());
            updateField(PlayerProperty.ISPUSHEDBYWATER, player.isPushedByWater());
        }
        if (ModConfig.reportMove) {
            updateField(PlayerProperty.MOVEFORWARD, player.moveForward);
            updateField(PlayerProperty.MOVESTRAFING, player.moveStrafing);
            updateField(PlayerProperty.MOVEVERTICAL, player.moveVertical);
            updateField(PlayerProperty.ISSNEAKING, player.isSneaking());
            updateField(PlayerProperty.ISSPRINTING, player.isSprinting());
        }
        updateField(PlayerProperty.ACTIVE_POTION_EFFECTS, player.getActivePotionEffects());
        updateField(PlayerProperty.AIR, player.getAir());
        updateField(PlayerProperty.FOODLEVEL, player.getFoodStats().getFoodLevel());
        updateField(PlayerProperty.HEALTH, player.getHealth());
        updateField(PlayerProperty.HELDITEMMAINHAND, player.getHeldItemMainhand());
        updateField(PlayerProperty.HELDITEMOFFHAND, player.getHeldItemOffhand());
        updateField(PlayerProperty.ISINVULNERABLE, player.getIsInvulnerable());
        updateField(PlayerProperty.MAXFALLHEIGHT, player.getMaxFallHeight());
        updateField(PlayerProperty.MAXHEALTH, player.getMaxHealth());
        updateField(PlayerProperty.NAME, player.getName());
        updateField(PlayerProperty.PERSISTENTID, player.getPersistentID());
        updateField(PlayerProperty.SATURATIONLEVEL, player.getFoodStats().getSaturationLevel());
        updateField(PlayerProperty.SCORE, player.getScore());
        updateField(PlayerProperty.TICKSELYTRAFLYING, player.getTicksElytraFlying());
        updateField(PlayerProperty.TOTALARMORVALUE, player.getTotalArmorValue());
        updateField(PlayerProperty.UNIQUEID, player.getUniqueID());
        updateField(PlayerProperty.YOFFSET, player.getYOffset());
        updateField(PlayerProperty.ADDED_TO_CHUNK, player.addedToChunk);
        updateField(PlayerProperty.ARROW_HIT_TIMER, player.arrowHitTimer);
        updateField(PlayerProperty.ATTACKED_AT_YAW, player.attackedAtYaw);
        updateField(PlayerProperty.BED_LOCATION, player.getBedLocation().toString());
        updateField(PlayerProperty.CAPTURE_DROPS, player.captureDrops);
        updateField(PlayerProperty.ENTITY_COLLISION_REDUCTION, player.entityCollisionReduction);
        updateField(PlayerProperty.EXPERIENCE_LEVEL, player.experienceLevel);
        updateField(PlayerProperty.EXPERIENCE_PROGRESS, player.experience);
        updateField(PlayerProperty.FORCESPAWN, player.forceSpawn);
        updateField(PlayerProperty.ISHURT, player.hurtTime > 0);
        updateField(PlayerProperty.IGNOREFRUSTUMCHECK, player.ignoreFrustumCheck);
        updateField(PlayerProperty.ISBEINGRIDDEN, player.isBeingRidden());
        updateField(PlayerProperty.ISBURNING, player.isBurning());
        updateField(PlayerProperty.ISDEAD, player.isDead);
        updateField(PlayerProperty.ISENTITYALIVE, player.isEntityAlive());
        updateField(PlayerProperty.ISENTITYINSIDEOPAQUEBLOCK, player.isEntityInsideOpaqueBlock());
        updateField(PlayerProperty.ISGLOWING, player.isGlowing());
        updateField(PlayerProperty.ISIMMUNETOEXPLOSIONS, player.isImmuneToExplosions());
        updateField(PlayerProperty.ISINLAVA, player.isInLava());
        updateField(PlayerProperty.ISINVISIBLE, player.isInvisible());
        updateField(PlayerProperty.ISNONBOSS, player.isNonBoss());
        updateField(PlayerProperty.ISOUTSIDEBORDER, player.isOutsideBorder());
        updateField(PlayerProperty.ISRIDING, player.isRiding());
        updateField(PlayerProperty.ISSILENT, player.isSilent());
        updateField(PlayerProperty.ISWET, player.isWet());
        updateField(PlayerProperty.MAXHURTRESISTANTTIME, player.maxHurtResistantTime);
        updateField(PlayerProperty.MAXHURTTIME, player.maxHurtTime);
        updateField(PlayerProperty.NOCLIP, player.noClip);
        updateField(PlayerProperty.PREVENTENTITYSPAWNING, player.preventEntitySpawning);
        updateField(PlayerProperty.RANDOMUNUSED1, player.randomUnused1);
        updateField(PlayerProperty.RANDOMUNUSED2, player.randomUnused2);
        updateField(PlayerProperty.RANDOMYAWVELOCITY, player.randomYawVelocity);
        updateField(PlayerProperty.SERVERPOSX, player.serverPosX);
        updateField(PlayerProperty.SERVERPOSY, player.serverPosY);
        updateField(PlayerProperty.SERVERPOSZ, player.serverPosZ);
        updateField(PlayerProperty.STEPHEIGHT, player.stepHeight);
        updateField(PlayerProperty.TIMEUNTILPORTAL, player.timeUntilPortal);
        updateField(PlayerProperty.UPDATEBLOCKED, player.updateBlocked);
        updateField(PlayerProperty.VELOCITYCHANGED, player.velocityChanged);
        updateField(PlayerProperty.DEATH_COUNT, TelemetryMod.readGlobalDeathCount());
        if (temperature != null) {
            updateField(PlayerProperty.TEMPERATURELEVEL, temperature.getTemperatureLevel());
        }
        if (thirst != null){
            updateField(PlayerProperty.THIRSTLEVEL, thirst.getThirstLevel());
            updateField(PlayerProperty.ISTHIRSTY, thirst.isThirsty());
        }
    }

    /**
     * This method updates a specific field (property) in the state map and logs any changes that occur.
     * If the new value is different from the current value, a PlayerStateUpdate transaction is created and added to the bundle.
     *
     * @param property The property of the player to update.
     * @param newValue The new value to be set for the property.
     */
    private void updateField(PlayerProperty property, Object newValue) {
        Object current = state.get(property);
        if (!Objects.equals(current, newValue)) {
            PlayerStateUpdate playerStateUpdate = new PlayerStateUpdate(player.getName(), WorldCalendar.getTotalWorldTicks(), property, current, newValue);
            TransactionController.addTransactionToBundle(playerStateUpdate);
            state.put(property, newValue);
        }
    }

    /**
     * This method retrieves the current value of a specified field (property) from the state map.
     *
     * @param property The property of the player to retrieve.
     * @return The current value of the specified property, or null if the property is not currently set.
     */
    public Object getField(PlayerProperty property) {
        return state.get(property);
    }

    /**
     * This method retrieves the current value of a specified field (property) from the state map.
     * If the property is not currently set, it returns the provided default value.
     *
     * @param property     The property of the player to retrieve.
     * @param defaultValue The default value to return if the property is not currently set.
     * @return The current value of the specified property, or the provided default value if the property is not currently set.
     */
    public Object getField(PlayerProperty property, Object defaultValue) {
        Object value = getField(property);
        if (value == null) return defaultValue;
        return value;
    }

}
