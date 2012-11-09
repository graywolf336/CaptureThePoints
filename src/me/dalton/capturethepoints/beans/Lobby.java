package me.dalton.capturethepoints.beans;

// Kjhf's
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/** A Lobby in a CTP arena */
public class Lobby {
    private HashMap<String, Boolean> playersinlobby = new HashMap<String, Boolean>();
    private List<String> playerswhowereinlobby = new ArrayList<String>();
    private double x = 0D;
    private double y = 0D;
    private double z = 0D;
    private double dir = 0D;

    /** Creates a Lobby in a CTP arena and sets all the coordinate points. */
    public Lobby(double x, double y, double z, double dir) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dir = dir;
    }
    
    /** Sets the X coordinate of this lobby's spawn */
    public void setX(double x) {
    	this.x = x;
    }
    
    /** Gets the X coordinate of this lobby's spawn */
    public double getX() {
    	return this.x;
    }
    
    /** Sets the Y coordinate of this lobby's spawn */
    public void setY(double y) {
    	this.y = y;
    }
    
    /** Gets the Y coordinate of this lobby's spawn */
    public double getY() {
    	return this.y;
    }
    
    /** Sets the Z coordinate of this lobby's spawn */
    public void setZ(double z) {
    	this.z = z;
    }
    
    /** Gets the Z coordinate of this lobby's spawn */
    public double getZ() {
    	return this.z;
    }
    
    /** Sets the direction in which players spawn in this lobby. */
    public void setDir(double dir) {
    	this.dir = dir;
    }
    
    /** Gets the direction in which players spawn in this lobby. */
    public double getDir() {
    	return this.dir;
    }
    
    /** Sets the list of Players who have been in this ctp lobby. They may still be in the Lobby. */
    public void setPlayersWhoWereInLobby(List<String> players) {
    	this.playerswhowereinlobby = players;
    }
    
    /** Gets the list of Players who have been in this ctp lobby. They may still be in the Lobby. */
    public List<String> getPlayersWhoWereInLobby() {
    	return this.playerswhowereinlobby;
    }
    
    /** Sets the list of Players and their ready status */
    public void setPlayersInLobby(HashMap<String, Boolean> players) {
    	this.playersinlobby = players;
    }
    
    /** Gets the list of Players and their ready status */
    public HashMap<String, Boolean> getPlayersInLobby() {
    	return this.playersinlobby;
    }
        
    /** Returns boolean stating whether any players in the lobby hashmap have "false" as their ready status boolean. */
    public boolean hasUnreadyPeople() {
        return playersinlobby.values().contains(false);
    }
    
    /** Return the number of players with a false ready status. */
    public int countUnreadyPeople() {
        if (playersinlobby.values().contains(false)) {
            int counter = 0;
            for (Boolean aBool : playersinlobby.values()) {
                if (aBool == false) {
                    counter++;
                    continue;
                } else {
                    continue;
                }
            }
            return counter;
        } else {
            return 0;
        }
    }
    /** Return the number of players with a true ready status. */
    public int countReadyPeople() {
        if (playersinlobby.values().contains(true)) {
            int counter = 0;
            for (Boolean aBool : playersinlobby.values()) {
                if (aBool == true) {
                    counter++;
                    continue;
                } else {
                    continue;
                }
            }
            return counter;
        } else {
            return 0;
        }
    }
    
    /** Return a list of players with a false ready status. */
    public List<String> getUnreadyPeople() {
        if (playersinlobby.values().contains(false)) {
            List<String> players = new ArrayList<String>();
            for (String player : playersinlobby.keySet()) {
                if (playersinlobby.get(player) == false) {
                    players.add(player);
                } else {
                    continue;
                }
            }
            return players;
        } else {
            return null;
        }
    }
    
    /** Return number of players in lobby hashmap. */
    public int countAllPeople() {
        return playersinlobby.size();        
    }
    
    /** Clears the data for the players and their statuses saved by this lobby */
    public void clearLobbyPlayerData() {
        this.playersinlobby.clear();
        this.playerswhowereinlobby.clear();
    }
    

    /** Get the last person to join this Lobby who is still online.
     * @param canBeInLobby if true, may return someone who is still in the lobby. If false, ignores those in the lobby.
     * @return The player or null if none found. */
    public Player getLastJoiner(boolean canBeInLobby) {
        List<String> players = this.playerswhowereinlobby;
        for (int i = 1; i < players.size() ; i++) {
            if (players.get(players.size()-i) != null) {
                String testplayer = players.get(players.size()-i);
                if (canBeInLobby) {
                    return Bukkit.getPlayer(testplayer); // Don't bother checking the lobby
                } else {
                    if (playersinlobby.get(testplayer) != null) {
                        continue; // Player is in the lobby.
                    } else {
                        return Bukkit.getPlayer(testplayer); // Player not in the lobby.
                    }
                }
            }
        }
        return null;
    }
}
