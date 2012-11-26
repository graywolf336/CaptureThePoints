package me.dalton.capturethepoints.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.dalton.capturethepoints.CaptureThePoints;

import org.bukkit.ChatColor;

/** A CTP Team */
public class Team {
    private ChatColor chatcolor = ChatColor.GREEN; // Kjhf
    private String color;
    private int memberCount;
    private int score;
    private int controlledPoints;
    private Spawn spawn;
    
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
    
    /** Get a Team from its color 
     * @param ctp CaptureThePoints instance
     * @param color The team's color
     * @return The Team corresponding to this color. */
    public static Team getTeamFromColor(CaptureThePoints ctp, String color) {
		for (Team aTeam : ctp.mainArena.getTeams())
		    if (aTeam.getColor().equalsIgnoreCase(color))
		        return aTeam;
		
		return null;
    }
    
    /** Get all Players in this team as a list of Players
     * @param ctp CaptureThePoints instance
     * @return The Player list */
    public List<String> getTeamPlayers(CaptureThePoints ctp) {
        List<String> teamplayers = new ArrayList<String>();
        
        for (String p : ctp.playerData.keySet()) {
            if (ctp.playerData.get(p).getTeam() == null) {
                continue; // Player is not yet in game.
            }
            
            if (ctp.playerData.get(p).getTeam().getColor().equalsIgnoreCase(this.color)) {
                teamplayers.add(p);
            }
        }
        return teamplayers;
    }

    // Kjhf
    /** Get all Players in this team as a list of playername strings
     * @param ctp CaptureThePoints instance
     * @return The playername list */
    public List<String> getTeamPlayerNames(CaptureThePoints ctp) {
        if (!ctp.mainArena.getTeams().contains(this)) {
            return null;
        }
        List<String> teamplayers = new ArrayList<String>();

        for (String p : ctp.playerData.keySet()) {
            if (ctp.playerData.get(p).getTeam() == null || ctp.playerData.get(p).getTeam().color == null) {
                continue; // Player is not yet in game.
            }
            
            if (ctp.playerData.get(p).getTeam() == this && ctp.playerData.get(p).getTeam().color.equalsIgnoreCase(this.color)) {
                teamplayers.add(p);
            }
        }
        return teamplayers;
    }
    
    /** Get a Random Player in this Team
     * @param ctp CaptureThePoints instance
     * @return The Player */
    public String getRandomPlayer(CaptureThePoints ctp) {
        List<String> teamPlayers = getTeamPlayers(ctp);
        if(teamPlayers.size() == 0) return null;
        
        Random random = new Random();
        int nextInt = random.nextInt(teamPlayers.size());
        return teamPlayers.get(nextInt);
    }
    
    /** Check this Team for errors. Currently only checks memberCount against TeamPlayers size.
     * @return boolean Has error? */
    public boolean sanityCheck(CaptureThePoints ctp) {
        if (this.getTeamPlayers(ctp) == null) {
            return this.getMemberCount() != 0;
        } else {
            return this.getTeamPlayers(ctp).size() != this.memberCount;    
        }
    }
}
