package me.dalton.capturethepoints.beans.tasks;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.beans.Arena;
import me.dalton.capturethepoints.beans.PlayerData;
import me.dalton.capturethepoints.beans.Points;
import me.dalton.capturethepoints.beans.Team;

public class ScoreGenerationTask {
	private CaptureThePoints pl;
    private Arena arena;
    private Task task;
    private boolean scheduled;
    
    public ScoreGenerationTask(CaptureThePoints plugin, Arena arena) {
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
            id = arena.scheduleDelayedRepeatingTask(this, 600, 600);
        }
    	
    	public void run() {
    		synchronized(this) {
    			if (arena.getStatus().isRunning()) {
                    for (PlayerData player : arena.getPlayersData().values())
                        if (player.inArena())
                        	player.setMoney(player.getMoney() + arena.getConfigOptions().moneyEvery30Sec);
                    
                    if (arena.getConfigOptions().useScoreGeneration) {
                        for (Team team : arena.getTeams()) {
                            int duplicator = 1;
                            int maxPossiblePointsToCapture = 0;
                            for (Points point : arena.getCapturePoints()) {
                                if(point.getNotAllowedToCaptureTeams() == null || !pl.getUtil().containsTeam(point.getNotAllowedToCaptureTeams(), team.getColor()))
                                    maxPossiblePointsToCapture++;
                            }

                            if (team.getControlledPoints() == maxPossiblePointsToCapture && maxPossiblePointsToCapture > 0) {
                                duplicator = arena.getConfigOptions().scoreMyltiplier;
                            }
                            
                            team.setScore(team.getScore() + (arena.getConfigOptions().onePointGeneratedScoreEvery30sec * team.getControlledPoints() * duplicator));
                        }
                    }
                    
                    pl.getArenaUtil().didSomeoneWin(arena);
                }
    			
    			this.notifyAll();
    		}
    	}
    }
}
