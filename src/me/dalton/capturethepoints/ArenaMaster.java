package me.dalton.capturethepoints;

import java.util.LinkedList;
import java.util.List;

import me.dalton.capturethepoints.beans.Arena;
import me.dalton.capturethepoints.beans.PlayerData;

public class ArenaMaster {
	//mob arena style! thanks to mob arena for being on github! :)
	private CaptureThePoints ctp;
	
	private List<Arena> arenas;
	private String selectedArena;
	
	public ArenaMaster(CaptureThePoints plugin) {
		this.ctp = plugin;
		
		this.arenas = new LinkedList<Arena>();
	}
	
	public CaptureThePoints getPlugin() {
		return this.ctp;
	}
	
	public void setSelectedArena(String a) {
		this.selectedArena = a;
	}
	
	public Arena getSelectedArena() {
		return this.getArena(this.selectedArena);
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
	
	/**
	 * Returns the player's PlayerData, null if nothing.
	 * <p />
	 * 
	 * @param player The player to get the PlayerData for.
	 * @return PlayerData of the given player, null if not playing.
	 * @author graywolf336
	 * @since 1.5.0-b126
	 * @see PlayerData
	 */
	public PlayerData getPlayerData(String player) {
		if(isPlayerInAnArena(player)) return getArenaPlayerIsIn(player).getPlayerData(player);
		else return null;
	}
	
    /** This method finds if a suitable arena exists.
     * <p />
     * 
     * If useSelectedArenaOnly in the global configuration is true, this method will only search the main arena.
     * 
     * @param numberofplayers The number of players that want to play.
     * @return If a suitable arena exists, else false.
     */
    public boolean hasSuitableArena (int numberofplayers) {
        // No arenas built
        if (getArenas() == null || getArenas().isEmpty())
            return false;
        
        // Is the config set to allow the random choosing of arenas?
        if (!ctp.getGlobalConfigOptions().useSelectedArenaOnly) {
            int size = getArenas().size();
            if (size > 1) {
                // If there is more than 1 arena to choose from
                for (Arena arena : getArenas()) {
                    if (arena.getMaxPlayers() >= numberofplayers && arena.getMinPlayers() <= numberofplayers)
                        return true;
                }
            }
            return false;
        } else {
            if (getSelectedArena().getMaxPlayers() >= numberofplayers && getSelectedArena().getMinPlayers() <= numberofplayers) {
                return true;
            } else {
                return false;
            }
        }
    }
}
