package me.dalton.capturethepoints.events;

import me.dalton.capturethepoints.beans.Arena;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * A custom event called <strong>after</strong> the player has been teleported to the arena.
 * 
 * @author graywolf336
 * @since 1.5.0-b104
 * @version 1.0.0
 * @see CTPPlayerLeaveEvent
 *
 */
public class CTPPlayerJoinEvent extends Event implements Cancellable {
	private static HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private Player player;
	private Arena arena;
	
	/**
	 * A custom event called <strong>before</strong> the player has been teleported to the arena's lobby.
	 * 
	 * @param player	The player in which has joined a game of CTP.
	 * @param arena {@link Arena}
	 * @since 1.5.0-b104
	 */
	public CTPPlayerJoinEvent(Player player, Arena arena) {
		this.cancelled = false;
		this.arena = arena;
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Arena getArena() {
		return arena;
	}

	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		return handlers;
	}
}