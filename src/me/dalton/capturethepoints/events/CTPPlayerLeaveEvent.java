package me.dalton.capturethepoints.events;

import me.dalton.capturethepoints.beans.ArenaData;
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
	private HandlerList handlers = new HandlerList();
	private Player player;
	private ArenaData arenadata;
	private PlayerData playerdata;
	private ArenaLeaveReason reason = ArenaLeaveReason.UNKNOWN;
	
	/**
	 * A custom event called <strong>after</strong> the player has been teleported back to where they were.
	 * 
	 * @param player	The player in which has joined a game of CTP.
	 * @param arenadata {@link ArenaData}
	 * @param playerdata {@link PlayerData}
	 * @param reason {@link ArenaLeaveReason}, defaults to UNKNOWN.
	 * @since 1.5.0-b104
	 */
	public CTPPlayerLeaveEvent(Player player, ArenaData arenadata, PlayerData playerdata, ArenaLeaveReason reason) {
		this.arenadata = arenadata;
		this.player = player;
		this.playerdata = playerdata;
		this.reason = reason;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public ArenaData getArenaData() {
		return arenadata;
	}
	
	public PlayerData getPlayerData() {
		return playerdata;
	}
	
	public ArenaLeaveReason getReason() {
		return reason;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
}
