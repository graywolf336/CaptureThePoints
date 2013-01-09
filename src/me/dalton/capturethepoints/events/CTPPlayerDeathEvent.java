package me.dalton.capturethepoints.events;

import me.dalton.capturethepoints.beans.Arena;
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
	private Arena arena;
	private PlayerData playerdata;
	
	/**
	 * A custom event called when the player dies <strong>inside</strong> of a CTP Game.
	 * 
	 * <p />
	 * 
	 * If you want to see when a player leaves the arena, then see the event {@link CTPPlayerLeaveEvent}
	 * 
	 * @param player	The player in which has joined a game of CTP.
	 * @param arena {@link Arena}
	 * @param playerdata {@link PlayerData}
	 * @since 1.5.0-b104
	 */
	public CTPPlayerDeathEvent(Player player, Arena arena, PlayerData playerdata) {
		this.arena = arena;
		this.player = player;
		this.playerdata = playerdata;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Arena getArenaData() {
		return arena;
	}
	
	public PlayerData getPlayerData() {
		return playerdata;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
}