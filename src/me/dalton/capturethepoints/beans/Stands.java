package me.dalton.capturethepoints.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the location for teleporting a player and who is all there.
 * 
 * @author graywolf336
 * @version 1.0.0
 * @since 1.5.0-243
 */
public class Stands {
	private List<String> playersInTheStand = new ArrayList<String>();
	private double x = 0D;
    private double y = 0D;
    private double z = 0D;
    private double dir = 0D;
	
    public Stands(double x, double y, double z, double dir) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dir = dir;
    }
    
    /** Sets the X coordinate of this stand's teleport */
    public void setX(double x) {
    	this.x = x;
    }
    
    /** Gets the X coordinate of this stand's teleport */
    public double getX() {
    	return this.x;
    }
    
    /** Sets the Y coordinate of this stand's teleport */
    public void setY(double y) {
    	this.y = y;
    }
    
    /** Gets the Y coordinate of this stand's teleport */
    public double getY() {
    	return this.y;
    }
    
    /** Sets the Z coordinate of this stand's teleport */
    public void setZ(double z) {
    	this.z = z;
    }
    
    /** Gets the Z coordinate of this stand's teleport */
    public double getZ() {
    	return this.z;
    }
    
    /** Sets the direction in which players teleport in this stand. */
    public void setDir(double dir) {
    	this.dir = dir;
    }
    
    /** Gets the direction in which players teleport in this stand. */
    public double getDir() {
    	return this.dir;
    }
    
    /** Gets the list of players in the stands. */
    public List<String> getPlayersInTheStands() {
    	return this.playersInTheStand;
    }
    
    /** Clears the list of players in the stands. */
    public void clearStandsPlayers() {
    	this.playersInTheStand.clear();
    }
}
