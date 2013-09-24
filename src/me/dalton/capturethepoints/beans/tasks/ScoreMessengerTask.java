package me.dalton.capturethepoints.beans.tasks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.beans.Arena;
import me.dalton.capturethepoints.beans.Team;

public class ScoreMessengerTask {
	private CaptureThePoints pl;
    private Arena arena;
    private Task task;
    private boolean scheduled;
    
    public ScoreMessengerTask(CaptureThePoints plugin, Arena arena) {
    	this.pl = plugin;
    	this.arena = arena;
    	scheduled = false;
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
    		task.start(arena.getConfigOptions().scoreAnnounceTime * 20);
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
        public synchronized void start(int delay) {
            id = arena.scheduleDelayedRepeatingTask(this, delay, delay);
        }
        
        public void run() {
    		synchronized(this) {
    			if (arena.getStatus().isRunning()) {
                    String s = "";
                    for (Team team : arena.getTeams())
                        s = s + team.getChatColor() + team.getColor().toUpperCase() + ChatColor.WHITE + " score: " + team.getScore() + ChatColor.AQUA + " // ";
                    
                    int scoreToWin = arena.getConfigOptions().scoreToWin;
                    
                    for (String player : arena.getPlayersData().keySet()) {
                    	Player p = pl.getServer().getPlayerExact(player);
                    	pl.sendMessage(p, "Max Score: " + ChatColor.GOLD + scoreToWin);
                    	pl.sendMessage(p, s);
                    }
                }
    		}
        }
    }
}
