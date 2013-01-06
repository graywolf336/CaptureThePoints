package me.dalton.capturethepoints.events;

import me.dalton.capturethepoints.beans.ArenaData;
import me.dalton.capturethepoints.beans.PlayerData;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * A custom event called when the player dies <strong>inside</strong> of a CTP Game.
 * 
 * <p />
 * 
 * If you want to see when a player leaves the arena, then see the event {@link CTPPlayerLeaveEvent}
 * 
 * @author graywolf336
 * @since 1.5.0-b104
 * @version 1.0.0
 *
 */
public class CTPPlayerDeathEvent extends Event {
	private HandlerList handlers = new HandlerList();
	private Player player;
	private ArenaData arenadata;
	private PlayerData playerdata;
	
	/**
	 * A custom event called when the player dies <strong>inside</strong> of a CTP Game.
	 * 
	 * <p />
	 * 
	 * If you want to see when a player leaves the arena, then see the event {@link CTPPlayerLeaveEvent}
	 * 
	 * @param player	The player in which has joined a game of CTP.
	 * @param arenadata {@link ArenaData}
	 * @param playerdata {@link PlayerData}
	 * @since 1.5.0-b104
	 */
	public CTPPlayerDeathEvent(Player player, ArenaData arenadata, PlayerData playerdata) {
		this.arenadata = arenadata;
		this.player = player;
		this.playerdata = playerdata;
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
	
	public HandlerList getHandlers() {
		return handlers;
	}
}