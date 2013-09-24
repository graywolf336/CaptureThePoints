package me.dalton.capturethepoints.beans.tasks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.HealingItems;
import me.dalton.capturethepoints.beans.Arena;
import me.dalton.capturethepoints.beans.PlayersAndCooldowns;

public class ItemCoolDownsTask {
	private CaptureThePoints pl;
    private Arena arena;
    private Task task;
    private boolean scheduled;
    
    public ItemCoolDownsTask(CaptureThePoints plugin, Arena arena) {
    	this.pl = plugin;
    	this.arena = arena;
    }
	
    /**
     * Schedules the task to run, if it is not already running.
     * 
     * The method is idempotent, meaning if the task was already
     * scheduled, nothing happens if the method is called again.
     */
    public void start() {
    	if(!scheduled && task == null) {
    		task = new Task();
    		task.start();
    		scheduled = true;
    	}
    }
    
    /** Stops the task in the scheduler, sets the local timer to null, and sets the scheduled to false. */
    public int cancel() {
    	if(getTaskId() != -1) {
    		pl.getServer().getScheduler().cancelTask(getTaskId());
    		task = null;
    		scheduled = false;
    	}
    	
    	return getTaskId();
    }
    
    /** Returns the task id of the scheduled task, if not scheduled then returns -1. */
    public int getTaskId() {
    	return task != null ? task.id : -1;
    }
    
	private class Task implements Runnable {
    	private int id;
    	
    	/**
         * Start the timer
         */
        public synchronized void start() {
            id = arena.scheduleDelayedRepeatingTask(this, 20, 20);
        }
    	
    	public void run() {
    		synchronized(this) {
    			if (arena.getStatus().isRunning()) {
                    for (HealingItems item : pl.getHealingItems()) {
                        if (item != null && item.cooldowns != null && item.cooldowns.size() > 0) {
                            for (PlayersAndCooldowns data : item.cooldowns.values()) {
                                Player player = pl.getServer().getPlayerExact(data.getPlayer());
                                if (data.getCooldown() == 1) {// This is cause we begin from top
                                	player.sendMessage(ChatColor.GREEN + item.item.getItem().toString().toLowerCase() + ChatColor.WHITE + " cooldown has refreshed!");
                                }

                                if (data.getHealingTimesLeft() > 0 && data.getIntervalTimeLeft() <= 0) {
                                    if (player.getHealth() + item.hotHeal > arena.getConfigOptions().maxPlayerHealth) {
                                    	pl.getArenaUtil().healPlayerAndCallEvent(player, arena.getConfigOptions().maxPlayerHealth);
                                    } else {
                                    	pl.getArenaUtil().healPlayerAndCallEvent(player, player.getHealth() + item.hotHeal);
                                    }
                                    data.setIntervalTimeLeft(item.hotInterval);
                                    data.setHealingTimesLeft(data.getHealingTimesLeft() - 1);
                                }
                                
                                data.setIntervalTimeLeft(data.getIntervalTimeLeft() - 1);
                                data.setCooldown(data.getCooldown() - 1);

                                if (data.getCooldown() <= 0 && data.getHealingTimesLeft() <= 0) {
                                    item.cooldowns.remove(data.getPlayer());
                                }
                            }
                        }
                    }
                }
    		}
    	}
	}
}
