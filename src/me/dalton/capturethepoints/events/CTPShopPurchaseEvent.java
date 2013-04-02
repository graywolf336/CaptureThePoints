package me.dalton.capturethepoints.events;

import me.dalton.capturethepoints.beans.Arena;
import me.dalton.capturethepoints.beans.PlayerData;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Event for when a player buys something from a shop in an arena, called before we give them items.
 * 
 * @author graywolf336
 * @version 1.0.0
 * @since 1.5.0-b195
 */
public class CTPShopPurchaseEvent extends Event implements Cancellable {
	private HandlerList handlers = new HandlerList();
	private boolean cancel;
	private Arena arena;
	private Player player;
	private PlayerData playerdata;
	private Sign sign;
	private ItemStack stack;
	
	/**
	 * A custom event called when a player right clicks a shop in game.
	 * 
	 * @param arena {@link Arena}
	 * @param player The player who is buying something.
	 * @param playerdata {@link PlayerData}
	 * @param sign The sign that this shop is at.
	 * @param stack The item which is being bought.
	 * @since 1.5.0-b195
	 */
	public CTPShopPurchaseEvent(Arena arena, Player player, PlayerData playerdata, Sign sign, ItemStack stack) {
		this.arena = arena;
		this.player = player;
		this.playerdata = playerdata;
		this.sign = sign;
		this.stack = stack;
	}
	
	public Arena getArena() {
		return this.arena;
	}

	public Player getPlayer() {
		return this.player;
	}
	
	public PlayerData getPlayerDate() {
		return this.playerdata;
	}
	
	public Sign getShopSign() {
		return this.sign;
	}
	
	public ItemStack getItemBought() {
		return this.stack;
	}
	
	public void setItemBought(ItemStack stack) {
		this.stack = stack;
	}
	
	public boolean isCancelled() {
		return this.cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	
	public HandlerList getHandlers() {
		return this.handlers;
	}
}