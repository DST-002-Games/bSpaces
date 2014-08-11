// Package Declaration
package me.iffa.bspace.wgen.populators;

// Java Imports
import java.util.Random;

// bSpace Imports
import me.iffa.bspace.handlers.ConfigHandler;
import me.iffa.bspace.handlers.SpoutHandler;

// Bukkit Imports
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

// Spout Imports
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;

/**
 * Populates a world with black holes.
 *
 * @author kitskub
 * @author iffa
 */
public class SpaceBlackHolePopulator extends BlockPopulator {
    public static final int ID_TO_USE = 120; //for easier changing if needed
    private static boolean useSpout;

    public SpaceBlackHolePopulator(boolean spout) {
        useSpout = spout;
    }


    /**
     * Populates a chunk with black holes.
     *
     * @param world World
     * @param random Random
     * @param source Source chunk
     */
    @Override
    public void populate(World world, Random random, Chunk source) {
        String id = ConfigHandler.getID(world);
        if (withinSpawn(source)) {
            return;
        }
        if (random.nextInt(100) <= ConfigHandler.getBlackHoleChance(id)) {
            int chunkX = source.getX();
            int chunkZ = source.getZ();
            int x = random.nextInt(16);
            int z = random.nextInt(16);
            int y = random.nextInt(world.getMaxHeight());
            if(useSpout){
                SpoutBlock sb = (SpoutBlock) world.getBlockAt((chunkX * 16 + x), y, (chunkZ * 16 + z));
                SpoutManager.getMaterialManager().overrideBlock(sb, SpoutHandler.blackHole);
            }
            else {
                Block block = world.getBlockAt((chunkX * 16 + x), y, (chunkZ * 16 + z));
                block.setTypeId(ID_TO_USE);
            }
        }
    }

    /**
     * Checks if the source chunk is in the spawn area.
     * 
     * @param source Source chunk
     * 
     * @return True if within spawn
     */
    private boolean withinSpawn(Chunk source) {
        int x = source.getX();
        int z = source.getZ();

        if (x > -2 && x < 2 && z > -2 && z < 2) {
            return true;
        }
        return false;
    }
}
