package cz.lukasmojzis.telemetrymod;

/**
 * The PlayerProperty enum represents various properties related to a player entity in the game.
 * These properties can be used to track and monitor specific aspects of the player's state.
 * Each enum constant provides a string representation of the property, which can be used to refer to it.
 */
public enum PlayerProperty {
    ACTIVE_POTION_EFFECTS("ActivePotionEffects"),
    ADDED_TO_CHUNK("addedToChunk"),
    AIR("Air"),
    ARROW_HIT_TIMER("arrowHitTimer"),
    ATTACKED_AT_YAW("attackedAtYaw"),
    BED_LOCATION("bedLocation"),
    CAPTURE_DROPS("captureDrops"),
    CHUNK_COORDS_X("chunkCoordX"),
    CHUNK_COORDS_Y("chunkCoordY"),
    CHUNK_COORDS_Z("chunkCoordZ"),
    COLLIDED("collided"),
    COLLIDED_HORIZONTALLY("collidedHorizontally"),
    COLLIDED_VERTICALLY("collidedVertically"),
    DEATH_COUNT("deathCount"),
    DIMENSION("dimension"),
    DISTANCE_WALKED_MODIFIED("distanceWalkedModified"),
    ENTITY_COLLISION_REDUCTION("entityCollisionReduction"),
    EXPERIENCE_LEVEL("experienceLevel"),
    EXPERIENCE_PROGRESS("experienceProgress"),
    FALL_DISTANCE("fallDistance"),
    FOODLEVEL("FoodLevel"),
    FORCESPAWN("forceSpawn"),
    HEALTH("Health"),
    HEIGHT("height"),
    HELDITEMMAINHAND("HeldItemMainhand"),
    HELDITEMOFFHAND("HeldItemOffhand"),
    IGNOREFRUSTUMCHECK("ignoreFrustumCheck"),
    ISAIRBORNE("isAirBorne"),
    ISBEINGRIDDEN("isBeingRidden"),
    ISBURNING("isBurning"),
    ISDEAD("isDead"),
    ISENTITYALIVE("isEntityAlive"),
    ISENTITYINSIDEOPAQUEBLOCK("isEntityInsideOpaqueBlock"),
    ISGLOWING("isGlowing"),
    ISHURT("isHurt"),
    ISIMMUNETOEXPLOSIONS("isImmuneToExplosions"),
    ISINLAVA("isInLava"),
    ISINVISIBLE("isInvisible"),
    ISINVULNERABLE("IsInvulnerable"),
    ISINWATER("isInWater"),
    ISNONBOSS("isNonBoss"),
    ISOUTSIDEBORDER("isOutsideBorder"),
    ISOVERWATER("isOverWater"),
    ISPUSHEDBYWATER("isPushedByWater"),
    ISRIDING("isRiding"),
    ISSILENT("isSilent"),
    ISSNEAKING("isSneaking"),
    ISSPRINTING("isSprinting"),
    ISTHIRSTY("isThirsty"),
    ISWET("isWet"),
    MAXFALLHEIGHT("MaxFallHeight"),
    MAXHEALTH("MaxHealth"),
    MAXHURTRESISTANTTIME("maxHurtResistantTime"),
    MAXHURTTIME("maxHurtTime"),
    MOTIONX("motionX"),
    MOTIONY("motionY"),
    MOTIONZ("motionZ"),
    MOVEFORWARD("moveForward"),
    MOVESTRAFING("moveStrafing"),
    MOVEVERTICAL("moveVertical"),
    NAME("Name"),
    NOCLIP("noClip"),
    ONGROUND("onGround"),
    PERSISTENTID("PersistentID"),
    POSX("posX"),
    POSY("posY"),
    POSZ("posZ"),
    PREVENTENTITYSPAWNING("preventEntitySpawning"),
    RANDOMUNUSED1("randomUnused1"),
    RANDOMUNUSED2("randomUnused2"),
    RANDOMYAWVELOCITY("randomYawVelocity"),
    SATURATIONLEVEL("SaturationLevel"),
    SCORE("Score"),
    SERVERPOSX("serverPosX"),
    SERVERPOSY("serverPosY"),
    SERVERPOSZ("serverPosZ"),
    STEPHEIGHT("stepHeight"),
    TEMPERATURELEVEL("TemperatureLevel"),
    THIRSTLEVEL("ThirstLevel"),
    TICKSELYTRAFLYING("TicksElytraFlying"),
    TIMEUNTILPORTAL("timeUntilPortal"),
    TOTALARMORVALUE("TotalArmorValue"),
    UNIQUEID("UniqueID"),
    UPDATEBLOCKED("updateBlocked"),
    VELOCITYCHANGED("velocityChanged"),
    WIDTH("width"),
    YOFFSET("YOffset");

    private final String property;

    /**
     * Constructs a new PlayerProperty enum constant with the given property name.
     *
     * @param property The name of the player property.
     */
    PlayerProperty(String property) {
        this.property = property;
    }

    /**
     * Returns the string representation of the player property.
     *
     * @return The string representation of the player property.
     */
    @Override
    public String toString() {
        return property;
    }
}