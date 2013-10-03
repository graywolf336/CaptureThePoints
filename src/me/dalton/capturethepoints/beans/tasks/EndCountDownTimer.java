package me.dalton.capturethepoints.beans.tasks;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.beans.Arena;

import org.bukkit.Bukkit;

public class EndCountDownTimer {
	private CaptureThePoints pl;
    private Arena arena;
    private int seconds;
    private Timer timer;
    private boolean started;
    
    public EndCountDownTimer(CaptureThePoints plugin, Arena arena, int seconds) {
    	this.pl = plugin;
    	this.arena = arena;
    	this.seconds = seconds;
    	this.started = false;
    }
    
    /**
     * Starts the timer.
     * The method is idempotent, meaning if the timer was already
     * started, nothing happens if the method is called again.
     */
    public void start(boolean rewards) {
        if (seconds > 5 && !started) {
            timer = new Timer(rewards, seconds);
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
    	private boolean rewards;
        private int remaining;
        private int id;
        
        private Timer(boolean rewards, int seconds) {
        	this.rewards = rewards;
            this.remaining = seconds;
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
                if (arena.getPlayers().size() == 0) {
                    started = false;
                    this.notifyAll();
                    Bukkit.getScheduler().cancelTask(id);
                    return;
                }
                
                // Start if 0
                if (remaining <= 0) {
                	started = false;
                    
    				arena.endGameNoCountDown(rewards);
                    Bukkit.getScheduler().cancelTask(id);
                } else {
                	if(arena.getConfigOptions().endCountDownTime == remaining) {
                		arena.sendMessageToPlayers(pl.getLanguage().END_COUNTDOWN.replaceAll("%CS", String.valueOf(remaining)));
                	}else {
                		arena.sendMessageToPlayers(remaining + "..");

                	}
                }
                
                // Count down after everything, this ensures that the first number is broadcasted.
                remaining--;
                
                this.notifyAll();
            }
        }
    }
}
