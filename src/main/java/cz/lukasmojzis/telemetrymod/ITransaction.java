package cz.lukasmojzis.telemetrymod;

/**
 * The ITransaction interface defines a contract for transactional interactions with players in the game.
 * This could be used for handling changes to player properties, game state changes, or other game interactions that require tracking or history.
 * <p>
 * The methods defined in the interface allow for accessing essential data about the transaction, such as the player involved, the game tick at which it happened, the property being affected, and the new value of that property.
 * <p>
 * Note: The actual implementations of this interface will have to provide these data.
 */
public interface ITransaction {
    /**
     * Returns the name of the player involved in the transaction.
     *
     * @return String representing the player's name.
     */
    String getPlayer();

    /**
     * Returns the game tick at which the transaction happened.
     * Game ticks are like the "heartbeat" of a game, occurring regularly to update the game state.
     *
     * @return long representing the game tick.
     */
    long getGameTick();

    /**
     * Returns the property of the player that is being affected in the transaction.
     * This could be any attribute or characteristic related to the player, such as health, score, position, etc.
     *
     * @return PlayerProperty representing the player's property.
     */
    PlayerProperty getProperty();

    /**
     * Returns the new value that the affected property is being changed to in the transaction.
     *
     * @return Object representing the new value of the property. The actual type of this object will depend on the property being affected.
     */
    Object getNewValue();
}