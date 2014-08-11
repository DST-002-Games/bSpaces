// Package Declaration
package me.iffa.bspace.listeners;

// Java Imports
import java.util.HashMap;
import java.util.Map;

// bSpace Imports
import java.util.logging.Level;
import me.iffa.bspace.Space;
import me.iffa.bspace.api.event.area.AreaEnterEvent;
import me.iffa.bspace.api.event.area.AreaLeaveEvent;
import me.iffa.bspace.api.event.area.SpaceEnterEvent;
import me.iffa.bspace.api.event.area.SpaceLeaveEvent;
import me.iffa.bspace.handlers.ConfigHandler;
import me.iffa.bspace.handlers.MessageHandler;
import me.iffa.bspace.handlers.PlayerHandler;
import me.iffa.bspace.runnables.SuffacationRunnable;

// Bukkit Imports
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 *
 * @author Jack
 */
public class SpaceSuffocationListener implements Listener {
    // Variables
    public static Map<Player, Integer> taskid = new HashMap<Player, Integer>();
    private static Space plugin;

    /**
     * Constructor of SpaceSuffocationListener.
     * 
     * @param plugin Space instance
     */
    public SpaceSuffocationListener(Space plugin) {
        SpaceSuffocationListener.plugin = plugin;
    }

    /**
     * Called when someone enters an area.
     * 
     * @param event Event data 
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onAreaEnter(AreaEnterEvent event) {
        stopSuffocating(event.getPlayer());
    }

    /**
     * Called when someone leaves an area.
     * 
     * @param event Event data 
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onAreaLeave(AreaLeaveEvent event) {
        startSuffocating(event.getPlayer());
    }

    /**
     * Called when someone leaves space.
     * 
     * @param event Event data 
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onSpaceLeave(SpaceLeaveEvent event) {
        stopSuffocating(event.getPlayer());
    }

    /**
     * Called when someone enters space.
     * 
     * @param event Event data 
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onSpaceEnter(SpaceEnterEvent event) {
        if (!PlayerHandler.insideArea(event.getTo())) {
            startSuffocating(event.getPlayer(), event.getTo().getWorld());
        }
    }

    /**
     * Starts suffocation for a player.
     * This is only a convience method.
     * This should NOT be used on cross-world teleportation.
     * 
     * @param player Player to suffocate
     */
    public static void startSuffocating(Player player) {
        startSuffocating(player, player.getWorld());
    }
    
    /**
     * Starts suffocation for a player.
     * 
     * @param player Player to suffocate
     * @param world the world
     */
    public static void startSuffocating(Player player, World world) {
        if (player.hasPermission("bSpace.ignoresuitchecks")) {
            return;
        }
        String id = ConfigHandler.getID(world);
        boolean suffocatingOn = (ConfigHandler.getRequireHelmet(id) || ConfigHandler.getRequireSuit(id));
        MessageHandler.debugPrint(Level.INFO, "Started SuffocationRunnable for " + player.getName());
        if (suffocatingOn) {
            SuffacationRunnable task = new SuffacationRunnable(player);
            int taskInt = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, task, 20L, 20L);
            taskid.put(player, taskInt);
        }
    }

    /**
     * Stops a player from suffocating.
     * 
     * @param player Player to stop suffocating
     * 
     * @return True if suffocating stopped
     */
    public static boolean stopSuffocating(Player player) {
        if (!taskid.containsKey(player)) {
            return false;
        }
        if (Bukkit.getScheduler().isQueued(taskid.get(player))) {
            Bukkit.getScheduler().cancelTask(taskid.get(player));
            taskid.remove(player);
            return true;
        }
        return false;
    }
}
