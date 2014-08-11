// Package Declaration
package me.iffa.bspace.wgen.blocks;

// Java Imports
import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_7_R1.Block;

// Bukkit Imports
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.material.Chest;

// Spout Imports
//import org.getspout.spoutapi.block.SpoutBlock;
//import org.getspout.spoutapi.material.block.GenericCubeCustomBlock;
//import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * Represents a black hole block.
 *
 * @author Jack
 * @author iffa
 */
public class BlackHole extends Chest {
    // Variables
    private static List<Block> holesMap = new ArrayList<Block>();

    /**
     * Constructor of BlackHole.
     */
    public BlackHole() {
//        super(Bukkit.getPluginManager().getPlugin("bSpace"), "BlackHole", "http://i.imgur.com/zVBCZ.png", 16);
    }

    /**
     * Gets the list containing all black holes.
     *
     * @return List of black holes
     */
    public static List<Block> getHolesList() {
        return holesMap;
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int changedId) {
    }

    public void onBlockPlace(World world, int x, int y, int z) {
    }

    public void onBlockPlace(World world, int x, int y, int z, LivingEntity entity) {
    }

    public void onBlockDestroyed(World world, int x, int y, int z) {
    }

    public boolean onBlockInteract(World world, int x, int y, int z, Player player) {
        return true;
    }

    public void onEntityMoveAt(World world, int x, int y, int z, Entity entity) {
    }

    public void onBlockClicked(World world, int x, int y, int z, Player player) {
    }

    public boolean isProvidingPowerTo(World world, int x, int y, int z, BlockFace face) {
        return false;
    }

    public boolean isIndirectlyProvidingPowerTo(World world, int x, int y, int z, BlockFace face) {
        return false;
    }

    public void onBlockDestroyed(World world, int i, int i1, int i2, LivingEntity le) {
    }
}
