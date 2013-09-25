package me.dalton.capturethepoints.beans.tasks;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.beans.Arena;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class AutoStartTimer {
	private CaptureThePoints pl;
    private Arena arena;
    private int seconds;
    private Timer timer;
    private boolean started;
    
    public AutoStartTimer(CaptureThePoints plugin, Arena arena, int seconds) {
    	this.pl		   = plugin;
        this.arena     = arena;
        this.seconds   = seconds;
        this.started   = false;
    }
    
    /**
     * Starts the timer.
     * The method is idempotent, meaning if the timer was already
     * started, nothing happens if the method is called again.
     */
    public void start() {
        if (seconds > 5 && !started) {
            timer = new Timer(seconds);
            timer.start();
            started = true;
        }
    }
    
    /** Cancels the task in the scheduler and sets the id to -1. */
	public int stop() {
		if(getTaskId() != -1) {
			pl.getServer().getScheduler().cancelTask(getTaskId());
			timer.id = -1;
			this.started = false;
		}
		
		return getTaskId();
	}
    
    public boolean isRunning() {
        return (timer != null && started);
    }
    
    public int getRemaining() {
        return timer != null ? timer.getRemaining() : -1;
    }
    
    public int getTaskId() {
    	return timer != null ? timer.id : -1;
    }
    
    /** Returns the about of seconds that the timer starts at. */
    public int getStartTime() {
    	return this.seconds;
    }
    
    /**
     * The internal timer class used for the auto-join-timer setting.
     * Using an extra internal object allows the interruption of a current
     * timer, followed by the creation of a new. Thus, no timers should
     * ever interfere with each other.
     */
    private class Timer implements Runnable {
        private int remaining;
        private int countdownIndex;
        private int id;
        private int[] intervals = new int[]{1, 2, 3, 4, 5, 10, 20, 30, 40, 45, 50, 60};
        
        private Timer(int seconds) {
            this.remaining = seconds;
            
            // Find the first countdown announcement value
            for (int i = 0; i < intervals.length; i++) {
                if (seconds > intervals[i]) {
                    countdownIndex = i;
                } else {
                    break;
                }
            }
        }
        
        /**
         * Start the timer
         */
        public synchronized void start() {
            id = arena.scheduleDelayedRepeatingTask(this, 20, 20);
        }
        
        /**
         * Get the remaining number of seconds
         * @return number of seconds left
         */
        public synchronized int getRemaining() {
            return remaining;
        }
    
        public void run() {
            synchronized(this) {
                // Abort if the arena is running, or if players have left
                if (arena.getStatus().isRunning() || arena.getPlayers().size() == 0) {
                    started = false;
                    this.notifyAll();
                    Bukkit.getScheduler().cancelTask(id);
                    return;
                }
                
                // Count down
                remaining--;
                
                // Start if 0
                if (remaining <= 0) {
                    arena.updateStatusToRunning();
                    started = false;
                    
                    arena.setMoveAbility(true);
                    arena.startOtherTasks();
                    
                    pl.getLogger().info("CaptureThePoints arena '" + arena.getName() + "' has started!");
                    pl.getUtil().sendMessageToPlayers(arena, ChatColor.ITALIC + "...Go!");
                    
                    this.notifyAll();
                    Bukkit.getScheduler().cancelTask(id);
                } else {
                	if(remaining == 5) {
                		//Teleport players to their spawns
                		//arena.setMoveAbility(false);
                	}
                	
                    // Warn at x seconds left
                    if (remaining == intervals[countdownIndex]) {
                    	pl.getUtil().sendMessageToPlayers(arena, ChatColor.ITALIC + pl.getLanguage().START_COUNTDOWN.replaceAll("%CS", String.valueOf(remaining)));
                        countdownIndex--;
                    }
                }
                
                this.notifyAll();
            }
        }
    }
}