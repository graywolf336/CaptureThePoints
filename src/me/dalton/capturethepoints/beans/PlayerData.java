package me.dalton.capturethepoints.beans;

import java.util.ArrayList;
import java.util.List;

import me.dalton.capturethepoints.CaptureThePoints;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

/** Player Data of the people playing CTP. */
public class PlayerData {
    private Team team;
    private String role;
    private int money, health, kills, killsInARow, deaths, deathsInARow, moveChecker, pointsCaptured, foodLevel;
    private long lobbyJoinTime, classChangeTime = 0;
    private boolean ready = false, winner = false, inLobby = false, inArena = false, justJoined = true, isInCreativeMode = false, warnedAboutActivity = false; // Kjhf
    private List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();

    /** Sets the team this player is on. */
    public void setTeam(Team team) {
    	this.team = team;
    }
    
    /** Gets the team this player is on. */
    public Team getTeam() {
    	return this.team;
    }
    
    /** Sets the class/role this player has chosen. */
    public void setRole(String role) {
    	this.role = role;
    }
    
    /** Gets the class/role this player has chosen. */
    public String getRole() {
    	return this.role;
    }
    
    /** Sets the game money this player has. */
    public void setMoney(int amount) {
    	this.money = amount;
    }
    
    /** Gets the game money this player has. */
    public int getMoney() {
    	return this.money;
    }
    
    /** Sets the player's health. */
    public void setHealth(int health) {
    	this.health = health;
    }
    
    /** Gets the player's health. */
    public int getHealth() {
    	return this.health;
    }
    
    /** Sets the number of kills this player has. */
    public void setKills(int kills) {
    	this.kills = kills;
    }
    
    /** Gets the number of kills this player has. */
    public int getKills() {
    	return this.kills;
    }
    
    /** Adds one kill to the number of kills this player has. */
    public void addOneKill() {
    	this.kills++;
    }
    
    /** Sets the number of kills in a row this player has. */
    public void setKillsInARow(int kills) {
    	this.killsInARow = kills;
    }
    
    /** Gets the number of kills in a row this player has. */
    public int getKillsInARow() {
    	return this.killsInARow;
    }
    
    /** Adds one kill to the number of kills in a row this player has. */
    public void addOneKillInARow() {
    	this.killsInARow++;
    }
    
    /** Sets the number of deaths this player has. */
    public void setDeaths(int deaths) {
    	this.deaths = deaths;
    }
    
    /** Gets the number of deaths this player has. */
    public int getDeaths() {
    	return this.deaths;
    }
    
    /** Adds one death to the number of deaths this player has. */
    public void addOneDeath() {
    	this.deaths++;
    }
    
    /** Sets the number of deaths in a row this player has. */
    public void setDeathsInARow(int kills) {
    	this.deathsInARow = kills;
    }
    
    /** Gets the number of deaths in a row this player has. */
    public int getDeathsInARow() {
    	return this.deathsInARow;
    }
    
    /** Adds one death to the number of deaths in a row this player has. */
    public void addOneDeathInARow() {
    	this.deathsInARow++;
    }
    
    public void setMoveChecker(int amount) {
    	this.moveChecker = amount;
    }
    
    public int getMoveChecker() {
    	return this.moveChecker;
    }
    
    public void addOneMoveChecker() {
    	this.moveChecker++;
    }
    
    /** Sets the number of points this player has captured. */
    public void setPointsCaptured(int points) {
    	this.pointsCaptured = points;
    }
    
    /** Gets the number of points this player has captured. */
    public int getPointsCaptured() {
    	return this.pointsCaptured;
    }
    
    /** Adds one point captured to the number of points this player has captured. */
    public void addOnePointCaptured() {
    	this.pointsCaptured++;
    }
    
    /** Sets the player's food level before joining a game. */
    public void setFoodLevel(int level) {
    	this.foodLevel = level;
    }
    
    /** Gets the player's food level before joining a game. */
    public int getFoodLevel() {
    	return this.foodLevel;
    }
    
    /** Sets the time the player joined the lobby (in ms -- gotten from System.currentTimeMillis). */
    public void setLobbyJoinTime(long time) {
    	this.lobbyJoinTime = time;
    }
    
    /** Gets the time the player joined the lobby (in ms -- gotten from System.currentTimeMillis). */
    public long getLobbyJoinTime() {
    	return this.lobbyJoinTime;
    }
    
    /** Sets the time the player changed class (in ms -- gotten from System.currentTimeMillis). */
    public void setClassChangeTime(long time) {
    	this.classChangeTime = time;
    }
    
    /** Gets the time the player changed class (in ms -- gotten from System.currentTimeMillis). */
    public long getClassChangeTime() {
    	return this.classChangeTime;
    }
    
    /** Sets the ready state of this player. */
    public void setReady(boolean ready) {
    	this.ready = ready;
    }
    
    /** Gets the ready state of this player. */
    public boolean isReady() {
    	return this.ready;
    }
    
    /** Sets the win state of this player. */
    public void setWinner(boolean winner) {
    	this.winner = winner;
    }
    
    /** Gets the win state of this player. */
    public boolean isWinner() {
    	return this.winner;
    }
    
    /** Sets if this player is in the lobby. */
    public void setInLobby(boolean inlobby) {
    	this.inLobby = inlobby;
    }
    
    /** Gets if this player is in the lobby. */
    public boolean inLobby() {
    	return this.inLobby;
    }
    
    /** Sets if this player is in the arena. */
    public void setInArena(boolean inarena) {
    	this.inArena = inarena;
    }
    
    /** Gets if this player is in the arena. */
    public boolean inArena() {
    	return this.inArena;
    }
    
    /** Sets if this player has just joined the lobby. */
    public void setJustJoined(boolean justjoined) {
    	this.justJoined = justjoined;
    }
    
    /** Gets if this player has just joined the lobby. */
    public boolean getJustJoined() {
    	return this.justJoined;
    }
    
    /** Sets if this player was in creative mode before joining a game. */
    public void inCreative(boolean creative) {
    	this.isInCreativeMode = creative;
    }
    
    /** Gets if this player was in creative mode before joining a game. */
    public boolean wasInCreative() {
    	return this.isInCreativeMode;
    }
    
    /** Sets if this player has been warned to ready up or be kicked. */
    public void isWarned(boolean warned) {
    	this.warnedAboutActivity = warned;
    }
    
    /** Gets if this player has been warned to ready up or be kicked. */
    public boolean hasBeenWarned() {
    	return this.warnedAboutActivity;
    }
    
    public void setPotionEffects(List<PotionEffect> effects) {
    	this.potionEffects = effects;
    }
    
    public List<PotionEffect> getPotionEffects() {
    	return this.potionEffects;
    }
    
    /** Get the player associated with this PlayerData
     * @param ctp CaptureThePoints instance
     * @return The Player */
    public Player getPlayer(CaptureThePoints ctp) {
        if (ctp.playerData.containsValue(this)) {
            for (String p : ctp.playerData.keySet()) {
                if (ctp.playerData.get(p) == this) {
                	Player player = ctp.getServer().getPlayer(p);
                    return player;
                }
            }
        }
        return null;
    }
}
