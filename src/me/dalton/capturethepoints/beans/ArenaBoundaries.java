package me.dalton.capturethepoints.beans;

import org.bukkit.util.Vector;

/**
 *
 * @author Humsas
 */

public class ArenaBoundaries {
//    public String arenaName;
    private String world;
    private Vector corner1, corner2;
    
    public void setWorld(String world) {
    	this.world = world;
    }
    
    public String getWorld() {
    	return this.world;
    }
    
    /** Sets the first corner to the given block coords. */
    public void setFirstCorner(int x, int y, int z) {
    	this.corner1 = new Vector(x, y, z);
    }
    
    /** Sets the first corner to the given vector. */
    public void setFirstVector(Vector corner) {
    	this.corner1 = corner.clone();
    }
    
    /** Returns the first corner of this arena in {@link Vector} form. */
    public Vector getFirstCorner() {
    	return this.corner1;
    }
    
    /** Sets the second corner to the given block coords. */
    public void setSecondCorner(int x, int y, int z) {
    	this.corner2 = new Vector(x, y, z);
    }
    
    /** Sets the second corner to the given vector. */
    public void setSecondVector(Vector corner) {
    	this.corner2 = corner.clone();
    }
    
    /** Returns the second corner of this arena in {@link Vector} form. */
    public Vector getSecondCorner() {
    	return this.corner2;
    }
}