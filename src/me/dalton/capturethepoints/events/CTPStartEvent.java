package me.dalton.capturethepoints.events;

import me.dalton.capturethepoints.beans.Arena;

import org.bukkit.event.HandlerList;

/**
 * Event for when a game in an arena starts, calls before all players are teleported to the team spawns..
 * 
 * @author graywolf336
 * @version 1.0.0
 * @since 1.5.0-b189
 */
public class CTPStartEvent {
	private HandlerList handlers = new HandlerList();
	private Arena arena;
	
	/**
	 * A custom event called when a game starts in an arena.
	 * 
	 * @param arena {@link Arena}
	 * @since 1.5.0-b189
	 */
	public CTPStartEvent(Arena arena) {
		this.arena = arena;
	}
	
	public Arena getArena() {
		return this.arena;
	}
	
	public HandlerList getHandlers() {
		return this.handlers;
	}
}