package me.dalton.capturethepoints.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.ConfigOptions;

/** Arena Data of the saved arenas for playing CTP. */
public class ArenaData {
    private String name = "";
    private String world;
    private HashMap<String, Spawn> teamSpawns = new HashMap<String, Spawn>();
    private List<Team> teams = new ArrayList<Team>();
    private List<Points> capturePoints = new LinkedList<Points>();
    private Lobby lobby;
    private ConfigOptions co;
    private int x1 = 0;
    private int y1 = 0;
    private int z1 = 0;
    private int x2 = 0;
    private int y2 = 0;
    private int z2 = 0;
    private int minimumPlayers = 2;
    private int maximumPlayers = 9999;
    
    /** Sets the name of this arena. */
    public void setName(String name) {
    	this.name = name;
    }
    
    /** Gets the name of this arena. */
    public String getName() {
    	return this.name;
    }
    
    /** Sets the name of the world this arena is in. */
    public void setWorld(String world) {
    	this.world = world;
    }
    
    /** Gets the name of the world this arena is in. */
    public String getWorld() {
    	return this.world;
    }
    
    /** Gets the teamspawns this arena has (Hashmap of Teamcolor, Spawn). 
     * @see Spawn */
    public HashMap<String, Spawn> getTeamSpawns() {
    	return this.teamSpawns;
    }
    
    /** Gets the Teams stored by CaptureThePoints.
     * @see Team */
    public List<Team> getTeams() {
    	return this.teams;
    }
    
    /** Gets the capture points this arena has. 
     * @see Points */
    public List<Points> getCapturePoints() {
    	return this.capturePoints;
    }
    
    /** Sets this arena's Lobby 
     * @see Lobby */
    public void setLobby(Lobby lobby) {
    	this.lobby = lobby;
    }
    
    /** Gets this arena's Lobby 
     * @see Lobby */
    public Lobby getLobby() {
    	return this.lobby;
    }
    
    /** Sets the arena's config options
     * @see ConfigOptions */
    public void setConfigOptions(ConfigOptions co) {
    	this.co = co;
    }
    
    /** Gets the arena's config options
     * @see ConfigOptions */
    public ConfigOptions getConfigOptions() {
    	return this.co;
    }
    
    /** Sets the first X coordinate representing the boundary of this arena. */
    public void setX1(int x1) {
    	this.x1 = x1;
    }
    
    /** Gets the first X coordinate representing the boundary of this arena. */
    public int getX1() {
    	return this.x1;
    }
    
    /** Sets the first Y coordinate representing the boundary of this arena. */
    public void setY1(int y1) {
    	this.y1 = y1;
    }
    
    /** Gets the first Y coordinate representing the boundary of this arena. */
    public int getY1() {
    	return this.y1;
    }
    
    /** Sets the first Z coordinate representing the boundary of this arena. */
    public void setZ1(int z1) {
    	this.z1 = z1;
    }
    
    /** Gets the first Z coordinate representing the boundary of this arena. */
    public int getZ1() {
    	return this.z1;
    }
    
    /** Sets the second X coordinate representing the boundary of this arena. */
    public void setX2(int x2) {
    	this.x2 = x2;
    }
    
    /** Gets the second X coordinate representing the boundary of this arena. */
    public int getX2() {
    	return this.x2;
    }
    
    /** Sets the second Y coordinate representing the boundary of this arena. */
    public void setY2(int y2) {
    	this.y2 = y2;
    }
    
    /** Gets the second Y coordinate representing the boundary of this arena. */
    public int getY2() {
    	return this.y2;
    }
    
    /** Sets the second Z coordinate representing the boundary of this arena. */
    public void setZ2(int z2) {
    	this.z2 = z2;
    }
    
    /** Gets the second Z coordinate representing the boundary of this arena. */
    public int getZ2() {
    	return this.z2;
    }
    
    /** Sets the minimum number of players this arena can take. [Default: 2] */
    public void setMinPlayers(int amount) {
    	this.minimumPlayers = amount;
    }
    
    /** Gets the minimum number of players this arena can take. [Default: 2] */
    public int getMinPlayers() {
    	return this.minimumPlayers;
    }
    
    /** Sets the maximum number of players this arena can take. [Default: 9999] */
    public void setMaxPlayers(int amount) {
    	this.maximumPlayers = amount;
    }
    
    /** Gets the maximum number of players this arena can take. [Default: 9999] */
    public int getMaxPlayers() {
    	return this.maximumPlayers;
    }
    
    /** Get all Players in this arena, including those in lobby, as a list of playername strings
     * @param ctp CaptureThePoints instance
     * @return The playername list */
    public List<String> getPlayers(CaptureThePoints ctp) {
        List<String> players = new ArrayList<String>();
        for (String p : ctp.playerData.keySet()) {
            players.add(p);
        }
        return players;
    }
    
    /** Get all Players that are playing in this arena as a list of playername strings
     * @param ctp CaptureThePoints instance
     * @return The playername list */
    public List<String> getPlayersPlaying(CaptureThePoints ctp) {
        List<String> players = new ArrayList<String>();
        for (String p : ctp.playerData.keySet()) {
            if(!ctp.playerData.get(p).inArena()) {
                continue; // Player is not yet in game.
            } else {
                players.add(p);
            }
        } return players;
    }
    
    /** Check to see if this Arena has a lobby.
     * @return true if Arena has a lobby, else false. */
    public boolean hasLobby() {
        return this.lobby != null;
    }
}
