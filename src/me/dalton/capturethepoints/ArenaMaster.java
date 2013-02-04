package me.dalton.capturethepoints;

import java.util.LinkedList;
import java.util.List;

import me.dalton.capturethepoints.beans.Arena;

public class ArenaMaster {
	//mob arena style! thanks to mob arena for being on github! :)
	private CaptureThePoints ctp;
	
	private List<Arena> arenas;
	private Arena selectedArena;
	
	public ArenaMaster(CaptureThePoints plugin) {
		this.ctp = plugin;
		
		this.arenas = new LinkedList<Arena>();
	}
	
	public CaptureThePoints getPlugin() {
		return this.ctp;
	}
	
	public void setSelectedArena(Arena a) {
		this.selectedArena = a;
	}
	
	public Arena getSelectedArena() {
		return this.selectedArena;
	}
	
	public Arena getArena(String name) {
		for(Arena a : arenas)
			if(a.getName().equalsIgnoreCase(name))
				return a;
			else
				continue;
		
		return null;
	}
	
	/**
	 * Gets the current list of all the arenas we have loaded.
	 * <p />
	 * 
	 * @return The list of the arenas loaded.
	 * @author graywolf336
	 * @since 1.5.0-b123
	 */
	public List<Arena> getArenas() {
		return this.arenas;
	}
	
	/**
	 * Clears out the arena list, no kicking players here.
	 * <p />
	 * 
	 * @author graywolf336
	 * @since 1.5.0-b123
	 */
	public void resetArenas() {
		this.arenas.clear();
	}
	
	/**
	 * Returns the arena that the player is currently playing in.
	 * <p />
	 * 
	 * @param p The player to check, as a string.
	 * @return The arena if the player is in one, null if none.
	 * @author graywolf336
	 * @since 1.5.0-b123
	 */
	public Arena getArenaPlayerIsIn(String p) {
		for(Arena a : arenas) {
			if(a.getPlayerList().contains(p))
				return a;
			else
				continue;
		}
		
		return null;
	}
	
	/**
	 * Returns whether the player is currently in a arena or game.
	 * <p />
	 * 
	 * @param player The player to check, as a string.
	 * @return True if the player is somewhere, false if not.
	 * @author graywolf336
	 * @since 1.5.0-b123
	 */
	public boolean isPlayerInAnArena(String player) {
		for(Arena a : arenas) {
			if(a.getPlayerList().contains(player))
				return true;
			else
				continue;
		}
		
		return false;
	}
}
