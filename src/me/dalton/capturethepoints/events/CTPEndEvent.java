package me.dalton.capturethepoints.events;

import me.dalton.capturethepoints.beans.Arena;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event for when a game in an arena ends, calls before all the resetting happens.
 * 
 * @author graywolf336
 * @version 1.0.0
 * @since 1.5.0-b189
 */
public class CTPEndEvent extends Event {
	private HandlerList handlers = new HandlerList();
	private Arena arena;
	
	/**
	 * A custom event called when a game ends in an arena.
	 * 
	 * @param arena {@link Arena}
	 * @since 1.5.0-b189
	 */
	public CTPEndEvent(Arena arena) {
		this.arena = arena;
	}
	
	public Arena getArena() {
		return this.arena;
	}
	
	public HandlerList getHandlers() {
		return this.handlers;
	}
}
