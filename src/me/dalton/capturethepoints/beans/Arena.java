package me.dalton.capturethepoints.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.util.Vector;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.ConfigOptions;
import me.dalton.capturethepoints.HealingItems;
import me.dalton.capturethepoints.beans.tasks.AutoStartTimer;
import me.dalton.capturethepoints.beans.tasks.ItemCoolDownsTask;
import me.dalton.capturethepoints.beans.tasks.PlayTimer;
import me.dalton.capturethepoints.beans.tasks.ScoreGenerationTask;
import me.dalton.capturethepoints.beans.tasks.ScoreMessengerTask;
import me.dalton.capturethepoints.enums.ArenaLeaveReason;
import me.dalton.capturethepoints.enums.Status;
import me.dalton.capturethepoints.events.CTPEndEvent;
import me.dalton.capturethepoints.events.CTPPlayerJoinEvent;
import me.dalton.capturethepoints.events.CTPPlayerLeaveEvent;
import me.dalton.capturethepoints.listeners.TagAPIListener;
import me.dalton.capturethepoints.util.PotionManagement;

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
    private int endCounterID = 0, endCount = 5;
    
    private HashMap<String, Spawn> teamSpawns;
    private List<Team> teams;
    private HashSet<Points> capturePoints;
    private List<String> waitingToMove;
    private Map<String, PlayerData> players;
    private HashMap<String, Location> previousLocation;
    private Lobby lobby;
    private Stands stands;
    
    //Scheduler, status, etc
    private Status status;
    private AutoStartTimer startTimer;
    private ItemCoolDownsTask itemCoolDowns;
    private PlayTimer playTime;
    private ScoreGenerationTask scoreGen;
    private ScoreMessengerTask scoreMsg;
    private boolean move = true;
    
    private int minimumPlayers = 2;
    private int maximumPlayers = 9999;
    
    /**
     * Initiates a new arena instance.
     * <p />
     * 
     * @param plugin The CTP plugin instance.
     * @param name The name of the arena.
     */
    public Arena(CaptureThePoints plugin, String name, Status status, int startSeconds, int playingTime) {
    	this.ctp = plugin;
    	this.name = name;
    	this.status = status;
    	this.teamSpawns = new HashMap<String, Spawn>();
    	this.teams = new ArrayList<Team>();
    	this.capturePoints = new HashSet<Points>();
    	this.waitingToMove = new LinkedList<String>();
    	this.players = new ConcurrentHashMap<String, PlayerData>();
    	this.previousLocation = new HashMap<String, Location>();
    	
    	this.startTimer = new AutoStartTimer(ctp, this, startSeconds);
    	this.itemCoolDowns = new ItemCoolDownsTask(ctp, this);
    	this.playTime = new PlayTimer(ctp, this, playingTime * 60 * 20); //Convert minutes to seconds, seconds to ticks
    	this.scoreGen = new ScoreGenerationTask(ctp, this);
    	this.scoreMsg = new ScoreMessengerTask(ctp, this);
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
    public HashSet<Points> getCapturePoints() {
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
    
    /** Returns the {@link ItemCoolDownsTask} for this arena. */
    public ItemCoolDownsTask getItemCoolDownTask() {
    	return this.itemCoolDowns;
    }
    
    /** Returns the {@link PlayTimer} for the timer countdown. */
    public PlayTimer getPlayTimer() {
    	return this.playTime;
    }
    
    /** Returns the {@link ScoreGenerationTask} for the score generation task. */
    public ScoreGenerationTask getScoreGenTask() {
    	return this.scoreGen;
    }
    
    /** Returns the {@link ScoreMessengerTask} for the score messaging to the players. */
    public ScoreMessengerTask getScoreMessenger() {
    	return this.scoreMsg;
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
	public int scheduleDelayedRepeatingTask(Runnable r, long delay, long period) {
		return Bukkit.getScheduler().runTaskTimer(ctp, r, delay, period).getTaskId();
	}
	
	/**
	 * Schedules a task which ones once at after the specified time, delay, has passed.
	 * 
	 * @param r The task to be scheduled
	 * @param delay The time to pass before we run this
	 * @return The id of the scheduled task, -1 if something went wrong.
	 */
	public int scheduleDelayedTask(Runnable r, long delay) {
		return Bukkit.getScheduler().scheduleSyncDelayedTask(ctp, r, delay);
	}
	
	public void startOtherTasks() {
        //Start all the other tasks and timers, since the game is starting.
        if(!getConfigOptions().useScoreGeneration)
        	getPlayTimer().schedule();
        
        getScoreGenTask().start();
        
        if(getConfigOptions().useScoreGeneration)
        	getScoreMessenger().start();
        
        getItemCoolDownTask().start();
	}
	
	/**
	 * Sends the player to the lobby.
	 * 
	 * @param p The player to send to the lobby.
	 * @return Whether it was successful or not.
	 */
	public boolean joinLobby(Player player) {
		//Don't add someone who is already in
		if(players.get(player.getName()) != null) return false;
		
		String mainArenaCheckError = ctp.getArenaMaster().checkArena(this, player); // Check arena, if there is an error, an error message is returned.
        if(!mainArenaCheckError.isEmpty()) {
            ctp.sendMessage(player, mainArenaCheckError);
            return false;
        }

        // Some more checks
        if(player.isInsideVehicle()) {
            try {
                player.leaveVehicle();
            } catch (Exception e) {
                player.kickPlayer(ctp.getLanguage().checks_PLAYER_IN_VEHICLE); // May sometimes reach this if player is riding an entity other than a Minecart
                return false;
            }
        }
        
        if(player.isSleeping()) {
            player.kickPlayer(ctp.getLanguage().checks_PLAYER_SLEEPING);
            return false;
        }

        if(players.isEmpty())
            lobby.getPlayersInLobby().clear();   //Reset if first to come

    	//Call a custom event for when players join the arena
        CTPPlayerJoinEvent event = new CTPPlayerJoinEvent(player, this, ctp.getLanguage().PLAYER_JOIN.replaceAll("%PN", player.getName()));
        ctp.getPluginManager().callEvent(event);
        player = event.getPlayer(); //In case some plugin sets data to this
        
        if(event.isCancelled())
        	return false; //Some plugin cancelled the event, so don't go forward and allow the plugin to handle the message that is sent when cancelled.
        
        if(ctp.getEconomy() != null && getConfigOptions().economyMoneyCostForJoiningArena != 0) {
            EconomyResponse r = ctp.getEconomy().bankWithdraw(player.getName(), getConfigOptions().economyMoneyCostForJoiningArena);
            if(r.transactionSuccess()) {
                ctp.sendMessage(player,
                		ctp.getLanguage().SUCCESSFUL_PAYING_FOR_JOINING
                			.replaceAll("%EA", String.valueOf(r.amount))
                			.replaceAll("%AN", name));
            } else {
                ctp.sendMessage(player, ctp.getLanguage().NOT_ENOUGH_MONEY_FOR_JOINING);
                event.setCancelled(true);
                return false;
            }
        }
        
        // Assign player's PlayerData
        PlayerData data = new PlayerData(player, getConfigOptions().moneyAtTheLobby);
        
        // Store and remove potion effects on player
        data.setPotionEffects(PotionManagement.storePlayerPotionEffects(player));
        PotionManagement.removeAllEffects(player);
        
        // Save player's previous state 
        if (player.getGameMode() == GameMode.CREATIVE) {
            data.inCreative(true);
            player.setGameMode(GameMode.SURVIVAL);
        }

        addPlayerData(player, data);
        getLobby().getPlayersInLobby().put(player.getName(), false); // Kj
        getLobby().getPlayersWhoWereInLobby().add(player.getName()); // Kj
        
        player.setFoodLevel(20);
        player.setMaxHealth(getConfigOptions().maxPlayerHealth);//Sets their health to the custom maximum.
        
        //Set the player's health and also trigger an event to happen because of it, add compability with other plugins
        player.setHealth(getConfigOptions().maxPlayerHealth);
        EntityRegainHealthEvent regen = new EntityRegainHealthEvent(player, (double)getConfigOptions().maxPlayerHealth, RegainReason.CUSTOM);
    	ctp.getPluginManager().callEvent(regen);
    	player = (Player) regen.getEntity(); //In case some plugin sets something different here
        
        // Get lobby location and move player to it.
        Location loc = new Location(getWorld(), getLobby().getX(), getLobby().getY(), getLobby().getZ());
        loc.setYaw((float) getLobby().getDir());
        if(!loc.getWorld().isChunkLoaded(loc.getChunk()))
        	loc.getWorld().loadChunk(loc.getChunk());
        
        getPrevoiusPosition().put(player.getName(), player.getLocation());
        ctp.getInvManagement().saveInv(player);

        ctp.getUtil().sendMessageToPlayers(this, event.getJoinMessage());

        // Get lobby location and move player to it.        
        player.teleport(loc); // Teleport player to lobby

        //clear the inventory again in case some other plugin restored some inventory to them after we teleported them (Multiverse inventories)
        ctp.getInvManagement().clearInventory(player, true);
        
        ctp.sendMessage(player, ctp.getLanguage().LOBBY_JOIN.replaceAll("%AN", name));
        getPlayerData(player).setInLobby(true);
		
		return true;
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
        if (playTime.getTaskId() != -1) {
            playTime.cancel();
        }
        
        if (scoreGen.getTaskId() != -1) {
        	scoreGen.cancel();
        }
        
        if (scoreMsg.getTaskId() != -1) {
            scoreMsg.cancel();
        }
        
        if (itemCoolDowns.getTaskId() != -1) {
        	itemCoolDowns.cancel();
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
