package me.dalton.capturethepoints.beans;

/**
 *
 * @author Humsas
 */

public class ArenaBoundaries {
//    public String arenaName;
    private String world;
    private int x1 = -1;
    private int x2 = -1;
    private int y1 = -1;
    private int y2 = -1;
    private int z1 = -1;
    private int z2 = -1;
    
    public void setWorld(String world) {
    	this.world = world;
    }
    
    public String getWorld() {
    	return this.world;
    }
    
    public void setx1(int x1) {
    	this.x1 = x1;
    }
    
    public int getx1() {
    	return this.x1;
    }
    
    public void setx2(int x2) {
    	this.x2 = x2;
    }
    
    public int getx2() {
    	return this.x2;
    }
    
    public void sety1(int y1) {
    	this.y1 = y1;
    }
    
    public int gety1() {
    	return this.y1;
    }
    
    public void sety2(int y2) {
    	this.y2 = y2;
    }
    
    public int gety2() {
    	return this.y2;
    }
    
    public void setz1(int z1) {
    	this.z1 = z1;
    }
    
    public int getz1() {
    	return this.z1;
    }
    
    public void setz2(int z2) {
    	this.z2 = z2;
    }
    
    public int getz2() {
    	return this.z2;
    }
}