package me.dalton.capturethepoints.beans;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class CTPBlock {
	private byte data;
	private Location loc;
	private int material;
	private ItemStack[] inv;
    
    public void setData(byte data) {
    	this.data = data;
    }
    
    public byte getData() {
    	return this.data;
    }
    
    public void setLocation(Location location) {
    	this.loc = location;
    }
    
    public Location getLocation() {
    	return this.loc;
    }
    
    public void setMaterial(int material) {
    	this.material = material;
    }
    
    public int getMaterial() {
    	return this.material;
    }
    
    public void setInventory(ItemStack[] inv) {
    	this.inv = inv;
    }
    
    public ItemStack[] getInventory() {
    	return this.inv;
    }
}
