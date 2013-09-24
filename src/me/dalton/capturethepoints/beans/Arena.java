package me.dalton.capturethepoints.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.ConfigOptions;
import me.dalton.capturethepoints.HealingItems;
import me.dalton.capturethepoints.enums.ArenaLeaveReason;
import me.dalton.capturethepoints.enums.Status;
import me.dalton.capturethepoints.events.CTPEndEvent;
import me.dalton.capturethepoints.events.CTPPlayerLeaveEvent;
import me.dalton.capturethepoints.listeners.TagAPIListener;

/** Arena Data of the saved arenas for playing {@link CaptureThePoints}.
 * 
 * @author graywolf336
 */
public class Arena {
	//general
	private CaptureThePoints ctp;
    private String name = "";
    private String world;
    private Vector corner1, corner2;
    
    //config
    private ConfigOptions co;
    
    //SchedulerIds
    private int playTimer = 0, money_Score = 0, pointMessenger = 0, healingItemsCooldowns = 0, endCounterID = 0, endCount = 5;
    
    private HashMap<String, Spawn> teamSpawns;
    private List<Team> teams;
    private List<Points> capturePoints;
    private List<String> waitingToMove;
    private Map<String, PlayerData> players;
    private HashMap<String, Location> previousLocation;
    private Lobby lobby;
    private Stands stands;
    
    //Scheduler, status, etc
    private Status status;
    private AutoStartTimer startTimer;
    private boolean move = true;
    
    private int minimumPlayers = 2;
    private int maximumPlayers = 9999;
    
    /**
     * Initiates a new arena instance without a name.
     * <p />
     * 
     * @param plugin The CTP plugin instance.
     * @since 1.5.0-b126
     */
    public Arena(CaptureThePoints plugin, int startSeconds) {
    	this.ctp = plugin;
    	this.teamSpawns = new HashMap<String, Spawn>();
    	this.teams = new ArrayList<Team>();
    	this.capturePoints = new LinkedList<Points>();
    	this.waitingToMove = new LinkedList<String>();
    	this.players = new ConcurrentHashMap<String, PlayerData>();
    	this.previousLocation = new HashMap<String, Location>();
    	
    	this.startTimer = new AutoStartTimer(ctp, this, startSeconds);
    }
    
    /**
     * Initiates a new arena instance.
     * <p />
     * 
     * @param plugin The CTP plugin instance.
     * @param name The name of the arena.
     */
    public Arena(CaptureThePoints plugin, String name, int startSeconds) {
    	this.ctp = plugin;
    	this.name = name;
    	this.teamSpawns = new HashMap<String, Spawn>();
    	this.teams = new ArrayList<Team>();
    	this.capturePoints = new LinkedList<Points>();
    	this.waitingToMove = new LinkedList<String>();
    	this.players = new ConcurrentHashMap<String, PlayerData>();
    	this.previousLocation = new HashMap<String, Location>();
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

    /** Gets the world object this arena is in. */
    public World getWorld() {
    	return world == null ? null : ctp.getServer().getWorld(this.world);
    }
    
    /** Gets the name of the world this arena is in. */
    public String getWorldName() {
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
    
    /** Sets the arena's stands.
     * 
     * @param stands The stands instance.
     * @see Stands
     */
    public void setStands(Stands stands) {
    	this.stands = stands;
    }
    
    /** Gets the arena's stands.
     * @return The stands teleport location for this arena.
     * @see Stands
     */
    public Stands getStands() {
    	return this.stands;
    }
    
    /**
     * Gets the status of the {@link Arena}.
     * 
     * @return The {@link Status status} of the arena.
     */
    public Status getStatus() {
    	return this.status;
    }
    
    /** Sets the status of the {@link Arena arena}.
     * 
     * @param status Status to set the arena to.
     */
    public void setStatus(Status status) {
    	this.status = status;
    }
    
    /** Updates the status of the arena to what is suitable per players. */
    public void updateStatusToRunning() {
    	if(players.size() == maximumPlayers)
    		status = Status.FULL_GAME;
    	else
    		status = Status.IN_GAME;
    }
    
    /** Sets whether the players can move or not. */
    public void setMoveAbility(boolean move) {
    	this.move = move;
    }
    
    /** Returns whether the players can move or not. */
    public boolean canMove() {
    	return this.move;
    }
    
    /** Returns the {@link AutoStartTimer start timer} for this arena. */
    public AutoStartTimer getStartTimer() {
    	return this.startTimer;
    }
    
    /** Sets the first corner to the given block coords. */
    public void setFirstCorner(int x, int y, int z) {
    	if(x == 0 && y == 0 && z == 0) return;
    	
    	this.corner1 = new Vector(x, y, z);
    }
    
    /** Returns the first corner of this arena in {@link Vector} form. */
    public Vector getFirstCorner() {
    	return this.corner1;
    }
    
    /** Sets the second corner to the given block coords. */
    public void setSecondCorner(int x, int y, int z) {
    	if(x == 0 && y == 0 && z == 0) return;
    	
    	this.corner2 = new Vector(x, y, z);
    }
    
    /** Returns the second corner of this arena in {@link Vector} form. */
    public Vector getSecondCorner() {
    	return this.corner2;
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
    
    /** Gets the List<String> of the players waiting to be moved in this arena. */
	public List<String> getWaitingToMove(){
		return this.waitingToMove;
	}
    
    /** Sets the scheduler id of the play timer task for this arena. */
    public void setPlayTimer(int playtimer) {
    	this.playTimer = playtimer;
    }
    
    /** Gets the scheduler id of the play timer task for this arena. */
    public int getPlayTimer() {
    	return this.playTimer;
    }
    
    /** Sets the scheduler id of the moneyscore task for this arena. */
    public void setMoneyScore(int moneyscore) {
    	this.money_Score = moneyscore;
    }
    
    /** Gets the scheduler id of the moneyscore task for this arena. */
    public int getMoneyScore() {
    	return this.money_Score;
    }
    
    /** Sets the scheduler id of the pointMessenger task for this arena. */
    public void setPointMessenger(int pointmessager) {
    	this.pointMessenger = pointmessager;
    }
    
    /** Gets the scheduler id of the pointMessenger task for this arena. */
    public int getPointMessenger() {
    	return this.pointMessenger;
    }
    
    /** Sets the scheduler id of the endCounter task for this arena. */
    public void setEndCounterID(int endCounterID) {
    	this.endCounterID = endCounterID;
    }
    
    /** Gets the scheduler id of the endCounter task for this arena. */
    public int getEndCounterID() {
    	return this.endCounterID;
    }
    
    /** Sets the number in which to start the counter off for counting down to the teleporting at the end of a game. */
    public void setEndCount(int count) {
    	this.endCount = count;
    }
    
    /** Gets the number in which to start the counter off at when ending the game and teleporting out. */
    public int getEndCount() {
    	return this.endCount;
    }
    
    /** Sets the scheduler id of the healingItemsCooldowns task for this arena. */
    public void setHealingItemsCooldowns(int healingitems) {
    	this.healingItemsCooldowns = healingitems;
    }
    
    /** Gets the scheduler id of the healingItemsCooldowns task for this arena. */
    public int getHealingItemsCooldowns() {
    	return this.healingItemsCooldowns;
    }
    
    /** Returns a list of all the players in the arena, including the lobby, as a List of Strings of their name.
     * <p />
     * 
     * @return The player name list
     * @since 1.5.0-b123
     */
    public List<String> getPlayers() {
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
     * @since 1.5.0-b123
     */
    public PlayerData getPlayerData(String player) {
    	return this.players.get(player);
    }
    
    /**
     * Returns the given player's arena data.
     * <p />
     * 
     * @param player The player who's data to get.
     * @return The player data, null if nothing.
     * @since 1.5.0-b126
     */
    public PlayerData getPlayerData(Player player) {
    	return this.getPlayerData(player.getName());
    }
    
    /**
     * Adds a player and his/her data to the list.
     * 
     * @param player The player who is being added.
     * @param playerdata The data about this player.
     * @since 1.5.0-148
     */
    public void addPlayerData(String player, PlayerData playerdata) {
    	this.players.put(player, playerdata);
    }
    
    /**
     * Adds a player and his/her data to the list.
     * 
     * @param player The player who is being added.
     * @param playerdata The data about this player.
     * @since 1.5.0-165
     */
    public void addPlayerData(Player player, PlayerData playerdata) {
    	this.players.put(player.getName(), playerdata);
    }
    
    /**
     * Returns a Map of all the players in the arena and their corresponding data.
     * <p />
     * 
     * @return Every player in this arena's data.
     * @since 1.5.0-b123
     */
    public Map<String, PlayerData> getPlayersData() {
    	return this.players;
    }
    
    /** Get all Players that are playing in this arena as a list of playername strings
     * <p />
     * 
     * @return The player name list
     */
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
    
    /**
     * Player's previous Locations before they started playing CTP.
     * 
     * @return A HashMap of the players pervious locations.
     * @since 1.5.0-b155
     */
    public HashMap<String, Location> getPrevoiusPosition() {
    	return this.previousLocation;
    }
    
    /** Check to see if this Arena has a lobby.
     * <p />
     * 
     * @return true if Arena has a lobby, else false.
     */
    public boolean hasLobby() {
        return this.lobby != null;
    }
    
    /**
     * Schedules a repeating task to run after the given delayed time and repeats after the given time, both in ticks.
     * 
     * @param r The count down task to schedule
     * @param delay The ticks to wait before running the task
     * @param period The ticks to wait between runs
     * @return The id of the task.
     */
	public int scheduleDelayedTask(Runnable r, long delay, long period) {
		return Bukkit.getScheduler().runTaskTimer(ctp, r, delay, period).getTaskId();
	}
    
    public void leaveGame(Player p, ArenaLeaveReason reason) {
        //On exit we get double signal
        if (players.get(p.getName()) == null)
            return;
        
        if (getWaitingToMove() != null && !getWaitingToMove().isEmpty()) {
            if (p.getName() == getWaitingToMove().get(0) && getWaitingToMove().size() == 1)
            	getWaitingToMove().clear(); // The player who left was someone in the lobby waiting to join. We need to remove them from the queue
            else
            	getWaitingToMove().remove(p.getName());
        }
        
        ctp.getInvManagement().removeCoolDowns(p.getName());
        
        ctp.getUtil().sendMessageToPlayers(this, p, ctp.getLanguage().PLAYER_LEFT.replaceAll("%PN", p.getName())); // Won't send to "player".
        
        //Remove the number count from the teamdata
        if (players.get(p.getName()).getTeam() != null) {
        	for(Team t : getTeams())
        		if(t == players.get(p.getName()).getTeam()) {
        			t.substractOneMemeberCount();
        			break;
        		}
        }

        CTPPlayerLeaveEvent event = new CTPPlayerLeaveEvent(p, this, players.get(p.getName()), reason);
        ctp.getPluginManager().callEvent(event);
        
        getLobby().getPlayersInLobby().remove(p.getName());
        ctp.getInvManagement().restoreThings(p);
        getPrevoiusPosition().remove(p.getName());
        players.remove(p.getName());

        // Check for player replacement if there is someone waiting to join the game
        boolean wasReplaced = false;
        if (getConfigOptions().exactTeamMemberCount && status.isRunning()) {
            for (String playerName : players.keySet()) {
                if (players.get(playerName).inLobby() && players.get(playerName).isReady()) {
                    ctp.getArenaUtil().moveToSpawns(this, playerName);
                    wasReplaced = true;
                    break;
                }
            }
        }

        //check for player count, only then were no replacement
        if (!wasReplaced)
            ctp.checkForGameEndThenPlayerLeft(this);
            
        //If there was no replacement we should move one member to lobby
        if (!wasReplaced && getConfigOptions().exactTeamMemberCount && status.isRunning())
            if (getConfigOptions().balanceTeamsWhenPlayerLeaves > 0)
                ctp.balanceTeams(this, 0, getConfigOptions().balanceTeamsWhenPlayerLeaves);
    }
    
    /**
     * Ends the current game that is happening in the arena, whether to give rewards or not.
     * 
     * @param rewards True to give rewards, false to not give rewards.
     * @param countdown True to countdown to the end, false to just straight up end it.
     */
    public void endGame(final boolean rewards, boolean countdown) {
    	final String aName = getName();
        
    	if(countdown)
	    	setEndCounterID(ctp.getServer().getScheduler().scheduleSyncRepeatingTask(ctp, new Runnable() {
	    		public void run() {
	    			Arena temp = ctp.getArenaMaster().getArena(aName);
	    			if(temp.getEndCount() == 0) {
	    				ctp.getServer().getScheduler().cancelTask(temp.getEndCounterID());
	    				ctp.getArenaMaster().getArena(aName).setEndCounterID(0);
	    				endGameNoCountDown(rewards);
	    				return;
	    			}
	    			
	    			if(temp.getConfigOptions().endCountDownTime == temp.getEndCount())
	    				ctp.getUtil().sendMessageToPlayers(temp, ctp.getLanguage().END_COUNTDOWN.replaceAll("%CS", String.valueOf(temp.getEndCount())));
	    			else
	    				ctp.getUtil().sendMessageToPlayers(temp, temp.getEndCount() + "..");
	    			
	    			ctp.getArenaMaster().getArena(aName).setEndCount(temp.getEndCount() - 1);//Set the counter to one minus what it current this.
	    		}
	    	}, 0L, 20L));
    	else
    		endGameNoCountDown(rewards);
    }
    
    private void endGameNoCountDown(boolean rewards) {
    	CTPEndEvent event = new CTPEndEvent(this, ctp.getLanguage().GAME_ENDED);
    	ctp.getPluginManager().callEvent(event);
    	
        ctp.getUtil().sendMessageToPlayers(this, event.getEndMessage());

        // Task canceling
        if (playTimer != 0) {
            ctp.getServer().getScheduler().cancelTask(playTimer);
            playTimer = 0;
        }
        if (money_Score != 0) {
            ctp.getServer().getScheduler().cancelTask(money_Score);
            money_Score = 0;
        }
        if (pointMessenger != 0) {
            ctp.getServer().getScheduler().cancelTask(pointMessenger);
            pointMessenger = 0;
        }
        if (healingItemsCooldowns != 0) {
            ctp.getServer().getScheduler().cancelTask(healingItemsCooldowns);
            healingItemsCooldowns = 0;
        }
        
        if(startTimer.getTaskId() != -1) {
        	startTimer.stop();
        }
        
        if(endCounterID != 0) {
        	ctp.getServer().getScheduler().cancelTask(endCounterID);
        }

        for (Points s : getCapturePoints())
            s.setControlledByTeam(null);
        
        status = Status.JOINABLE;

        for (String player : getPlayersData().keySet()) {
        	Player p = ctp.getServer().getPlayer(player);
        	ctp.getInvManagement().restoreThings(p);
            if (rewards)
                ctp.getUtil().rewardPlayer(this, p);
            if(ctp.useTag())//if we're using tag, refresh it on ending
            	TagAPIListener.refreshTag(p);
        }
        
        //Arena restore
        if(ctp.getGlobalConfigOptions().enableHardArenaRestore)
            ctp.getArenaRestore().restoreMySQLBlocks(this);
        else
            ctp.getArenaRestore().restoreAllBlocks();

        for (HealingItems item : ctp.getHealingItems())
            if (!item.cooldowns.isEmpty())
                item.cooldowns.clear();
        
        getLobby().clearLobbyPlayerData();
        if(getStands() != null) getStands().clearStandsPlayers();
        getPrevoiusPosition().clear();
        getPlayersData().clear();
        getPlayers().clear();
        
        //Reset the count downs
        endCount = getConfigOptions().endCountDownTime;
        
        for (Team t : getTeams()) {
            t.setMemberCount(0);
            t.setControlledPoints(0);
            t.setScore(0);
    	}
    }
}
