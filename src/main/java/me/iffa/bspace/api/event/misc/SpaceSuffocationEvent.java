// Package Declaration
package me.iffa.bspace.api.event.misc;

// Bukkit Imports
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event data for when a player suffocates.
 * 
 * @author iffa
 */
public class SpaceSuffocationEvent extends Event implements Cancellable {
    // Variables
    private static final HandlerList handlers = new HandlerList();
    private static final long serialVersionUID = 8772846319048911013L;
    private boolean canceled = false;
    private Player player = null;

    /**
     * Constructor for SpaceSuffocationEvent.
     * 
     * @param player Player
     */
    public SpaceSuffocationEvent(Player player) {
        this.player = player;
    }

    /**
     * Gets the player that is suffocating.
     * 
     * @return Player that is suffocating
     */
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.canceled = cancel;
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
