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
import me.dalton.capturethepoints.beans.Arena;
import me.dalton.capturethepoints.beans.PlayerData;
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
    
    /** Returns the ChatColor from the given color, returns green if nothing close enough is found. */
    public ChatColor getChatColorFromColor(String color) {
    	if(color.equalsIgnoreCase("cyan")) {
    		return ChatColor.DARK_AQUA;
    	}else if(color.equalsIgnoreCase("black")) {
    		return ChatColor.BLACK;
    	}else if(color.equalsIgnoreCase("light_blue")) {
    		return ChatColor.BLUE;
    	}else if(color.equalsIgnoreCase("blue")) {
    		return ChatColor.DARK_BLUE;
    	}else if(color.equalsIgnoreCase("gray")) {
    		return ChatColor.DARK_GRAY;
    	}else if(color.equalsIgnoreCase("green")) {
    		return ChatColor.DARK_GREEN;
    	}else if(color.equalsIgnoreCase("purple")) {
    		return ChatColor.DARK_PURPLE;
    	}else if(color.equalsIgnoreCase("red")) {
    		return ChatColor.DARK_RED;
    	}else if(color.equalsIgnoreCase("orange")) {
    		return ChatColor.GOLD;
    	}else if(color.equalsIgnoreCase("lime")) {
    		return ChatColor.GREEN;
    	}else if(color.equalsIgnoreCase("magenta")) {
    		return ChatColor.LIGHT_PURPLE;
    	}else if(color.equalsIgnoreCase("pink")) {
    		return ChatColor.RED;
    	}else if(color.equalsIgnoreCase("white")) {
    		return ChatColor.WHITE;
    	}else if(color.equalsIgnoreCase("yellow")) {
    		return ChatColor.YELLOW;
    	}else {
    		return ChatColor.GREEN;
    	}
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
                        return team.getChatColor() + team.getName() + ChatColor.WHITE + " " + ctp.getLanguage().CAPTURED + " " + ChatColor.GOLD + gainedpoint + ChatColor.WHITE + ". (" + team.getControlledPoints() + "/" + arena.getConfigOptions().pointsToWin + " " + ctp.getLanguage().POINTS + ").";
                    else
                        return team.getChatColor() + team.getName() + ChatColor.WHITE + " " + ctp.getLanguage().CAPTURED + " " + ChatColor.GOLD + gainedpoint + ChatColor.WHITE + ". (" + team.getControlledPoints() + "/" + arena.getCapturePoints().size() + " " + ctp.getLanguage().POINTS + ").";
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
                    
                    return team.getChatColor() + team.getName() + ChatColor.WHITE + " " + ctp.getLanguage().LOST + " " + ChatColor.GOLD + lostpoint + ".";
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
                    WinMessage = team.getChatColor() + team.getName() + ChatColor.WHITE + " " + ctp.getLanguage().WINS;
                }
            }
        } else {
            for (Team team : arena.getTeams()) {
                if (team.getControlledPoints() >= arena.getConfigOptions().pointsToWin) {
                    winningteams.add(team);
                    WinMessage = team.getChatColor() + team.getName() + ChatColor.WHITE + " " + ctp.getLanguage().WINS;
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
            for (PlayerData player : arena.getPlayersData().values()) {
                if (player.inArena() && (player.getTeam() == team)) {
                	player.setWinner(true);
                }
            }
        }

        ctp.getUtil().sendMessageToPlayers(arena, WinMessage);
        String message = "";
        if (arena.getConfigOptions().useScoreGeneration)
            for (Team aTeam : arena.getTeams())
                message = message + aTeam.getChatColor() + aTeam.getName() + ChatColor.WHITE + " " + ctp.getLanguage().FINAL_SCORE + ": " + aTeam.getScore() + ChatColor.AQUA + " // ";
        else
            for (Team aTeam : arena.getTeams())
                message = message + aTeam.getChatColor() + aTeam.getName() + ChatColor.WHITE + " " + ctp.getLanguage().FINAL_POINTS + ": " + aTeam.getControlledPoints() + ChatColor.AQUA + " // ";

        ctp.getUtil().sendMessageToPlayers(arena, message);
        
        arena.endGame(true, true);//End the game and give the rewards.
        return true;
    }
    
    public void moveToSpawns(Arena arena) {
    	CTPStartEvent event = new CTPStartEvent(arena, ctp.getLanguage().GAME_STARTED);
    	ctp.getPluginManager().callEvent(event);
    	arena = event.getArena();
    	
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
        arena.balanceTeams(0);

        ctp.getServer().broadcastMessage(ChatColor.AQUA + "[CTP] " + ChatColor.WHITE + event.getStartMessage() + " " + arena.getName() + "!");
        
        didSomeoneWin(arena);
    	
        //Start the timers/scheduler/tasks/whatever you wanna call it
        if(arena.getConfigOptions().useStartCountDown) {
        	arena.getStartTimer().start();
        	arena.setMoveAbility(false);
        	arena.updateStatusToRunning(true);
        }else {
        	arena.setMoveAbility(true);
        	arena.startOtherTasks();
        	arena.updateStatusToRunning(false);
        }
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

            arena.getTeams().get(teamNR).addOneMember();

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
        		arena.getTeamSpawns().get(color).getY(),
        		arena.getTeamSpawns().get(color).getZ());
        loc.setYaw((float) arena.getTeamSpawns().get(color).getDir());
        loc.getWorld().loadChunk(loc.getBlockX(), loc.getBlockZ());
        
        boolean teleport = p.teleport(loc);
        if (!teleport)
            p.teleport(new Location(p.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ(), 0.0F, (float) spawn.getDir()));
            
        arena.getLobby().getPlayersInLobby().remove(pl);
        playerdata.setInLobby(false);
        playerdata.setInArena(true);
    }
	
	/** Moves the player into the stands and */
	public void moveToStands(Arena arena, Player player) {
		if(arena.getStands() == null)
			return;
		
		PlayerData data = arena.getPlayerData(player);
		
		if(data.inArena())
			data.setInArena(false);
		if(data.inLobby())
			data.setInLobby(false);
		if(data.inStands())
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
