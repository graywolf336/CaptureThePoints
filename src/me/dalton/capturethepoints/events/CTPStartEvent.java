package me.dalton.capturethepoints.events;

import me.dalton.capturethepoints.beans.Arena;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event for when a game in an arena starts, calls before all players are teleported to the team spawns..
 * 
 * @author graywolf336
 * @version 1.0.0
 * @since 1.5.0-b189
 */
public class CTPStartEvent extends Event {
	private static HandlerList handlers = new HandlerList();
	private Arena arena;
	private String startMessage;
	
	/**
	 * A custom event called when a game starts in an arena.
	 * 
	 * @param arena {@link Arena}
	 * @since 1.5.0-b189
	 */
	public CTPStartEvent(Arena arena, String startMessage) {
		this.arena = arena;
		this.startMessage = startMessage;
	}
	
	public Arena getArena() {
		return this.arena;
	}
	
	public void setStartMessage(String message) {
		this.startMessage = message;
	}
	
	public String getStartMessage() {
		return this.startMessage;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		return handlers;
	}
}