// Package Declaration
package me.iffa.bspace.wgen.planets;

// Java Imports
import java.io.Serializable;
import java.util.Set;

// Bukkit Imports
import org.bukkit.material.MaterialData;

/**
 * Holder class for an individual planetoid.
 * 
 * @author Canis85
 * @author iffa
 */
public class Planetoid implements Serializable {
    // Variables
    private static final long serialVersionUID = 1L;
    public Set<MaterialData> coreBlkIds;
    public Set<MaterialData> shellBlkIds;
    public int shellThickness;
    public int radius;
    public int xPos;
    public int yPos;
    public int zPos;

    /**
     * Constructor of Planetoid.
     */
    public Planetoid() {
    }

    /**
     * Constructor of Planetoid.
     * 
     * @param coreID Core material
     * @param shellID Shell material
     * @param shellThick Shell thickness
     * @param radius Radius
     * @param x X-coord
     * @param y Y-coord
     * @param z Z-coord
     */
    public Planetoid(Set<MaterialData> coreID, Set<MaterialData> shellID, int shellThick, int radius, int x, int y, int z) {
        this.coreBlkIds = coreID;
        this.shellBlkIds = shellID;
        this.shellThickness = shellThick;
        this.radius = radius;
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
    }
}
