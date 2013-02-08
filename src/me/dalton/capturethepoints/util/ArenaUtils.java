package me.dalton.capturethepoints.util;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.beans.Team;

public class ArenaUtils {
	private CaptureThePoints ctp;
	
	public ArenaUtils(CaptureThePoints ctp) {
		this.ctp = ctp;
	}
	
    /**
     * Returns a Team from the color provided.
     * 
     * @param ctp CaptureThePoints instance
     * @param arena The arena to check
     * @param color The color of the team to get
     * @return The Team from the color given.
     * @since 1.5.0-b122
     */
    public Team getTeamFromColor(CaptureThePoints ctp, String arena, String color) {
    	for (Team t : ctp.getArenaMaster().getArena(arena).getTeams())
    		if(t.getColor().equalsIgnoreCase(color))
    			return t;
    	
    	return null;
    }
    
    /**
     * Heal the player (set the health) and cause an event to happen from it, thus improving relations with other plugins.
     * 
     * @param player The player to heal.
     * @param amount The amount to heal the player.
     */
    public void healPlayerAndCallEvent(Player player, int amount) {
    	player.setHealth(amount);
    	EntityRegainHealthEvent regen = new EntityRegainHealthEvent(player, amount, RegainReason.CUSTOM);
    	ctp.getPluginManager().callEvent(regen);
    }
}
