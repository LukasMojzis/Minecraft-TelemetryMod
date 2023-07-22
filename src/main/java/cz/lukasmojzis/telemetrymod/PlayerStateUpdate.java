/**
 * The cz.lukasmojzis.telemetrymod package contains classes related to telemetry and event notifications
 * in the context of player interactions and properties within the game.
 */
package cz.lukasmojzis.telemetrymod;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents a state update for a player in the game.
 * It holds information about a change in player state at a particular game tick.
 */
@XmlRootElement(name = "PlayerStateUpdate")
public class PlayerStateUpdate implements ITransaction {
    /**
     * The name of the player associated with the state update.
     */
    @XmlAttribute(name = "player")
    final String player;

    /**
     * The property of the player that changed in this state update.
     */
    @XmlAttribute(name = "property")
    final PlayerProperty property;

    /**
     * The old value of the property before the state change.
     */
    @XmlAttribute(name = "oldValue")
    final Object oldValue;

    /**
     * The new value of the property after the state change.
     */
    @XmlAttribute(name = "newValue")
    final Object newValue;

    /**
     * The game tick when the state change occurred.
     */
    @XmlAttribute(name = "gameTick")
    final long gameTick;

    /**
     * Constructor to create a new PlayerStateUpdate.
     *
     * @param player   The name of the player associated with the state update.
     * @param gameTick The game tick when the state change occurred.
     * @param property The property of the player that changed in this state update.
     * @param oldValue The old value of the property before the state change.
     * @param newValue The new value of the property after the state change.
     */
    public PlayerStateUpdate(String player, long gameTick, PlayerProperty property, Object oldValue, Object newValue) {
        this.player = player;
        this.gameTick = gameTick;
        this.property = property;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * Gets the name of the player associated with the state update.
     *
     * @return The name of the player.
     */
    @Override
    public String getPlayer() {
        return player;
    }

    /**
     * Gets the game tick when the state change occurred.
     *
     * @return The game tick.
     */
    @Override
    public long getGameTick() {
        return gameTick;
    }

    /**
     * Gets the property of the player that changed in this state update.
     *
     * @return The property of the player.
     */
    @Override
    public PlayerProperty getProperty() {
        return property;
    }

    /**
     * Gets the new value of the property after the state change.
     *
     * @return The new value of the property.
     */
    @Override
    public Object getNewValue() {
        return newValue;
    }
}
