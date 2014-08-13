// Package Declaration
package me.iffa.bspace;

// Java Imports
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

// bSpace Imports
import me.iffa.bspace.api.SpaceAddon;
import me.iffa.bspace.api.SpaceWorldHandler;
import me.iffa.bspace.api.schematic.SpaceSchematicHandler;
import me.iffa.bspace.commands.SpaceCommandHandler;
import me.iffa.bspace.config.SpaceConfig;
import me.iffa.bspace.config.SpaceConfig.ConfigFile;
import me.iffa.bspace.config.SpaceConfigUpdater;
import me.iffa.bspace.economy.Economy;
import me.iffa.bspace.handlers.AddonHandler;
import me.iffa.bspace.handlers.ConfigHandler;
import me.iffa.bspace.handlers.LangHandler;
import me.iffa.bspace.handlers.MessageHandler;
import me.iffa.bspace.handlers.WorldHandler;
import me.iffa.bspace.listeners.SpaceEconomyListener;
import me.iffa.bspace.listeners.SpaceEntityListener;
import me.iffa.bspace.listeners.SpacePlayerListener;
import me.iffa.bspace.listeners.SpaceSuffocationListener;
import me.iffa.bspace.listeners.misc.BlackHolePlayerListener;
import me.iffa.bspace.listeners.misc.SpaceWorldListener;
import me.iffa.bspace.wgen.planets.PlanetsChunkGenerator;

// Bukkit Imports
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class of bSpace.
 *
 * @author iffa
 * @author kitskub
 * @author HACKhalo2
 */
public class Space extends JavaPlugin 
{
	private final int projectId =  83794;
	private final String apiKey = "7a99d55450a225335559c13c44607f248a2533e8";

	// Variables
    private static String prefix;
    private static String version;
    private static Map<Player, Location> locCache = null;
    private static Map<Player, Boolean> jumpPressed = new HashMap<Player, Boolean>();
    private PluginManager pm;
    private SpaceCommandHandler sce = null;
    private Economy economy;
    private final SpaceEntityListener entityListener = new SpaceEntityListener();
    private final SpaceWorldListener worldListener = new SpaceWorldListener();
    private final SpacePlayerListener playerListener = new SpacePlayerListener(this);
    private final SpaceSuffocationListener suffocationListener = new SpaceSuffocationListener(this);
    private final SpaceEconomyListener economyListener = new SpaceEconomyListener();
    private boolean isAutoUpdate = false;
    private boolean isUpdateCheck = false;
	private Update update; // = new Update(projectId, apiKey);

    
    /**
     * Called when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        for (SpaceAddon addon : AddonHandler.addons) {
            addon.onSpaceDisable();
        }
        // Finishing up disablation.
        MessageHandler.print(Level.INFO, LangHandler.getDisabledMessage());
    }

    /**
     * Called when the plugin is enabled.
     */
    @Override
    public void onEnable() 
    {
        // Initializing variables.
        initVariables();
        MessageHandler.debugPrint(Level.INFO, "Initialized startup variables.");

        // Loading configuration files.
        SpaceConfig.loadConfigs();
        MessageHandler.debugPrint(Level.INFO, "Loaded configuration files, now checking if they need to be updated...");
        // 	Check for Update is available and Autoupdate
		isAutoUpdate  = SpaceConfig.getConfig(ConfigFile.CONFIG).getBoolean("autoupdate", false);
		isUpdateCheck = SpaceConfig.getConfig(ConfigFile.CONFIG).getBoolean("updatecheck", false);
		// check for Updazte the configuration file
        SpaceConfigUpdater.updateConfigs();

        if (isUpdateCheck)
        {
        	update = new Update(projectId, apiKey);
        	update.query();
        	if (this.getVersion().equalsIgnoreCase(update.versionGameVersion) == false)
			{
        		update.getUpdateInfo(this.getName());
			}
        } else
        {
        	update = null;
        }

        // Registering events.
        registerEvents();

        // Loading schematic files.
        SpaceSchematicHandler.loadSchematics();

        // Loading space worlds (startup).
        WorldHandler.loadSpaceWorlds();

        // Initializing the CommandExecutor for /space.
        sce = new SpaceCommandHandler(this);
        getCommand("space").setExecutor(sce);
        MessageHandler.debugPrint(Level.INFO, "Initialized CommandExecutors.");

        // Checking if it should always be night in space worlds.
        for (World world : WorldHandler.getSpaceWorlds()) 
        {
            String id = ConfigHandler.getID(world);
            if (ConfigHandler.forceNight(id)) {
                WorldHandler.startForceNightTask(world);
                MessageHandler.debugPrint(Level.INFO, "Started night forcing task for world '" + world.getName() + "'.");
            }
        }

        // Checking if Economy is ok.
        if (economy == null && getServer().getPluginManager().getPlugin("Register") != null) {
            if (Economy.checkEconomy()) {
                economy = new Economy();
            }
        }

        // Finishing up enablation.
        MessageHandler.print(Level.INFO, LangHandler.getUsageStatsMessage());
//        try {
//            Metrics metrics = new Metrics();
//            metrics.beginMeasuringPlugin(this);
//        } catch (IOException e) {
//            // Fail silently
//            MessageHandler.debugPrint(Level.WARNING, "Failed to contact Metrics (usage stats)");
//        }
        MessageHandler.print(Level.INFO, LangHandler.getEnabledMessage());
    }

    /**
     * Initializes variables (used on startup).
     */
    private void initVariables() {
        pm = getServer().getPluginManager();
        version = getDescription().getVersion();
        prefix = "[" + getDescription().getName() + "]";
        
        if (pm.getPlugin("Spout") != null && ConfigHandler.isUsingSpout()) {
            locCache = new HashMap<Player, Location>();
        }
    }

    /**
     * Registers events for bSpace.
     */
    private void registerEvents() {
        // Registering other events.
        pm.registerEvents(worldListener, this);
        MessageHandler.debugPrint(Level.INFO, "Registered events (other).");

        // Registering entity & player events.
        pm.registerEvents(entityListener, this);
        pm.registerEvents(playerListener, this);
        pm.registerEvents(suffocationListener, this);
        pm.registerEvents(economyListener, this);
        MessageHandler.debugPrint(Level.INFO, "Registered events (entity & player).");

        // Registering events for Spout.
    }

    /**
     * Gets the default world generator of the plugin.
     *
     * @param worldName World name
     * @param id ID (cow, fish etc)
     *
     * @return ChunkGenerator to use
     */
    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        boolean realID = true;
        if (id == null || id.isEmpty() || id.length() == 0) {
            realID = false;
        }
        if (realID == false) 
        {
//            MessageHandler.debugPrint(Level.INFO, "Getting generator for '" + worldName + "' using id: '" + id + "'");
//        } else 
//        {
//            MessageHandler.debugPrint(Level.INFO, "Getting generator for '" + worldName + "' using default id,planets.");
            MessageHandler.debugPrint(Level.INFO, "No id set for world: " + worldName + " ");
        }
        WorldHandler.checkWorld(worldName);
//        if (!realID) {
//            return new PlanetsChunkGenerator("planets");
//        }
        if (SpaceConfig.getLoaded(ConfigFile.IDS) == true)
        {
            MessageHandler.debugPrint(Level.INFO, "List of ids : ");
			Map<String,Object> ids = SpaceConfig.getConfig(ConfigFile.IDS).getConfigurationSection("ids").getValues(false);
        	for (String ref : ids.keySet())
        	{
                MessageHandler.debugPrint(Level.INFO, "id : " + ref + " ");
        	}
        	id = SpaceWorldHandler.getID(worldName);
        } else
        {
        	id = "empty";
        }
        MessageHandler.debugPrint(Level.INFO, "Getting generator for '" + worldName + "' using id: " + id + "");
        return new PlanetsChunkGenerator(id);
    }

    /*
     * Some API methods
     */
    /**
     * Gets the jump pressed value. (ie = wtf is this)
     *
     * @param player Player
     *
     * @return Jump pressed
     */
    public static boolean getJumpPressed(Player player) {
        return jumpPressed.get(player);
    }

    /**
     * Sets the jump pressed value. (ie = wtf is this ??)
     *
     * @param player Player
     * @param newJumpPressed New jump pressed value
     */
    public static void setJumpPressed(Player player, boolean newJumpPressed) {
        jumpPressed.put(player, newJumpPressed);
    }

    /**
     * Gets the location cache.
     *
     * @return Location cach
     */
    public static Map<Player, Location> getLocCache() {
        return locCache;
    }

    /**
     * Gets the plugin's prefix.
     *
     * @return Prefix
     */
    public static String getPrefix() {
        return prefix;
    }

    /**
     * Gets the plugin's version.
     *
     * @return Version
     */
    public static String getVersion() {
        return version;
    }

    /**
     * Gets the Economy-class.
     *
     * @return Economy
     */
    public Economy getEconomy() {
        return economy;
    }
}