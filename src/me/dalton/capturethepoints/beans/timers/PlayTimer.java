package me.dalton.capturethepoints.beans.timers;

import java.util.HashSet;

import org.bukkit.ChatColor;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.beans.Arena;
import me.dalton.capturethepoints.beans.PlayerData;
import me.dalton.capturethepoints.beans.Team;

public class PlayTimer {
	private CaptureThePoints pl;
    private Arena arena;
    private Timer timer;
    private int time;
    private boolean scheduled;
    
    public PlayTimer(CaptureThePoints plugin, Arena arena, int time) {
    	this.pl = plugin;
    	this.arena = arena;
    	this.time = time;
    }
    
    /**
     * Schedules the task to run.
     * 
     * The method is idempotent, meaning if the task was already
     * scheduled, nothing happens if the method is called again.
     */
    public void schedule() {
    	if(!scheduled && timer == null) {
    		timer = new Timer();
    		timer.schedule();
    		scheduled = true;
    	}
    }
    
    /** Cancels the task in the scheduler, sets the local timer to null, and sets the scheduled to false. */
    public int cancel() {
    	if(getTaskId() != -1) {
    		pl.getServer().getScheduler().cancelTask(getTaskId());
    		timer = null;
    		scheduled = false;
    	}
    	
    	return getTaskId();
    }
    
    /** Returns the task id of the scheduled task, if not scheduled then returns -1. */
    public int getTaskId() {
    	return timer != null ? timer.id : -1;
    }
    
    private class Timer implements Runnable {
    	private int id;
    	
    	/**
         * Start the timer
         */
        public synchronized void schedule() {
            id = arena.scheduleDelayedTask(this, time);
        }
    	
    	public void run() {
    		synchronized(this) {
    			if ((arena.getStatus().isRunning()) && (!arena.getConfigOptions().useScoreGeneration)) {
                    int maxPoints = -9999;
                    for (Team team : arena.getTeams()) {
                        if (team.getControlledPoints() > maxPoints) {
                            maxPoints = team.getControlledPoints();
                        }
                    }
                    
                    HashSet<String> colors = new HashSet<String>();

                    for (Team team : arena.getTeams()) {
                        if (team.getControlledPoints() == maxPoints) {
                            colors.add(team.getColor());
                        }
                    }

                    for (PlayerData player : arena.getPlayersData().values()) {
                        if (player.inArena() && colors.contains(player.getTeam().getColor())) {
                        	player.setWinner(true);
                        }
                    }
                    
                    pl.getUtil().sendMessageToPlayers(arena, "Time out! " + ChatColor.GREEN + colors.toString().toUpperCase().replace(",", " and") + ChatColor.WHITE + " wins!");
                    arena.endGame(true, true); //The game ended so give rewards
                }
    			
    			this.notifyAll();
    		}
    	}
    }
}
