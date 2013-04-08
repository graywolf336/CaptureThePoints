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
	private static final HandlerList handlers = new HandlerList();
	private Arena arena;
	private String endMessage;
	
	/**
	 * A custom event called when a game ends in an arena.
	 * 
	 * @param arena {@link Arena}
	 * @param endMessage The message sent to the players about the game ending.
	 * @since 1.5.0-b189
	 */
	public CTPEndEvent(Arena arena, String endMessage) {
		this.arena = arena;
		this.endMessage = endMessage;
	}
	
	public Arena getArena() {
		return this.arena;
	}
	
	public void setEndMessage(String message) {
		this.endMessage = message;
	}
	
	public String getEndMessage() {
		return this.endMessage;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		return handlers;
	}
}