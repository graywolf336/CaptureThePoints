package me.dalton.capturethepoints.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;

/** A CTP Team */
public class Team {
    private ChatColor chatcolor = ChatColor.GREEN; // Kjhf
    private String color, name;
    private int memberCount;
    private int score;
    private int controlledPoints;
    private Spawn spawn;
    
    /** Gets the name of this team, the color but first case is cap and the rest are lowercase. */
    public String getName() {
    	if(name.isEmpty()) name = Character.toUpperCase(color.charAt(0)) + color.substring(1).toLowerCase();
    	
    	return this.name;
    }
    
    /** Sets the associated ChatColor of this Team.
     * @see ChatColor
     */
    public void setChatColor(ChatColor color) {
    	this.chatcolor = color;
    }
    
    /** Gets the associated ChatColor of this Team; defaults to GREEN.
     * @see ChatColor
     */
    public ChatColor getChatColor() {
    	return this.chatcolor;
    }
    
    /** Sets this Teams color. */
    public void setColor(String color) {
    	this.color = color;
    	this.name = Character.toUpperCase(color.charAt(0)) + color.substring(1).toLowerCase();
    }
    
    /** Gets this Teams color. */
    public String getColor() {
    	return this.color;
    }
    
    /** Sets the number of players in this Team. */
    public void setMemberCount(int count) {
    	this.memberCount = count;
    }
    
    /** Gets the number of players in this Team. */
    public int getMemberCount() {
    	return this.memberCount;
    }
    
    /** Subtracts one member count from the total. */
    public void substractOneMemeberCount() {
    	this.memberCount--;
    }
    
    /** Adds one member to the count. */
    public void addOneMemeberCount() {
    	this.memberCount++;
    }
    
    /** Sets this Teams score. */
    public void setScore(int score) {
    	this.score = score;
    }
    
    /** Gets this Teams score. */
    public int getScore() {
    	return this.score;
    }
    
    /** Sets the number of control points this Team has. */
    public void setControlledPoints(int amount) {
    	this.controlledPoints = amount;
    }
    
    /** Gets the number of control points this Team has. */
    public int getControlledPoints() {
    	return this.controlledPoints;
    }
    
    /** Subtracts one point count from the total. */
    public void substractOneControlledPoints() {
    	this.controlledPoints--;
    }
    
    /** Adds one point to the count. */
    public void addOneControlledPoints() {
    	this.controlledPoints++;
    }
    
    /** Sets this Teams spawn point. */
    public void setSpawn(Spawn spawn) {
    	this.spawn = spawn;
    }
    
    /** Gets this Teams spawn point. */
    public Spawn getSpawn() {
    	return this.spawn;
    }
    
    /** Get all Players in this team as a list of Players
     * @param arena The arena to get the team players from.
     * @return The Player list */
    public List<String> getTeamPlayers(Arena arena) {
        List<String> teamplayers = new ArrayList<String>();
        
        for (String p : arena.getPlayersData().keySet()) {
            if (arena.getPlayerData(p).getTeam() == null)
                continue; // Player is not yet in game.
            
            if (arena.getPlayerData(p).getTeam().getColor().equalsIgnoreCase(this.color))
                teamplayers.add(p);
        }
        return teamplayers;
    }

    /** Get all Players in this team as a list of playername strings
     * <p />
     * 
     * @param arena The arena the to get the team players for.
     * @return The playername list
     */
    public List<String> getTeamPlayerNames(Arena arena) {
        if (!arena.getTeams().contains(this)) {
            return null;
        }
        List<String> teamplayers = new ArrayList<String>();

        for (String p : arena.getPlayersData().keySet()) {
            if (arena.getPlayerData(p).getTeam() == null || arena.getPlayerData(p).getTeam().color == null) {
                continue; // Player is not yet in game.
            }
            
            if (arena.getPlayerData(p).getTeam() == this && arena.getPlayerData(p).getTeam().color.equalsIgnoreCase(this.color)) {
                teamplayers.add(p);
            }
        }
        return teamplayers;
    }
    
    /** Get a Random Player in this Team
     * <p />
     * 
     * @param arena The arena this team belongs to
     * @return The Player's name
     */
    public String getRandomPlayer(Arena arena) {
        List<String> teamPlayers = getTeamPlayers(arena);
        if(teamPlayers.size() == 0) return null;
        
        Random random = new Random();
        int nextInt = random.nextInt(teamPlayers.size());
        return teamPlayers.get(nextInt);
    }
    
    /** Check this Team for errors. Currently only checks memberCount against TeamPlayers size.
     * @return boolean Has error? */
    public boolean sanityCheck(Arena arena) {
        if (getTeamPlayers(arena) == null) {
            return this.getMemberCount() != 0;
        } else {
            return this.getTeamPlayers(arena).size() != this.memberCount;    
        }
    }
}
