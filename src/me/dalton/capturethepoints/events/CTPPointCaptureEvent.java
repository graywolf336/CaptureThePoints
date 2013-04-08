package me.dalton.capturethepoints.events;

import me.dalton.capturethepoints.beans.Arena;
import me.dalton.capturethepoints.beans.PlayerData;
import me.dalton.capturethepoints.beans.Points;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event for when a player captures a point, called after the point is captured.
 * 
 * @author graywolf336
 * @version 1.0.0
 * @since 1.5.0-b181
 */
public class CTPPointCaptureEvent extends Event {
	private static HandlerList handlers = new HandlerList();
	private Arena arena;
	private Player player;
	private PlayerData playerdata;
	private Points point;
	
	/**
	 * A custom event called <strong>after</strong> the player has captured a point.
	 * 
	 * @param arena {@link Arena}
	 * @param player The player in which has captured the point.
	 * @param playerdata {@link PlayerData}
	 * @param point {@link Points}
	 * @since 1.5.0-b181
	 */
	public CTPPointCaptureEvent(Arena arena, Player player, PlayerData playerdata, Points point) {
		this.arena = arena;
		this.player = player;
		this.playerdata = playerdata;
		this.point = point;
	}
	
	public Arena getArena() {
		return this.arena;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public PlayerData getPlayerData() {
		return this.playerdata;
	}
	
	public Points getPoint() {
		return this.point;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		return handlers;
	}
}
