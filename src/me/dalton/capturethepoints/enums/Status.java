package me.dalton.capturethepoints.enums;

/**
 * Contains enums for the status of the arenas.
 * 
 * @author graywolf336
 * @version 1.0.0
 * @since 1.5.0
 */
public enum Status {
	/** The arena is joinable and is not started. */
	JOINABLE (true, false),
	/** The arena is full but the game hasn't started. */
	FULL (false, false),
	/** The arena is running and is joinable (check the config for the arena first to see if it is allowed). */
	IN_GAME (true, true),
	/** The arena is running and is not joinable. */
	FULL_GAME (false, true),
	/** The arena is disabled for any reason. */
	DISABLED (false, false),
	/** The arena is in the process of being created, or something is missing and needs to be created. */
	CREATING (false, false),
	/** The default status, as a safe fall back. */
	UNKNOWN (false, false);
	
	private boolean join, running;
	private Status(boolean canJoin, boolean running) {
		this.join = canJoin;
		this.running = running;
	}
	
	/** Gets the join ability of the arena, true if they can join false if they can not. */
	public boolean getJoinAbility() {
		return this.join;
	}
	
	/** Returns if the game status is running or not. */
	public boolean isRunning() {
		return this.running;
	}
}
