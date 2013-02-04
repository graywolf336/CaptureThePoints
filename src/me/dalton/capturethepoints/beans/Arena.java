package me.dalton.capturethepoints.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.ConfigOptions;
import me.dalton.capturethepoints.Util;
import me.dalton.capturethepoints.enums.ArenaLeaveReason;
import me.dalton.capturethepoints.events.CTPPlayerLeaveEvent;
import me.dalton.capturethepoints.util.InvManagement;

/** Arena Data of the saved arenas for playing CTP. */
public class Arena {
	//general
	private CaptureThePoints ctp;
    private String name = "";
    private String world;
    
    //config
    private ConfigOptions co;
    
    @SuppressWarnings("unused")
	private boolean enabled = true, edit = false, running = false;
    
    private HashMap<String, Spawn> teamSpawns;
    private List<Team> teams;
    private List<Points> capturePoints;
    @SuppressWarnings("unused")
    private List<String> notReadyPlayers;
    private Map<String, PlayerData> players;
    private Lobby lobby;
    
    private int x1 = 0, y1 = 0, z1 = 0, x2 = 0, y2 = 0, z2 = 0;
    private int minimumPlayers = 2;
    private int maximumPlayers = 9999;
    
    public Arena(CaptureThePoints plugin, String name) {
    	this.ctp = plugin;
    	this.teamSpawns = new HashMap<String, Spawn>();
    	this.teams = new ArrayList<Team>();
    	this.capturePoints = new LinkedList<Points>();
    	this.players = new ConcurrentHashMap<String, PlayerData>();
    	this.notReadyPlayers = new ArrayList<String>();
    	
    	setName(name);
    }
    
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
    
    /**
     * Sets if the game is running or not.
     * 
     * @param running True if running, false if not.
     * @author graywolf336
     * @since 1.5.0-b123
     */
    public void setRunning(boolean running) {
    	this.running = running;
    }
    
    /**
     * Returns if the game is running or not.
     * <p />
     * 
     * @return True if the game is running, false if not.
     * @author graywolf336
     * @since 1.5.0-b123
     */
    public boolean isGameRunning() {
    	return this.running;
    }
    
    /** Returns a list of all the players in the arena, including the lobby, as a List of Strings of their name.
     * <p />
     * 
     * @return The player name list
     * @author graywolf336
     * @since 1.5.0-b123
     */
    public List<String> getPlayerList() {
        List<String> toReturn = new ArrayList<String>();
        
        for (String p : players.keySet())
            toReturn.add(p);
            
        return toReturn;
    }
    
    /**
     * Returns the given player's arena data.
     * <p />
     * 
     * @param player The player who's data to get.
     * @return The player data, null if nothing.
     * @author graywolf336
     * @since 1.5.0-b123
     */
    public PlayerData getPlayerData(String player) {
    	return this.players.get(player);
    }
    
    /**
     * Returns a Map of all the players in the arena and their corresponding data.
     * <p />
     * 
     * @return Every player in this arena's data.
     * @author graywolf336
     * @since 1.5.0-b123
     */
    public Map<String, PlayerData> getPlayersData() {
    	return this.players;
    }
    
    /** Get all Players that are playing in this arena as a list of playername strings
     * <p />
     * 
     * @param ctp CaptureThePoints instance
     * @return The player name list */
    public List<String> getPlayersPlaying() {
        List<String> toReturn = new ArrayList<String>();
        
        for (String p : players.keySet()) {
        	if(!players.get(p).inLobby())
        		toReturn.add(p);
        	else
        		continue;
        }
        
        return toReturn;
    }
    
    /** Check to see if this Arena has a lobby.
     * <p />
     * 
     * @return true if Arena has a lobby, else false.
     */
    public boolean hasLobby() {
        return this.lobby != null;
    }
    
    public void leaveGame(Player p, ArenaLeaveReason reason) {
        //On exit we get double signal
        if (players.get(p.getName()) == null) {
            return;
        }
        
        if (ctp.waitingToMove != null && !ctp.waitingToMove.isEmpty()) {
            if (p.getName() == ctp.waitingToMove.get(0) && ctp.waitingToMove.size() == 1) {
                ctp.clearWaitingQueue(); // The player who left was someone in the lobby waiting to join. We need to remove them from the queue
            } else {
                ctp.waitingToMove.remove(p.getName());
            }
        }
        
        InvManagement.removeCoolDowns(p.getName());
        
        Util.sendMessageToPlayers(ctp, p, ChatColor.GREEN + p.getName() + ChatColor.WHITE + " left the CTP game!"); // Won't send to "player".
        
        //Remove the number count from the teamdata
        if (players.get(p.getName()).getTeam() != null) {
            for (int i = 0; i < getTeams().size(); i++) {
                if (getTeams().get(i) == (players.get(p.getName()).getTeam())) {
                    getTeams().get(i).substractOneMemeberCount();
                    break;
                }
            }
        }

        CTPPlayerLeaveEvent event = new CTPPlayerLeaveEvent(p, this, players.get(p.getName()), reason);
        ctp.getPluginManager().callEvent(event);
        
        getLobby().getPlayersInLobby().remove(p.getName());
        InvManagement.restoreThings(p);
        ctp.previousLocation.remove(p.getName());
        players.remove(p.getName());

        // Check for player replacement if there is someone waiting to join the game
        boolean wasReplaced = false;
        if (getConfigOptions().exactTeamMemberCount && isGameRunning()) {
            for (String playerName : players.keySet()) {
                if (players.get(playerName).inLobby() && players.get(playerName).isReady()) {
                    //this.playerListener.moveToSpawns(playerName);//TODO
                    wasReplaced = true;
                    break;
                }
            }
        }

        //check for player count, only then were no replacement
        if (!wasReplaced) {
            ctp.checkForGameEndThenPlayerLeft();
        }
            
        //If there was no replacement we should move one member to lobby
        if (!wasReplaced && getConfigOptions().exactTeamMemberCount && isGameRunning()) {
            if (getConfigOptions().balanceTeamsWhenPlayerLeaves > 0) {
                ctp.balanceTeams(0, getConfigOptions().balanceTeamsWhenPlayerLeaves); //TODO
            }
        }
    }
    
    /**
     * Forces the current arena game to stop.
     * 
     * @return True if successful, false if not.
     * @author graywolf336
     * @since 1.5.0-b123
     */
    public boolean forceEnd() {
    	if(!isGameRunning()) return false; //it's not running, thus we didn't shut it down.
    	
    	for(String p : players.keySet()) {
    		ctp.leaveGame(ctp.getServer().getPlayer(p), ArenaLeaveReason.FORECE_STOP); //TODO: Clean up here and make it smoother
    		players.remove(p);
    	}
    	
    	return true;
    }
}
