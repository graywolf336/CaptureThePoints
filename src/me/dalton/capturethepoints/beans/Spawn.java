package me.dalton.capturethepoints.beans;

/** A Spawn Point in a CTP arena */
public class Spawn {
	private double x;
	private double y;
	private double z;
	private double dir = 0.0D;
	private String name;
	
	/** Sets the X coordinate of this Spawn */
	public void setX(double x) {
		this.x = x;
	}
	
	/** Gets the X coordinate of this Spawn */
	public double getX() {
		return this.x;
	}
	
	/** Sets the Y coordinate of this Spawn */
	public void setY(double y) {
		this.y = y;
	}
	
	/** Gets the Y coordinate of this Spawn */
	public double getY() {
		return this.y;
	}
	
	/** Sets the Z coordinate of this Spawn */
	public void setZ(double z) {
		this.z = z;
	}
	
	/** Gets the Z coordinate of this Spawn */
	public double getZ() {
		return this.z;
	}
	
	/** Sets the direction that players Spawn at here. */
	public void setDir(double dir) {
		this.dir = dir;
	}
	
	/** Gets the direction that players Spawn at here. */
	public double getDir() {
		return this.dir;
	}
	
	/** Sets the name of this Spawn. */
	public void setName(String name) {
		this.name = name;
	}
	
	/** Gets the name of this Spawn. */
	public String getName() {
		return this.name;
	}
}
