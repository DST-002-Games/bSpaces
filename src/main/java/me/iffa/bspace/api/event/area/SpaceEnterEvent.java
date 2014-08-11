// Package Declaration
package me.iffa.bspace.api.event.area;

// Bukkit Imports
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Event data for when a player enters space. 
 * If player is joining server, location from will be null.
 * 
 * @author iffa
 */
public class SpaceEnterEvent extends SpaceWorldAreaEvent{
    // Variables
    private static final HandlerList handlers = new HandlerList();
    private static final long serialVersionUID = 8744071438699676557L;
    private Location to = null;
    private Location from = null;

    /**
     * Constructor for SpaceEnterEvent
     * 
     * 
     * @param player Player
     * @param from Where the player teleports from null if from respawn or server join
     * @param to Where the player teleports to
     */
    public SpaceEnterEvent(Player player, Location from, Location to) {
        super("SpaceEnterEvent", player);
        this.from = from;
        this.to = to;
    }

    /**
     * Gets the destination of the teleport.
     * 
     * @return Destination of the teleport
     */
    public Location getTo() {
        return this.to;
    }

    /**
     * Gets where the player is trying to teleport from.
     * 
     * @return Where the player is trying to teleport from
     */
    public Location getFrom() {
        return this.from;
    }
    
    /**
     * {@inheritDoc}
     * @return Handler list
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
