package me.dalton.capturethepoints.beans;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

/** Simpler ItemStack class  */
public class Items {
    private Material item;
    private int amount;
    private short type = -1;
    private List<Enchantment> enchantments = new LinkedList<Enchantment>();
    private List<Integer> enchLevels = new LinkedList<Integer>();
    private int money = 0;
    private int exp = 0;
    
    /** Sets the Item's Material 
     * @see org.bukkit.Material */
    public void setItem(Material item) {
    	this.item = item;
    }
    
    /** Gets the Item's Material 
     * @see org.bukkit.Material */
    public Material getItem() {
    	return this.item;
    }
    
    /** Sets the amount of this Item */
    public void setAmount(int amount) {
    	this.amount = amount;
    }
    
    /** Returns the amount of this Item. */
    public int getAmount() {
    	return this.amount;
    }
    
    /** Sets the items data type */
    public void setType(short type) {
    	this.type = type;
    }
    
    /** Gets the items data type */
    public short getType() {
    	return this.type;
    }
    
    /** Sets the items enchantments */
    public void setEnchantments(List<Enchantment> enchantments) {
    	this.enchantments = enchantments;
    }
    
    /** Gets the items enchantments */
    public List<Enchantment> getEnchantments() {
    	return this.enchantments;
    }
    
    /** Sets the items levels of enchantments */
    public void setEnchantmentLevels(List<Integer> enchantLevels) {
    	this.enchLevels = enchantLevels;
    }
    
    /** Gets the items levels of enchantments */
    public List<Integer> getEnchantmentLevels() {
    	return this.enchLevels;
    }
    
    /** Sets the money reward */
    public void setMoney(int money) {
    	this.money = money;
    }
    
    /** Gets the money reward */
    public int getMoney() {
    	return this.money;
    }
    
    /** Sets the experience reward */
    public void setExpReward(int exp) {
    	this.exp = exp;
    }
    
    /** Gets the experience reward */
    public int getExpReward() {
    	return this.exp;
    }
}
