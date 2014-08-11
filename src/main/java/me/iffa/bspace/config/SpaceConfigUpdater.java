// Package Declaration
package me.iffa.bspace.config;

// Java Imports
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

// bSpace Imports
import me.iffa.bspace.config.SpaceConfig.ConfigFile;
import me.iffa.bspace.handlers.LangHandler;
import me.iffa.bspace.handlers.MessageHandler;

// Bukkit Imports
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Converts old pre-v2 worlds into v2 IDs.
 * 
 * @author iffa
 */
public class SpaceConfigUpdater {
    // Variables
    private static boolean hadToBeUpdated = false;

    /**
     * Checks if the configuration files had to be "fixed".
     * 
     * @return True if had to be updated
     */
    public static boolean wasUpdated() {
        return hadToBeUpdated;
    }

    /**
     * Checks if a config file needs updating to v2.
     * 
     * @param configfile ConfigFile to check
     * 
     * @return True if needs updating
     */
    private static boolean needsUpdate(ConfigFile configfile) {
        if(configfile.equals(ConfigFile.CONFIG)){
            if (SpaceConfig.getConfig(configfile).contains("worlds")) {
                try {
                    for (String world : SpaceConfig.getConfig(configfile).getConfigurationSection("worlds").getKeys(false)) {
                        if (SpaceConfig.getConfig(configfile).getConfigurationSection("worlds." + world).contains("generation")) {
                            hadToBeUpdated = true;
                            return true;
                        }
                    }
                } catch (NullPointerException ex) {
                    return false;
                }
            }
        }
	if(configfile.equals(ConfigFile.IDS)){
	    for(String id : SpaceConfig.getConfig(configfile).getConfigurationSection("ids").getKeys(false)){
		if(SpaceConfig.getConfig(configfile).contains("ids."+id+".generation.spout-only.blackholes")){
		    hadToBeUpdated = true;
		    return true;
		}
	    }
	}
        return false;
    }

    /**
     * Updates config files to be compatible with latest.
     */
    public static void updateConfigs() {
        if (needsUpdate(ConfigFile.CONFIG)) {
            // Variables
            MessageHandler.print(Level.INFO, LangHandler.getConfigUpdateStartMessage());
            YamlConfiguration configFile = SpaceConfig.getConfig(ConfigFile.CONFIG);
            YamlConfiguration idsFile = SpaceConfig.getConfig(ConfigFile.IDS);

            for (String world : configFile.getConfigurationSection("worlds").getKeys(false)) {
                // Generation values
                for (String key : configFile.getConfigurationSection("worlds." + world + "." + "generation").getKeys(false)) {
                    Object value = configFile.get("worlds." + world + ".generation." + key);
                    idsFile.set("ids." + world + ".generation." + key, value);
                    MessageHandler.debugPrint(Level.INFO, "Moved " + key + " of " + world + " to ids.yml with a value of " + value);
                }
                // Suit values
                for (String key : configFile.getConfigurationSection("worlds." + world + "." + "suit").getKeys(false)) {
                    Object value = configFile.get("worlds." + world + ".suit." + key);
                    idsFile.set("ids." + world + ".suit." + key, value);
                    MessageHandler.debugPrint(Level.INFO, "Moved " + key + " of " + world + " to ids.yml with a value of " + value);
                }
                // Helmet values
                for (String key : configFile.getConfigurationSection("worlds." + world + "." + "helmet").getKeys(false)) {
                    Object value = configFile.get("worlds." + world + ".helmet." + key);
                    idsFile.set("ids." + world + ".helmet." + key, value);
                    MessageHandler.debugPrint(Level.INFO, "Moved " + key + " of " + world + " to ids.yml with a value of " + value);
                }
                // Misc. values
                for (String key : configFile.getConfigurationSection("worlds." + world).getKeys(false)) {
                    // So we don't make bad things happen. Skrillex (Y)
                    if (key.equalsIgnoreCase("generation") || key.equalsIgnoreCase("suit") || key.equalsIgnoreCase("helmet")) {
                        continue;
                    }
                    Object value = configFile.get("worlds." + world + "." + key);
                    idsFile.set("ids." + world + "." + key, value);
                    MessageHandler.debugPrint(Level.INFO, "Moved " + key + " of " + world + " to ids.yml with a value of " + value);
                }

                // Removing the world from config.yml.
                configFile.set("worlds." + world, null);
                MessageHandler.debugPrint(Level.INFO, "Removed " + world + " from config.yml.");
            }
            // Saving both files since converting is done.
            try {
                configFile.save(SpaceConfig.getConfigFile(ConfigFile.CONFIG));
                idsFile.save(SpaceConfig.getConfigFile(ConfigFile.IDS));
                MessageHandler.debugPrint(Level.INFO, "Saved changes to ids and config.yml.");
            } catch (IOException ex) {
                // In case of any error.
                MessageHandler.print(Level.SEVERE, LangHandler.getConfigUpdateFailureMessage(ex));
            }
            // It was all done.
            MessageHandler.print(Level.INFO, LangHandler.getConfigUpdateFinishMessage());
        }

	if (needsUpdate(ConfigFile.IDS)) {
            // Variables
            MessageHandler.print(Level.INFO, LangHandler.getConfigUpdateStartMessage());
            YamlConfiguration idsFile = SpaceConfig.getConfig(ConfigFile.IDS);

            for (String id : idsFile.getConfigurationSection("ids").getKeys(false)) {
		if(idsFile.contains("ids."+id+".generation.spout-only.blackholes")){
		    idsFile.set("ids."+id+".generation.spoutblackholes", idsFile.get("ids."+id+".generation.spout-only.blackholes"));
		    idsFile.set("ids."+id+".generation.spout-only.blackholes", null);
		}
            }

            // Saving files since converting is done.
            try {
                idsFile.save(SpaceConfig.getConfigFile(ConfigFile.IDS));
                MessageHandler.debugPrint(Level.INFO, "Saved changes to ids.yml.");
            } catch (IOException ex) {
                // In case of any error.
                MessageHandler.print(Level.SEVERE, LangHandler.getConfigUpdateFailureMessage(ex));
            }
            // It was all done.
            MessageHandler.print(Level.INFO, LangHandler.getConfigUpdateFinishMessage());
        }

        File oldPlanets = new File(Bukkit.getPluginManager().getPlugin("bSpace").getDataFolder(), "planets.yml");
        File newPlanets = new File(Bukkit.getPluginManager().getPlugin("bSpace").getDataFolder(), "planets/planets.yml");
        if(oldPlanets.exists()){
            if(newPlanets.exists()){
                newPlanets.delete();
            }
            oldPlanets.renameTo(newPlanets);
            SpaceConfig.loadConfig(ConfigFile.DEFAULT_PLANETS);
        }
    }

    /**
     * Constructor of SpaceConfigUpdater.
     */
    private SpaceConfigUpdater() {
    }
}
