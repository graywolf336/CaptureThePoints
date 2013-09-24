package me.dalton.capturethepoints.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.HealingItems;
import me.dalton.capturethepoints.beans.Arena;
import me.dalton.capturethepoints.beans.PlayerData;
import me.dalton.capturethepoints.beans.PlayersAndCooldowns;
import me.dalton.capturethepoints.beans.Spawn;
import me.dalton.capturethepoints.beans.Team;
import me.dalton.capturethepoints.events.CTPStartEvent;

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
    public Team getTeamFromColor(Arena arena, String color) {
    	for (Team t : arena.getTeams())
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
    public void healPlayerAndCallEvent(Player player, double amount) {
    	player.setHealth(amount);
    	EntityRegainHealthEvent regen = new EntityRegainHealthEvent(player, amount, RegainReason.CUSTOM);
    	ctp.getPluginManager().callEvent(regen);
    }
    
    /**
     * Heal the player (set the health) and cause an event to happen from it, thus improving relations with other plugins.
     * 
     * @param arena The arena which this has happened.
     * @param player The player to heal.
     */
    public void setFullHealthPlayerAndCallEvent(Arena arena, Player player) {
    	double gained = player.getMaxHealth() - player.getHealth();
    	
    	player.setHealth(player.getMaxHealth());
    	
    	EntityRegainHealthEvent regen = new EntityRegainHealthEvent(player, gained, RegainReason.CUSTOM);
    	ctp.getPluginManager().callEvent(regen);
    }
    
    /**
     * Adds a point to the given team's total points.
     * <p />
     * 
     * @param arena The arena in which this is happening
     * @param aTeam The team name which to add a point to
     * @param gainedpoint The point gained.
     * @return The message to send to the players.
     */
    public String addPoints(Arena arena, String aTeam, String gainedpoint) {
    	if(arena.getStatus().isRunning()) {
            for (Team team : arena.getTeams()) {
                if (team.getColor().equalsIgnoreCase(aTeam)) {
                    team.addOneControlledPoints();
                    if (!arena.getConfigOptions().useScoreGeneration)
                        return team.getChatColor() + aTeam.toUpperCase() + ChatColor.WHITE + " " + ctp.getLanguage().CAPTURED + " " + ChatColor.GOLD + gainedpoint + ChatColor.WHITE + ". (" + team.getControlledPoints() + "/" + arena.getConfigOptions().pointsToWin + " " + ctp.getLanguage().POINTS + ").";
                    else
                        return team.getChatColor() + aTeam.toUpperCase() + ChatColor.WHITE + " " + ctp.getLanguage().CAPTURED + " " + ChatColor.GOLD + gainedpoint + ChatColor.WHITE + ". (" + team.getControlledPoints() + "/" + arena.getCapturePoints().size() + " " + ctp.getLanguage().POINTS + ").";
                }
            }
            return null;
    	}
        return null;
    }

    /**
     * Subtracts a given point from the given team's total points.
     * <p />
     * 
     * @param arena The arena in which this is happening
     * @param aTeam The team name which to subtract a point from
     * @param lostpoint The point lost.
     * @return The message to send to the players.
     */
    public String subtractPoints(Arena arena, String aTeam, String lostpoint) { // Kj -- remade.
        if (arena.getStatus().isRunning()) {
            for (Team team : arena.getTeams()) {
                if (team.getColor().equalsIgnoreCase(aTeam)) {
                    team.substractOneControlledPoints();
                    if (team.getControlledPoints() < 0) {
                        team.setControlledPoints(0);
                    }
                    return team.getChatColor() + aTeam.toUpperCase() + ChatColor.WHITE + " " + ctp.getLanguage().LOST + " " + ChatColor.GOLD + lostpoint + ".";
                }
            }
            return null;
        }
        return null;
    }
    
    /** Checks if the first {@link Vector} is inside the other two. */
	public boolean isInsideAB(Vector point, Vector first, Vector second) {
		boolean x = isInside(point.getBlockX(), first.getBlockX(), second.getBlockX());
		boolean y = isInside(point.getBlockY(), first.getBlockY(), second.getBlockY());
		boolean z = isInside(point.getBlockZ(), first.getBlockZ(), second.getBlockZ());
		
		return x && y && z;
	}
    
    /**
     * Checks if two numbers are inside a point, or something.
     * <p />
     * 
     * @param loc The location.
     * @param first The first point
     * @param second The second point
     * @return True if they are inside, false if not.
     */
    private boolean isInside(int loc, int first, int second) {
        int point1 = 0;
        int point2 = 0;
        if (first < second) {
            point1 = first;
            point2 = second;
        } else {
            point2 = first;
            point1 = second;
        }

        return (point1 <= loc) && (loc <= point2);
    }
    
    /**
     * Checks if a team won in an arena.
     * <p />
     * @param arena The arena to check.
     * @return True if someone did win, false if not.
     */
    public boolean didSomeoneWin(Arena arena) {
        List<Team> winningteams = new ArrayList<Team>();
        String WinMessage = "";
        if (arena.getConfigOptions().useScoreGeneration) {
            for (Team team : arena.getTeams()) {
                if (team.getScore() >= arena.getConfigOptions().scoreToWin) {
                    winningteams.add(team);
                    WinMessage = team.getChatColor() + team.getColor().toUpperCase() + ChatColor.WHITE + " " + ctp.getLanguage().WINS;
                }
            }
        } else {
            for (Team team : arena.getTeams()) {
                if (team.getControlledPoints() >= arena.getConfigOptions().pointsToWin) {
                    winningteams.add(team);
                    WinMessage = team.getChatColor() + team.getColor().toUpperCase() + ChatColor.WHITE + " " + ctp.getLanguage().WINS;
                }
            }
        }

        if (winningteams.isEmpty()) {
            return false;
        } else if (winningteams.size() > 1) {
            if (arena.getConfigOptions().useScoreGeneration)
            	WinMessage = ctp.getLanguage().TIE_POINTS.replaceAll("%WT", winningteams.size() + "").replaceAll("%WP", arena.getConfigOptions().pointsToWin + "");
            else
            	WinMessage = ctp.getLanguage().TIE_SCORE.replaceAll("%WT", winningteams.size() + "").replaceAll("%WS", arena.getConfigOptions().scoreToWin + "");
        }

        for (Team team : winningteams) {
            for (String player : arena.getPlayersData().keySet()) {
                if (arena.getPlayerData(player).inArena() && (arena.getPlayerData(player).getTeam() == team)) {
                	arena.getPlayerData(player).setWinner(true);
                }
            }
        }

        ctp.getUtil().sendMessageToPlayers(arena, WinMessage);
        String message = "";
        if (arena.getConfigOptions().useScoreGeneration)
            for (Team aTeam : arena.getTeams())
                message = message + aTeam.getChatColor() + aTeam.getColor().toUpperCase() + ChatColor.WHITE + " " + ctp.getLanguage().FINAL_SCORE + ": " + aTeam.getScore() + ChatColor.AQUA + " // ";
        else
            for (Team aTeam : arena.getTeams())
                message = message + aTeam.getChatColor() + aTeam.getColor().toUpperCase() + ChatColor.WHITE + " " + ctp.getLanguage().FINAL_POINTS + ": " + aTeam.getControlledPoints() + ChatColor.AQUA + " // ";

        ctp.getUtil().sendMessageToPlayers(arena, message);
        
        arena.endGame(true, true);//End the game and give the rewards.
        return true;
    }
    
    public void moveToSpawns(Arena arena) {
    	CTPStartEvent event = new CTPStartEvent(arena, ctp.getLanguage().GAME_STARTED);
    	ctp.getPluginManager().callEvent(event);
    	
        for (String player : arena.getPlayersData().keySet()) {
            moveToSpawns(arena, player);
        }

        //Game settings
        for (Team team : arena.getTeams()) {
            team.setControlledPoints(0);
            team.setScore(0);
        }
        
        if ((!arena.getConfigOptions().useScoreGeneration) && (arena.getConfigOptions().pointsToWin > arena.getCapturePoints().size()))
            arena.getConfigOptions().pointsToWin = arena.getCapturePoints().size();

        // Balance teams for already selected teams
        balanceTeamsFromLobby(arena);

        ctp.getServer().broadcastMessage(ChatColor.AQUA + "[CTP] " + ChatColor.WHITE + event.getStartMessage() + " " + arena.getName() + "!");
        
        arena.updateStatusToRunning();
        
        didSomeoneWin(arena);

        final String aName = arena.getName();
    	
        //Start the timers/scheduler/tasks/whatever you wanna call it
        arena.getStartTimer().start();
        
        if(!arena.getConfigOptions().useScoreGeneration)
        	arena.getPlayTimer().schedule();

        //Money giving and score generation
        arena.getScoreGenTask().start();
        
        if(arena.getConfigOptions().useScoreGeneration)
        	arena.getScoreMessenger().start();

        // Healing items cooldowns
        arena.setHealingItemsCooldowns(ctp.getServer().getScheduler().scheduleSyncRepeatingTask(ctp, new Runnable() {
            public void run () {
                if (ctp.getArenaMaster().getArena(aName).getStatus().isRunning()) {
                    for (HealingItems item : ctp.getHealingItems()) {
                        if (item != null && item.cooldowns != null && item.cooldowns.size() > 0) {
                            for (String playName : item.cooldowns.keySet()) {
                                PlayersAndCooldowns data = item.cooldowns.get(playName);
                                Player player = ctp.getServer().getPlayer(playName);
                                if (data.getCooldown() == 1) {// This is cause we begin from top
                                	player.sendMessage(ChatColor.GREEN + item.item.getItem().toString().toLowerCase() + ChatColor.WHITE + " cooldown has refreshed!");
                                }

                                if (data.getHealingTimesLeft() > 0 && data.getIntervalTimeLeft() <= 0) {
                                    if (ctp.getServer().getPlayer(playName).getHealth() + item.hotHeal > ctp.getArenaMaster().getArena(aName).getConfigOptions().maxPlayerHealth) {
                                    	ctp.getArenaUtil().healPlayerAndCallEvent(player, ctp.getArenaMaster().getArena(aName).getConfigOptions().maxPlayerHealth);
                                    } else {
                                    	ctp.getArenaUtil().healPlayerAndCallEvent(player, player.getHealth() + item.hotHeal);
                                    }
                                    data.setIntervalTimeLeft(item.hotInterval);
                                    data.setHealingTimesLeft(data.getHealingTimesLeft() - 1);
                                }
                                
                                data.setIntervalTimeLeft(data.getIntervalTimeLeft() - 1);
                                data.setCooldown(data.getCooldown() - 1);

                                if (data.getCooldown() <= 0 && data.getHealingTimesLeft() <= 0) {
                                    item.cooldowns.remove(playName);
                                }
                            }
                        }
                    }
                }
            }

        }, 20L, 20L)); // Every second (one)
    }
    
	@SuppressWarnings("deprecation")
	public void moveToSpawns(Arena arena, String pl) {
        if(pl == null)
            return;

        if (arena.getWaitingToMove() != null && !arena.getWaitingToMove().isEmpty())
            if(arena.getWaitingToMove().contains(pl))
            	arena.getWaitingToMove().remove(pl);

        //Assign team
        int smallest = 99999;
        String color = null;
        Team team = null;
        int teamNR = -1;
        PlayerData playerdata = arena.getPlayerData(pl);

        if(playerdata.getTeam() == null) {
            for (int i = 0; i < arena.getTeams().size(); i++) {
                if (arena.getTeams().get(i).getMemberCount() < smallest) {
                    team = arena.getTeams().get(i);
                    smallest = team.getMemberCount();
                    color = team.getColor();
                    teamNR = i;
                }
            }

            try {
            	arena.getTeams().get(teamNR).setChatColor(ChatColor.valueOf(team.getColor().toUpperCase())); // Kj
            } catch (Exception ex) {
            	arena.getTeams().get(teamNR).setChatColor(ChatColor.GREEN);
            }

            arena.getTeams().get(teamNR).addOneMemeberCount();

        } else {   // For already selected team
            team = playerdata.getTeam();
            color = team.getColor();
        }

        Player p = ctp.getServer().getPlayer(pl);
        
        //Give wool
        DyeColor color1 = DyeColor.valueOf(color.toUpperCase());
        ItemStack helmet = new ItemStack(Material.WOOL, 1, color1.getData());
        p.getInventory().setHelmet(helmet);
        if (arena.getConfigOptions().givenWoolNumber != -1) {  // Kj -- if it equals -1, skip the giving of wool.
            ItemStack wool = new ItemStack(Material.WOOL, arena.getConfigOptions().givenWoolNumber, color1.getData());
            p.getInventory().addItem(wool);
        }
        
		//It's deprecated but it's currently the only way to get the desired effect.
		p.updateInventory();
		
        //Move to spawn  
		playerdata.setTeam(team);

        Spawn spawn =
        		arena.getTeamSpawns().get(playerdata.getTeam().getColor()) != null
                ? arena.getTeamSpawns().get(playerdata.getTeam().getColor())
                : team.getSpawn();

        Location loc = new Location(arena.getWorld(),
        		arena.getTeamSpawns().get(color).getX(),
        		arena.getTeamSpawns().get(color).getY() + 1D,
        		arena.getTeamSpawns().get(color).getZ()); // Kj -- Y+1
        loc.setYaw((float) arena.getTeamSpawns().get(color).getDir());
        loc.getWorld().loadChunk(loc.getBlockX(), loc.getBlockZ());
        
        boolean teleport = p.teleport(loc);
        if (!teleport)
            p.teleport(new Location(p.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ(), 0.0F, (float) spawn.getDir()));
            
        arena.getLobby().getPlayersInLobby().remove(pl);
        playerdata.setInLobby(false);
        playerdata.setInArena(true);
    }
	
	@SuppressWarnings("deprecation")
	private void balanceTeamsFromLobby(Arena arena) {
        int difference = 0;
        int optimalPlayerCountInTeam = arena.getPlayersPlaying().size() / arena.getTeams().size();
        int[] teamPlayersCount = new int[arena.getTeams().size()];
        List<String> playersForBalance = new ArrayList<String>();

        if(ctp.getGlobalConfigOptions().debugMessages) {
        	ctp.getLogger().info("Starting the auto balacing:");
        	ctp.getLogger().info("   Difference: " + difference);
        	ctp.getLogger().info("   optimalPlayerCountInTeam: " + optimalPlayerCountInTeam);
        	ctp.getLogger().info("   teamPlayersCount: " + teamPlayersCount.length);
        }
        
        boolean areEqual = true;
        for(int i = 0; i < arena.getTeams().size(); i++) {
            teamPlayersCount[i] = arena.getTeams().get(i).getTeamPlayers(arena).size();
            if(optimalPlayerCountInTeam != teamPlayersCount[i])
                areEqual = false;
        }

        if(ctp.getGlobalConfigOptions().debugMessages) {
        	ctp.getLogger().info("   areEqual: " + areEqual);
        	for(int i = 0; i < teamPlayersCount.length; i++)
        		ctp.getLogger().info("   teamPlayersCount[" + i + "]: " + teamPlayersCount[i]);
        }
        
        //Teams are equal, no need to balance
        if(areEqual)
            return;

        //Finding which teams are overcrowded.
        for(int i = 0; i < arena.getTeams().size(); i++) {
           
            List<String> TeamPlayers = arena.getTeams().get(i).getTeamPlayers(arena);
            // Randam ir sudedam i sarasa zaidejus, kuriu yra per daug
            for(int j = 0; j < teamPlayersCount[i] - optimalPlayerCountInTeam; j++)
                playersForBalance.add(TeamPlayers.get(j));
            
            if(teamPlayersCount[i] - optimalPlayerCountInTeam < 0)
                difference = difference + (optimalPlayerCountInTeam - teamPlayersCount[i]);
        }
        
        // If there are enough players to balance teams
        if(difference <= playersForBalance.size() && difference > 0) {
            for(int i = 0; i < arena.getTeams().size(); i++) {
                if(teamPlayersCount[i] - optimalPlayerCountInTeam < 0) {
                    List<Player> playersForRemove = new ArrayList<Player>();
                    for(int j = 0; j < optimalPlayerCountInTeam - teamPlayersCount[i]; j++) {
                    	
                        Player p = ctp.getServer().getPlayer(playersForBalance.get(j));
                        
                        arena.getPlayerData(p).getTeam().substractOneMemeberCount();
                        Team oldTeam = arena.getPlayerData(p).getTeam();
                        arena.getPlayerData(p).setTeam(null);     // For moveToSpawns team check

                        //Remove Helmet
                        p.getInventory().setHelmet(null);
                        p.getInventory().remove(Material.WOOL);
                        
                		//It's deprecated but it's currently the only way to get the desired effect.
                		p.updateInventory();
                        
                		ctp.getArenaUtil().moveToSpawns(arena, p.getName());
                        playersForRemove.add(p);
                        ctp.sendMessage(p, arena.getPlayerData(p).getTeam().getChatColor() + "You" + ChatColor.WHITE + " changed teams from "
                                + oldTeam.getChatColor() + oldTeam.getColor() + ChatColor.WHITE + " to "+ arena.getPlayerData(p).getTeam().getChatColor() + arena.getPlayerData(p).getTeam().getColor() + ChatColor.WHITE + "! [Team-balancing]");
                    }
                    
                    for(Player p : playersForRemove)
                        playersForBalance.remove(p);
                }
            }
        }else if(arena.getConfigOptions().exactTeamMemberCount) { //If there are not enough players to balance teams
            for(String p : playersForBalance) {
                // Moving to Lobby
            	arena.getPlayerData(p).getTeam().substractOneMemeberCount();
            	arena.getPlayerData(p).setTeam(null);
            	arena.getPlayerData(p).setInArena(false);
            	arena.getPlayerData(p).setInLobby(true);
                arena.getLobby().getPlayersInLobby().put(p, true);
                arena.getPlayerData(p).setReady(true);
                arena.getPlayerData(p).setJustJoined(true); // Flag for teleport
                arena.getPlayerData(p).setLobbyJoinTime(System.currentTimeMillis());
                arena.getPlayerData(p).isWarned(false);
                arena.getWaitingToMove().add(p);

                // Get lobby location and move player to it.
                Location loc = new Location(arena.getWorld(), arena.getLobby().getX(), arena.getLobby().getY() + 1, arena.getLobby().getZ());
                loc.setYaw((float) arena.getLobby().getDir());
                loc.getWorld().loadChunk(loc.getBlockX(), loc.getBlockZ());
                ctp.getServer().getPlayer(p).teleport(loc); // Teleport player to lobby
                
                ctp.getUtil().sendMessageToPlayers(arena, ChatColor.GREEN + p + ChatColor.WHITE + " was moved to lobby! [Team-balancing]");
            }
        }
    }
	
	/** Moves the player into the stands and */
	public void moveToStands(Arena arena, Player player) {
		if(arena.getStands() == null)
			return;
		
		PlayerData pd = arena.getPlayerData(player);
		
		if(pd.inArena())
			arena.getPlayerData(player).setInArena(false);
		if(pd.inLobby())
			arena.getPlayerData(player).setInLobby(false);
		if(pd.inStands())
			return;
		
		ctp.getInvManagement().clearInventory(player, true); //clear the inventory, thus they have no items
		
		Location loc = new Location(arena.getWorld(), arena.getStands().getX(), arena.getStands().getY(), arena.getStands().getZ());
		loc.setYaw((float) arena.getStands().getDir());
		loc.getWorld().loadChunk(loc.getBlockX(), loc.getBlockZ());
		player.teleport(loc);
		
		arena.getStands().getPlayersInTheStands().add(player.getName());
		
		ctp.sendMessage(player, ctp.getLanguage().STANDS_MESSAGE);
	}
}
