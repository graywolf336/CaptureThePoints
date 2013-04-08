package me.dalton.capturethepoints.events;

import me.dalton.capturethepoints.beans.Arena;
import me.dalton.capturethepoints.beans.PlayerData;
import me.dalton.capturethepoints.enums.ArenaLeaveReason;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * A custom event called <strong>after</strong> the player has been teleported back to where they were.
 * 
 * @author graywolf336
 * @since 1.5.0-b104
 * @version 1.0.0
 * @see CTPPlayerJoinEvent
 *
 */
public class CTPPlayerLeaveEvent extends Event {
	private static HandlerList handlers = new HandlerList();
	private Player player;
	private Arena arena;
	private PlayerData playerdata;
	private ArenaLeaveReason reason = ArenaLeaveReason.UNKNOWN;
	
	/**
	 * A custom event called <strong>after</strong> the player has been teleported back to where they were.
	 * 
	 * @param player	The player in which has joined a game of CTP.
	 * @param arena {@link Arena}
	 * @param playerdata {@link PlayerData}
	 * @param reason {@link ArenaLeaveReason}, defaults to UNKNOWN.
	 * @since 1.5.0-b104
	 */
	public CTPPlayerLeaveEvent(Player player, Arena arena, PlayerData playerdata, ArenaLeaveReason reason) {
		this.arena = arena;
		this.player = player;
		this.playerdata = playerdata;
		this.reason = reason;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Arena getArena() {
		return arena;
	}
	
	public PlayerData getPlayerData() {
		return playerdata;
	}
	
	public ArenaLeaveReason getReason() {
		return reason;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		return handlers;
	}
}
