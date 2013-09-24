package me.dalton.capturethepoints.beans;

/**
 *
 * @author Humsas
 */
public class PlayersAndCooldowns {
	private String player;
    private int cooldown = 0;
    private int healingTimesLeft = 0;
    private int intervalTimeLeft = 0;
    
    public PlayersAndCooldowns(String player) {
    	this.player = player;
    }
    
    public String getPlayer() {
    	return this.player;
    }
    
    public void setCooldown(int cooldown) {
    	this.cooldown = cooldown;
    }
    
    public int getCooldown() {
    	return this.cooldown;
    }
    
    public void setHealingTimesLeft(int left) {
    	this.healingTimesLeft = left;
    }
    
    public int getHealingTimesLeft() {
    	return this.healingTimesLeft;
    }
    
    public void setIntervalTimeLeft(int left) {
    	this.intervalTimeLeft = left;
    }
    
    public int getIntervalTimeLeft() {
    	return this.intervalTimeLeft;
    }
}