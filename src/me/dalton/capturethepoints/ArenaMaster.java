package me.dalton.capturethepoints;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import me.dalton.capturethepoints.beans.Arena;
import me.dalton.capturethepoints.beans.ArenaBoundaries;
import me.dalton.capturethepoints.beans.Lobby;
import me.dalton.capturethepoints.beans.PlayerData;
import me.dalton.capturethepoints.beans.Points;
import me.dalton.capturethepoints.beans.Spawn;
import me.dalton.capturethepoints.beans.Team;
import me.dalton.capturethepoints.events.CTPPlayerJoinEvent;
import me.dalton.capturethepoints.util.PotionManagement;

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
	 * @since 1.5.0-b123
	 */
	public List<Arena> getArenas() {
		return this.arenas;
	}
	
	/**
	 * Clears out the arena list, no kicking players here.
	 * <p />
	 * 
	 * @since 1.5.0-b123
	 */
	public void resetArenas() {
		this.arenas.clear();
		this.arenasBoundaries.clear();
		this.selectedArena = null;
		this.editingArena = null;
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
    
    /** This method changes the selectedArena (main) to a suitable arena using the number of players you have.
     * Note, it will not change the mainArena if useSelectedArenaOnly is set to true.
     * 
     * @param numberofplayers The number of players that want to play.
     * @return The name of the selected mainArena, else empty String. */
    public String chooseSuitableArena(int numberofplayers) {
        // Is the config set to allow the random choosing of arenas?
        if (!getSelectedArena().getConfigOptions().useSelectedArenaOnly) {
            int size = getArenas().size();

            if (size > 1) {
                // If there is more than 1 arena to choose from
                List<String> arenas = new ArrayList<String>();
                for (Arena arena : getArenas()) {
                    if (arena.getMaxPlayers() >= numberofplayers && arena.getMinPlayers() <= numberofplayers) {
                        arenas.add(arena.getName());
                        setSelectedArena(arena);
                    }
                }
                
                if (arenas.size() > 1) {
                    Random random = new Random();
                    int nextInt = random.nextInt(size); // Generate a random number between 0 (inclusive) -> Number of arenas (exclusive)
                    setSelectedArena(arenas.get(nextInt));
                }
                
                ctp.getLogger().info("ChooseSuitableArena: Players found: " + numberofplayers + ", total arenas found: " + size + " " + getArenas() + ", of which " + arenas.size() + " were suitable: " + arenas);
            }else {
            	ctp.getLogger().info("The selected arena, " + getSelectedArena().getName()
            			+ ", has a minimum of " + getSelectedArena().getMinPlayers()
            			+ ", and a maximum of " + getSelectedArena().getMaxPlayers() + ".");
                return getSelectedArena().getName();
            }
        }
        return getSelectedArena().getName() == null ? "" : getSelectedArena().getName();
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
     *  <li>The arena is not in edit mode</li>
     *  <li>The arena is enabled</li>
     *  <li>The arena is full or not.</li>
     *  <li>The arena is running and we don't allow joining after it is started.</li>
     * </ul>
     * 
     * @param arena The arena to check.
     * @param sender The one who sent the command.
     * @return An error message, empty if the arena is safe.
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
    		if (ctp.getPermissions().canAccess(sender, true, new String[] { "ctp.*", "ctp.admin" })) {
                return ChatColor.RED + "The arena config is incorrect. The world \"" + arena.getWorldName() + "\" could not be found. Hint: your first world's name is \"" + ctp.getServer().getWorlds().get(0).getName() + "\".";
            } else {
                return ChatColor.RED + "Sorry, this arena has not been set up properly. Please tell an admin. [Incorrect World]";
            }
    	}
    	
    	if(arena.getLobby() == null)
    		return ChatColor.RED + "No lobby for the arena '" + arena.getName() + "'";
    	
    	if(arena.getX1() == 0 || arena.getX2() == 0 || arena.getY1() == 0 || arena.getY2() == 0 || arena.getZ1() == 0 || arena.getZ2() == 0)
    		return ChatColor.RED + "The arena's boundaries are not properly set for '" + arena.getName() + "'";
    	
    	if(arena.getTeamSpawns().size() == 0)
    		return ChatColor.RED + "There are currently no team spawns defined for '" + arena.getName() + "'";
    	
    	if(arena.getTeamSpawns().size() == 1)
    		return ChatColor.RED + "There is only one team spawn, minimum of two are needed for '" + arena.getName() + "'";
    	
    	for(Spawn aSpawn : arena.getTeamSpawns().values()) {
            if (!ctp.getArenaUtil().isInside((int) aSpawn.getX(), arena.getX1(), arena.getX2()) || !ctp.getArenaUtil().isInside((int) aSpawn.getZ(), arena.getZ1(), arena.getZ2())) {
                if (ctp.getPermissions().canAccess(sender, true, new String[] { "ctp.*", "ctp.admin" })) {
                    return ChatColor.RED + "The spawn point \"" + aSpawn.getName() + "\" in the arena \"" + arena.getName() + "\" is out of the arena boundaries. "
                            + "[Spawn is " + (int) aSpawn.getX() + ", " + (int) aSpawn.getZ() + ". Boundaries are " + arena.getX1() + "<==>" + arena.getX2() + ", " + arena.getZ1() + "<==>" + arena.getZ2() + "].";
                } else {
                    return ChatColor.RED + "Sorry, this arena has not been set up properly. Please tell an admin. [Incorrect Boundaries]";
                }
            }
        }
    	
    	if(arena.getCapturePoints().size() == 0)
    		return ChatColor.RED + "No points have been defined, therefore it is hard to play a game so I can't let you join.";
    	
    	if(arena.isEdit())
    		return ChatColor.RED + "Sorry, this arena is currently in edit mode.";
    	
    	if(!arena.isEnabled())
    		return ChatColor.RED + "Sorry, this arena is currently disabled.";
    	
    	if(arena.getPlayersPlaying().size() == arena.getMaxPlayers())
    		return ChatColor.RED + "Sorry, this arena is currently full of players. Try again in a little bit.";
    	
    	if(arena.isGameRunning() && !arena.getConfigOptions().allowLateJoin)
    		return ChatColor.RED + "A game has already started. You may not join.";
    	
    	return "";
    }
    
    public void moveToLobby(Arena arena, Player player) {
    	//Call a custom event for when players join the arena
        CTPPlayerJoinEvent event = new CTPPlayerJoinEvent(player, arena);
        ctp.getPluginManager().callEvent(event);
        
        if(event.isCancelled())
        	return; //Some plugin cancelled the event, so don't go forward and allow the plugin to handle the message that is sent when cancelled.
        
        String mainArenaCheckError = checkArena(arena, player); // Check arena, if there is an error, an error message is returned.
        if (!mainArenaCheckError.isEmpty()) {
            ctp.sendMessage(player, mainArenaCheckError);
            return;
        }

        // Some more checks
        if (player.isInsideVehicle()) {
            try {
                player.leaveVehicle();
            } catch (Exception e) {
                player.kickPlayer("Banned for ever... Nah, just don't join while riding something."); // May sometimes reach this if player is riding an entity other than a Minecart
                return;
            }
        }
        
        if (player.isSleeping()) {
            player.kickPlayer("Banned for life... Nah, just don't join from a bed ;)");
            return;
        }

        if (arena.getPlayersData().isEmpty())
            arena.getLobby().getPlayersInLobby().clear();   //Reset if first to come

        if(ctp.getEconomyHandler() != null && arena.getConfigOptions().economyMoneyCostForJoiningArena != 0) {
            EconomyResponse r = ctp.getEconomyHandler().bankWithdraw(player.getName(), arena.getConfigOptions().economyMoneyCostForJoiningArena);
            if(r.transactionSuccess()) {
                ctp.sendMessage(player, "You were charged " + ChatColor.GREEN + r.amount + ChatColor.WHITE + " for entering " + ChatColor.GREEN + arena.getName() + ChatColor.WHITE + " arena.");
            } else {
                ctp.sendMessage(player, "You dont have enough money to join arena!");
                return;
            }
        }
        
        // Assign player's PlayerData
        PlayerData data = new PlayerData();
        data.setDeaths(0);
        data.setDeathsInARow(0);
        data.setKills(0);
        data.setKillsInARow(0);
        data.setMoney(arena.getConfigOptions().moneyAtTheLobby);
        data.setPointsCaptured(0);
        data.setReady(false);
        data.setInArena(false);
        data.setFoodLevel(player.getFoodLevel());
        data.setHealth(player.getHealth());
        data.setLobbyJoinTime(System.currentTimeMillis());
        
        // Store and remove potion effects on player
        data.setPotionEffects(PotionManagement.storePlayerPotionEffects(player));
        PotionManagement.removeAllEffects(player);

        // Save player's previous state 
        player.setFoodLevel(20);
        if (player.getGameMode() == GameMode.CREATIVE) {
            data.inCreative(true);
            player.setGameMode(GameMode.SURVIVAL);
        }

        arena.addPlayerData(player, data);
        
        arena.getLobby().getPlayersInLobby().put(player.getName(), false); // Kj
        arena.getLobby().getPlayersWhoWereInLobby().add(player.getName()); // Kj

        //Set the player's health and also trigger an event to happen because of it, add compability with other plugins
        player.setHealth(arena.getConfigOptions().maxPlayerHealth);
        EntityRegainHealthEvent regen = new EntityRegainHealthEvent(player, arena.getConfigOptions().maxPlayerHealth, RegainReason.CUSTOM);
    	ctp.getPluginManager().callEvent(regen);
        
        // Get lobby location and move player to it.
        Location loc = new Location(arena.getWorld(), arena.getLobby().getX(), arena.getLobby().getY() + 1, arena.getLobby().getZ());
        loc.setYaw((float) arena.getLobby().getDir());
        if(!loc.getWorld().isChunkLoaded(loc.getChunk()))
        	loc.getWorld().loadChunk(loc.getChunk());

        Double X = Double.valueOf(player.getLocation().getX());
        Double y = Double.valueOf(player.getLocation().getY());
        Double z = Double.valueOf(player.getLocation().getZ());

        Location previous = new Location(player.getWorld(), X.doubleValue(), y.doubleValue(), z.doubleValue());
        arena.getPrevoiusPosition().put(player.getName(), previous);
        ctp.getInvManagement().saveInv(player);

        ctp.getUtil().sendMessageToPlayers(arena, ChatColor.GREEN + player.getName() + ChatColor.WHITE + " joined a CTP game.");

        // Get lobby location and move player to it.        
        player.teleport(loc); // Teleport player to lobby
        ctp.sendMessage(player, ChatColor.GREEN + "Joined CTP lobby " + ChatColor.GOLD + arena.getName() + ChatColor.GREEN + ".");
        arena.getPlayerData(player).setInLobby(true);
    }
}
