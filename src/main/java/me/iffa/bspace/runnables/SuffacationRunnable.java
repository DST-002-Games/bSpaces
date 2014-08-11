// Package Declaration
package me.iffa.bspace.runnables;

// bSpace Imports
import java.util.logging.Level;
import me.iffa.bspace.api.event.misc.SpaceSuffocationEvent;
import me.iffa.bspace.config.SpaceConfig;
import me.iffa.bspace.handlers.MessageHandler;
import me.iffa.bspace.handlers.PlayerHandler;
import me.iffa.bspace.listeners.SpaceSuffocationListener;

// Bukkit Imports
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

/**
 * A runnable class for suffocating.
 * 
 * @author iffa
 */
public class SuffacationRunnable implements Runnable {
    // Variables
    private final Player player;
    private boolean suffocating = false;
    private double damage = 2.0;
    /**
     * Constructor for SuffacationRunnable.
     * 
     * @param player Player
     */
    public SuffacationRunnable(Player player) {
        this.player = player;
    }

    /**
     * Suffocates the player when repeated every second.
     */
    @Override
    public void run() {
        if (player.isDead() == false) 
        {
            if(PlayerHandler.checkNeedsSuffocation(player))
            {
                if(suffocating  == false)
                {
                    /* Notify listeners start */
                    SpaceSuffocationEvent e = new SpaceSuffocationEvent(player);
                    Bukkit.getServer().getPluginManager().callEvent(e);
                    if (e.isCancelled()) 
                    {
                        return;
                    }
                    /* Notify listeners end */
                    suffocating=true;
                    player.sendMessage(ChatColor.RED+"You left an area and are now suffocating.");
                    MessageHandler.debugPrint(Level.INFO, "Player '" + player.getName() + "' is now suffocating in space.");
                }
            } else 
            {
                if(suffocating == true) 
                {
                    suffocating = false;
                }
            }
            
            if(suffocating == true)
            {
                if (((Damageable)player).getHealth() < 2 && ((Damageable)player).getHealth() > 0) 
                {
                	((Damageable)player).setHealth(0D);
                    SpaceSuffocationListener.stopSuffocating(player);
                    return;
                } else if (player.getHealthScale() <= 0) 
                {
                    SpaceSuffocationListener.stopSuffocating(player);
                    return;
                }
                
                ((Damageable)player).setHealth(((Damageable)player).getHealth()-damage);
                MessageHandler.debugPrint(Level.INFO, "Player '" + player.getName() + "' damage: 2");
            }
        } else 
        {
            SpaceSuffocationListener.stopSuffocating(player);
        }
    }
}
