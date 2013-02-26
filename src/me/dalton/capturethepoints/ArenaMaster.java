package me.dalton.capturethepoints;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.dalton.capturethepoints.beans.Arena;
import me.dalton.capturethepoints.beans.ArenaBoundaries;
import me.dalton.capturethepoints.beans.Lobby;
import me.dalton.capturethepoints.beans.PlayerData;
import me.dalton.capturethepoints.beans.Points;
import me.dalton.capturethepoints.beans.Spawn;
import me.dalton.capturethepoints.beans.Team;
import me.dalton.capturethepoints.util.Permissions;

public class ArenaMaster {
	//mob arena style! thanks to mob arena for being on github! :)
	private CaptureThePoints ctp;
	
	private List<Arena> arenas;
	private String selectedArena, editingArena;
    private HashMap<String, ArenaBoundaries> arenasBoundaries = new HashMap<String, ArenaBoundaries>();
	
	public ArenaMaster(CaptureThePoints plugin) {
		this.ctp = plugin;
		this.arenas = new LinkedList<Arena>();
	}
	
	public CaptureThePoints getPlugin() {
		return ctp;
	}
	
	public void addNewArena(Arena arena) {
		getArenas().add(arena);
	}
	
	public void removeArena(Arena a) {
		getArenas().remove(a);
	}
	
	public void setSelectedArena(String a) {
		this.selectedArena = a;
	}
	
	public void setSelectedArena(Arena a) {
		this.selectedArena = a.getName();
	}
	
	public Arena getSelectedArena() {
		return getArena(selectedArena);
	}
	
	public void clearSelectedArena() {
		this.selectedArena = null;
	}
	
	public void setEditingArena(String name) {
		this.editingArena = name;
	}
	
	public Arena getEditingArena() {
		return getArena(editingArena);
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
	 * Checks if there is an arena with the provided name.
	 * <p />
	 * 
	 * @param name The name to check if it is an arena.
	 * @return True if it is an arena, false if not.
	 * @author graywolf336
	 * @since 1.5.0-b126
	 */
	public boolean isArena(String name) {
		return getArena(name) != null;
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
	
	/** Returns the HashMap of all the arena boundaries. */
    public HashMap<String, ArenaBoundaries> getArenasBoundaries() {
    	return this.arenasBoundaries;
    }
	
	/**
	 * Returns the arena that the player is currently playing in.
	 * <p />
	 * 
	 * @param player The player to check, as a string.
	 * @return The arena if the player is in one, null if none.
	 * @author graywolf336
	 * @since 1.5.0-b123
	 */
	public Arena getArenaPlayerIsIn(String player) {
		for(Arena a : arenas) {
			if(a.getPlayerList().contains(player))
				return a;
			else
				continue;
		}
		
		return null;
	}
	
	/**
	 * Returns the arena that the player is currently playing in.
	 * <p />
	 * 
	 * @param player The player to check, as a string.
	 * @return The arena if the player is in one, null if none.
	 * @author graywolf336
	 * @since 1.5.0-b126
	 */
	public Arena getArenaPlayerIsIn(Player player) {
		return getArenaPlayerIsIn(player.getName());
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
	 * Returns whether the player is currently in a arena or game.
	 * <p />
	 * 
	 * @param player The player instance to check
	 * @return True if the player is somewhere, false if not.
	 * @author graywolf336
	 * @since 1.5.0-b126
	 */
	public boolean isPlayerInAnArena(Player player) {
		return isPlayerInAnArena(player.getName());
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
	public PlayerData getPlayerData(Player player) {
		if(isPlayerInAnArena(player)) return getArenaPlayerIsIn(player).getPlayerData(player);
		else return null;
	}
	
    /**
     * Loads all the files in the given <strong>directory</strong>.
     * 
     * @param directory The <strong>directory</strong> to load the arena files from.
     */
    public void loadArenas(File directory) {
        if (directory.isDirectory()) {
        	if(ctp.isFirstTime()) {
        		directory.mkdirs();
        		return;
        	}else
                for (String name : directory.list())
                	if (!name.startsWith("."))
                		loadArena(new File(directory.getAbsolutePath() + File.separator + name));
        }
    }
    
    private void loadArena(File file) {
    	String fileName = file.getName().split("\\.")[0];
    	if(getArena(fileName) == null)
    		getArenas().add(loadArena(fileName));//MEAT OF THE PLUGIN! Loads all the settings and stuff, important we do this.
    }
	
    /**Loads ArenaData data ready for assignment to mainArena */
    public Arena loadArena(String name) {
        Arena arena = new Arena(ctp, name);

        if (getArenas().contains(name)) {
            File arenaFile = new File(ctp.getMainDirectory() + File.separator + "Arenas" + File.separator + name + ".yml");
            FileConfiguration arenaConf = YamlConfiguration.loadConfiguration(arenaFile);
            
            String world = arenaConf.getString("World");
            
            // Kj -- check the world to see if it exists. 
            try {
            	ctp.getServer().getWorld(world);
                arena.setWorld(world);
            } catch (Exception ex) {
            	ctp.getLogger().warning(name + " has an incorrect World. The World in the config, \"" + world + "\", could not be found. ###");
                List<String> worlds = new LinkedList<String>();
                for (World aWorld : ctp.getServer().getWorlds()) {
                    worlds.add(aWorld.getName());
                }
                
                if (worlds.size() == 1) {
                    arena.setWorld(worlds.get(0));
                    ctp.getLogger().info("Successfully resolved the world. \"" + arena.getWorld() + "\" will be used.");
                } else {
                	ctp.getLogger().info("This usually happens on the first load, create an arena and this message should go away.");
                	ctp.getLogger().info("Could not resolve the world. Please fix this manually. Hint: Your installed worlds are: " + worlds);
                }
            }
            
            if(!arenaConf.contains("MaximumPlayers"))
                arenaConf.set("MaximumPlayers", 9999);
            if(!arenaConf.contains("MinimumPlayers"))
                arenaConf.set("MinimumPlayers", 4);

            arena.setMaxPlayers(arenaConf.getInt("MaximumPlayers", 9999));
            arena.setMinPlayers(arenaConf.getInt("MinimumPlayers", 4));
            if (arenaConf.contains("Points")) {
                for (String str : arenaConf.getConfigurationSection("Points").getKeys(false)) {
                    Points tmps = new Points();
                    tmps.setName(str);
                    str = "Points." + str;
                    tmps.setX(arenaConf.getInt(str + ".X", 0));
                    tmps.setY(arenaConf.getInt(str + ".Y", 0));
                    tmps.setZ(arenaConf.getInt(str + ".Z", 0));

                    // Load teams that are not allowed to capture
                    String teamColors = arenaConf.getString(str + ".NotAllowedToCaptureTeams");
                    if(teamColors == null) {
                        tmps.setNotAllowedToCaptureTeams(null);
                    } else {
                        // Trim commas and whitespace, and split items by commas
                        teamColors = teamColors.toLowerCase();
                        teamColors = teamColors.trim();
                        teamColors = teamColors.replaceAll(" ", "");

                        if (teamColors.endsWith(",")) {
                            teamColors = teamColors.substring(0, teamColors.length() - 1);
                        }
                        
                        String[] tc = teamColors.split(",");

                        tmps.getNotAllowedToCaptureTeams().addAll(Arrays.asList(tc));
                    }

                    if (arenaConf.contains(str + ".Dir")) {
                        tmps.setPointDirection(arenaConf.getString(str + ".Dir"));
                    }
                    arena.getCapturePoints().add(tmps);
                }
            }
            
            if (arenaConf.contains("Team-Spawns")) {
                for (String str : arenaConf.getConfigurationSection("Team-Spawns").getKeys(false)) {
                    Spawn spawn = new Spawn();
                    spawn.setName(str);
                    str = "Team-Spawns." + str;
                    spawn.setX(arenaConf.getDouble(str + ".X", 0.0D));
                    spawn.setY(arenaConf.getDouble(str + ".Y", 0.0D));
                    spawn.setZ(arenaConf.getDouble(str + ".Z", 0.0D));
                    spawn.setDir(arenaConf.getDouble(str + ".Dir", 0.0D));
                    arena.getTeamSpawns().put(spawn.getName(), spawn);

                    Team team = new Team();
                    team.setSpawn(spawn);
                    team.setColor(spawn.getName());
                    team.setMemberCount(0);
                    
                    try {
                        team.setChatColor(ChatColor.valueOf(spawn.getName().toUpperCase()));
                    } catch (Exception ex) {
                        team.setChatColor(ChatColor.GREEN);
                    }

                    // Check if this spawn is already in the list
                    boolean hasTeam = false;

                    for (Team aTeam : arena.getTeams()) {
                        if (aTeam.getColor().equalsIgnoreCase(spawn.getName())) {
                            hasTeam = true;
                            break;
                        }
                    }

                    if (!hasTeam) {
                        arena.getTeams().add(team);
                    }
                }
            }
            
            // Arena boundaries
            arena.setX1(arenaConf.getInt("Boundarys.X1", 0));
            arena.setY1(arenaConf.getInt("Boundarys.Y1", 0));
            arena.setZ1(arenaConf.getInt("Boundarys.Z1", 0));
            arena.setX2(arenaConf.getInt("Boundarys.X2", 0));
            arena.setY2(arenaConf.getInt("Boundarys.Y2", 0));
            arena.setZ2(arenaConf.getInt("Boundarys.Z2", 0));


            Lobby lobby = new Lobby(
                    arenaConf.getDouble("Lobby.X", 0.0D),
                    arenaConf.getDouble("Lobby.Y", 0.0D),
                    arenaConf.getDouble("Lobby.Z", 0.0D),
                    arenaConf.getDouble("Lobby.Dir", 0.0D));
            arena.setLobby(lobby);
            if ((lobby.getX() == 0.0D) && (lobby.getY() == 0.0D) && (lobby.getZ() == 0.0D) && (lobby.getDir() == 0.0D)) {
                arena.setLobby(null);
            }

            // Kj -- Test that the spawn points are within the map boundaries
            for (Spawn aSpawn : arena.getTeamSpawns().values()) {
                if (!ctp.getArenaUtil().isInside((int) aSpawn.getX(), arena.getX1(), arena.getX2()) || !ctp.getArenaUtil().isInside((int) aSpawn.getZ(), arena.getZ1(), arena.getZ2())) {
                	ctp.getLogger().warning("The spawn point \"" + aSpawn.getName() + "\" in the arena \"" + arena.getName() + "\" is out of the arena boundaries. ###");
                    continue;
                }
            }

            try {
                arenaConf.options().copyDefaults(true);
                arenaConf.save(arenaFile);
            } catch (IOException ex) {
                Logger.getLogger(CaptureThePoints.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            arena.setConfigOptions(ctp.getConfigTools().getArenaConfigOptions(arenaFile));

            return arena;
        } else {
        	ctp.getLogger().warning("Could not load arena! Check your config file and existing arenas");
            return null;
        }
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
                for (Arena arena : getArenas())
                    if (arena.getMaxPlayers() >= numberofplayers && arena.getMinPlayers() <= numberofplayers)
                        return true;
            }return false;
        } else {
            if (getSelectedArena().getMaxPlayers() >= numberofplayers && getSelectedArena().getMinPlayers() <= numberofplayers) {
                return true;
            } else {
                return false;
            }
        }
    }
    
    /**
     * Checks whether the given arena is fit to use, basically checking most things are set.
     * 
     * <p />
     * 
     * It checks the following:
     * <ul>
     * 	<li>The arena isn't null</li>
     * 	<li>There are arenas</li>
     * 	<li>The arena's name isn't null</li>
     * 	<li>The arena's world isn't null</li>
     * 	<li>The arena's lobby isn't null</li>
     * 	<li>The arena's boundaries are not zero</li>
     * 	<li>There are not zero team spawns</li>
     * 	<li>There is not just one team spawn</li>
     * 	<li>The team spawns are inside the boundaries of the arena</li>
     * 	<li>There are points to capture</li>
     * </ul>
     * 
     * @param arena The arena to check.
     * @param sender The one who sent the command.
     * @return An error message, empty if the arena is safe.
     * @author graywolf336
     * @since 1.5.0-b138
     */
    public String checkArena(Arena arena, CommandSender sender) {
    	if(arena == null)
    		return ChatColor.RED + "Couldn't find an arena by that name."; 
    	
    	if(getArenas().size() == 0)
    		return ChatColor.RED + "There are currently no arenas made.";
    	
    	if(arena.getName().isEmpty())
    		return ChatColor.RED + "Couldn't find the name of the arena, please try again.";
    	
    	if(arena.getWorld() == null) {
    		if (Permissions.canAccess(sender, true, new String[] { "ctp.*", "ctp.admin" })) {
                return ChatColor.RED + "The arena config is incorrect. The world \"" + arena.getWorldName() + "\" could not be found. Hint: your first world's name is \"" + ctp.getServer().getWorlds().get(0).getName() + "\".";
            } else {
                return ChatColor.RED + "Sorry, this arena has not been set up properly. Please tell an admin. [Incorrect World]";
            }
    	}
    	
    	if(arena.getLobby() == null)
    		return ChatColor.RED + "No lobby for the arena " + arena.getName();
    	
    	if(arena.getX1() == 0 || arena.getX2() == 0 || arena.getY1() == 0 || arena.getY2() == 0 || arena.getZ1() == 0 || arena.getZ2() == 0)
    		return ChatColor.RED + "The arena's boundaries are not properly set.";
    	
    	if(arena.getTeamSpawns().size() == 0)
    		return ChatColor.RED + "There are currently no team spawns defined.";
    	
    	if(arena.getTeamSpawns().size() == 1)
    		return ChatColor.RED + "There is only one team spawn, minimum of two are needed.";
    	
    	for(Spawn aSpawn : arena.getTeamSpawns().values()) {
            if (!ctp.getArenaUtil().isInside((int) aSpawn.getX(), arena.getX1(), arena.getX2()) || !ctp.getArenaUtil().isInside((int) aSpawn.getZ(), arena.getZ1(), arena.getZ2())) {
                if (Permissions.canAccess(sender, true, new String[] { "ctp.*", "ctp.admin" })) {
                    return ChatColor.RED + "The spawn point \"" + aSpawn.getName() + "\" in the arena \"" + arena.getName() + "\" is out of the arena boundaries. "
                            + "[Spawn is " + (int) aSpawn.getX() + ", " + (int) aSpawn.getZ() + ". Boundaries are " + arena.getX1() + "<==>" + arena.getX2() + ", " + arena.getZ1() + "<==>" + arena.getZ2() + "].";
                } else {
                    return ChatColor.RED + "Sorry, this arena has not been set up properly. Please tell an admin. [Incorrect Boundaries]";
                }
            }
        }
    	
    	if(arena.getCapturePoints().size() == 0)
    		return ChatColor.RED + "No points have been defined, therefore it is hard to play a game so I can't let you join.";
    	
    	return "";
    }
}
