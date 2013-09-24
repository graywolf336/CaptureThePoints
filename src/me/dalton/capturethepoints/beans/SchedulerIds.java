package me.dalton.capturethepoints.beans;

/** The schedulers used by CTP */
public class SchedulerIds {
  
  /** The timer used to check time players have been in the lobby (and then to see if they need kicking) */
  public int lobbyActivity; // Kjhf
  
  /** The timer used to check healing items' cooldowns. */
  public int healingItemsCooldowns;

  public int arenaRestore;
  public int arenaRestoreSec;
}
