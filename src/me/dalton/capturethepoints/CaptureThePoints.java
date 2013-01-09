package me.dalton.capturethepoints;

import me.dalton.capturethepoints.listeners.CaptureThePointsPlayerListener;
import me.dalton.capturethepoints.listeners.CaptureThePointsBlockListener;
import me.dalton.capturethepoints.listeners.CaptureThePointsEntityListener;
import me.dalton.capturethepoints.util.ConfigTools;
import me.dalton.capturethepoints.util.PotionManagement;
import me.dalton.capturethepoints.util.InvManagement;
import me.dalton.capturethepoints.util.Permissions;
import me.dalton.capturethepoints.beans.ArenaBoundaries;
import me.dalton.capturethepoints.beans.Arena;
import me.dalton.capturethepoints.beans.Items;
import me.dalton.capturethepoints.beans.Lobby;
import me.dalton.capturethepoints.beans.PlayerData;
import me.dalton.capturethepoints.beans.Points;
import me.dalton.capturethepoints.beans.Rewards;
import me.dalton.capturethepoints.beans.SchedulerIds;
import me.dalton.capturethepoints.beans.Spawn;
import me.dalton.capturethepoints.beans.Team;
import me.dalton.capturethepoints.commands.*;
import me.dalton.capturethepoints.enums.ArenaLeaveReason;
import me.dalton.capturethepoints.events.CTPPlayerJoinEvent;
import me.dalton.capturethepoints.events.CTPPlayerLeaveEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.io.IOException;
import java.util.logging.Level;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class CaptureThePoints extends JavaPlugin {
	public static Permission permission = null;
    public static Economy economyHandler = null;
    public static boolean UsePermissions;

    /** "plugins/CaptureThePoints" */
    private String mainDir;

    /** "plugins/CaptureThePoints/CaptureSettings.yml" */
    //public static final File myFile = new File(mainDir + File.separator + "CaptureSettings.yml");
    private File globalConfigFile = null;

    private PluginManager pluginManager = null;

    /** List of commands accepted by CTP */
    private List<CTPCommand> commands = new ArrayList<CTPCommand>(); // Kj

    public final CaptureThePointsBlockListener blockListener = new CaptureThePointsBlockListener(this);

    public final CaptureThePointsEntityListener entityListener = new CaptureThePointsEntityListener(this);

    public final CaptureThePointsPlayerListener playerListener = new CaptureThePointsPlayerListener(this);

    public ArenaRestore arenaRestore = new ArenaRestore(this);
    public MysqlConnector mysqlConnector = new MysqlConnector(this);

    private final HashMap<String, ItemStack[]> Inventories = new HashMap<String, ItemStack[]>();

    private HashMap<String, ItemStack[]> armor = new HashMap<String, ItemStack[]>();

    /** The PlayerData stored by CTP. (HashMap: PlayerName, and their data) */
    public Map<String, PlayerData> playerData = new ConcurrentHashMap<String, PlayerData>();  // To avoid concurrent modification exceptions    

    /** Player's previous Locations before they started playing CTP. */
    public final HashMap<String, Location> previousLocation = new HashMap<String, Location>();

    /** The global config options for CTP. */
    private ConfigOptions globalConfigOptions = new ConfigOptions();
    
    /** Hashmap of all the arenas, identifier is their name. */
    private HashMap<String, Arena> arenas = new HashMap<String, Arena>();

    /** The list of arena names stored by CTP.
     * @deprecated
     */
    public List<String> arena_list = new LinkedList<String>();

    /** The selected arena for playing.
     * @deprecated
     */
    public Arena mainArena = new Arena();

    /** All arenas boundaries (HashMap: Arena's name, and its boundaries)**/
    public HashMap<String, ArenaBoundaries> arenasBoundaries = new HashMap<String, ArenaBoundaries>();

    /** The arena currently being edited. */
    public Arena editingArena = new Arena();

    /** The roles/classes stored by CTP. (HashMap: Role's name, and the Items it contains) 
     * @see Items */
    public HashMap<String, List<Items>> roles = new HashMap<String, List<Items>>();

    /** The list of Healing Items stored by CTP. */
    public List<HealingItems> healingItems = new LinkedList<HealingItems>();

    /** The list of Rewards stored by CTP. */
    private Rewards rewards = new Rewards();

    /** The timers used by CTP. */
    public SchedulerIds CTP_Scheduler = new SchedulerIds();

    public int arenaRestoreTimesRestored = 0;
    public int arenaRestoreMaxRestoreTimes = 0;
    public int arenaRestoreTimesRestoredSec = 0;   //For second time
    public int arenaRestoreMaxRestoreTimesSec = 0;
    
    /** If we're loading the plugin for the first time, defaults to false. */
    private boolean firstTime = false;

    /** Name of the player who needs teleporting. */
    public String playerNameForTeleport = ""; // Block destroy - teleport protection

    /** Load from CaptureSettings.yml */
    public FileConfiguration load () { //Yaml Configuration
        return load(globalConfigFile);
    }

    /** Load yml from specified file */
    public FileConfiguration load (File file) {
        try {
            FileConfiguration PluginPropConfig = YamlConfiguration.loadConfiguration(file);
            return PluginPropConfig;
        } catch (Exception localException) {} return null;
    }

    @Override
    public void onEnable () {
    	pluginManager = getServer().getPluginManager();
    	if(!pluginManager.isPluginEnabled("Vault")) {
    		getLogger().severe("Vault is required in order to use this plugin.");
    		getLogger().severe("dev.bukkit.org/server-mods/vault/");
			pluginManager.disablePlugin(this);
			return;
		}
    	
    	mainDir = this.getDataFolder().toString();
    	if(mainDir.isEmpty()) firstTime = true;
    	globalConfigFile = new File(mainDir + File.separator + "CaptureSettings.yml");
    	
        enableCTP(false);
    }

    /**
     * Loads everything from the configs and registers the events (if not reloading).
     * 
     * @param reloading Are we reloading the plugin?
     */
    public void enableCTP (boolean reloading) {
        if (!reloading) {
            setupPermissions();
            setupEconomy();

            // REGISTER EVENTS-----------------------------------------------------------------------------------
            pluginManager.registerEvents(blockListener, this);
            pluginManager.registerEvents(entityListener, this);
            pluginManager.registerEvents(playerListener, this);

            populateCommands();
        }
        
        ConfigTools.setCTP(this);
        Util.setCTP(this);
        InvManagement.setCTP(this);
        
        loadConfigFiles();

        // Checks for mysql
        if(this.globalConfigOptions.enableHardArenaRestore)
            mysqlConnector.checkMysqlData();

        //Kj: LobbyActivity timer.
        CTP_Scheduler.lobbyActivity = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run () {
                if (playerData == null) {
                    return;
                }
                
                if (playerData.isEmpty()) {
                    return;
                }
                
                if (globalConfigOptions.lobbyKickTime <= 0) {
                    return;
                }

                for (String player : playerData.keySet()) {
                    PlayerData data = playerData.get(player);
                    Player p = getServer().getPlayer(player);
                    if (data.inLobby() && !data.isReady()) {
                        // Kj -- Time inactivity warning.
                        if (((System.currentTimeMillis() - data.getLobbyJoinTime()) >= ((globalConfigOptions.lobbyKickTime * 1000) / 2)) && !data.hasBeenWarned()) {
                            sendMessage(p, ChatColor.LIGHT_PURPLE + "Please choose your class and ready up, else you will be kicked from the lobby!");
                            data.isWarned(true);
                        }

                        // Kj -- Time inactive in the lobby is greater than the lobbyKickTime specified in config (in ms)
                        if ((System.currentTimeMillis() - data.getLobbyJoinTime() >= (globalConfigOptions.lobbyKickTime * 1000)) && data.hasBeenWarned()) {
                            data.setInLobby(false);
                            data.setInArena(false);
                            data.isWarned(false);
                            leaveGame(p, ArenaLeaveReason.SERVER_STOP);
                            sendMessage(p, ChatColor.LIGHT_PURPLE + "You have been kicked from the lobby for not being ready on time.");
                        }
                    }
                }
            }

        }, 200L, 200L); // 10 sec
        
        logInfo("Loaded " + arenas.size() + " arena" + ((arenas.size() > 1) ? "s!" : "!"));
    }

    @Override
    public void onDisable () {
        if (CTP_Scheduler.lobbyActivity != 0) {
            getServer().getScheduler().cancelTask(CTP_Scheduler.lobbyActivity);
            CTP_Scheduler.lobbyActivity = 0;
        }
        
        clearConfig();
        pluginManager = null;
        permission = null;
        commands.clear();
    }

    @Override
    public boolean onCommand (CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("ctp")) {
            return true;
        }

        if (commands == null || commands.isEmpty()) { // Really weird bug that rarely occurs. Could call it a sanity check.
            populateCommands();
        }

        List<String> parameters = new ArrayList<String>();
        parameters.add(command.getName());
        parameters.addAll(Arrays.asList(args));

        if (parameters.size() == 1) {
            sendHelp(sender);
            return true;
        }

        for (CTPCommand each : commands) {
            for (String aString : each.aliases) {
                if (aString.equalsIgnoreCase(parameters.get(1))) { // Search the command aliases for the first argument given. If found, execute command.
                    each.execute(sender, parameters);
                    return true;
                }
            }
        }
        
        // Comand not found

        getLogger().info(sender.getName() + " issued an unknown CTP command. It has " + parameters.size() + " Parameters: " + parameters + ". Displaying help to them.");
        sendHelp(sender);
        return true;
    }

    /** Send the CTP help to this sender */
    public void sendHelp (CommandSender sender) {
        HelpCommand helpCommand = new HelpCommand(this);
        helpCommand.execute(sender, Arrays.asList("ctp"));
    }

    /** Attempt to balance the teams.
     * @param loop Times this has recursed (prevents overruns).
     * @return Whether the teams are balanced. */
    public boolean balanceTeams(int loop, int balanceThreshold) {
        if (loop > 5) {
        	getLogger().warning("balanceTeams hit over 5 recursions. Aborting.");
            return false;
        }
        //int balancethreshold = mainArena.co.balanceTeamsWhenPlayerLeaves; // Get the balance threshold from config. We know this is over 0 already.
        
        Team lowestTeam = null; // Team with the lower number of players
        int lowestmembercount = -1;
        Team highestTeam = null; // Team with the higher number of players
        int highestmembercount = -1;
        
        int difference = 0;

        for (Team aTeam : mainArena.getTeams()) {
            if (lowestmembercount == -1) {
                lowestmembercount = aTeam.getMemberCount();
                lowestTeam = aTeam;
                highestmembercount = aTeam.getMemberCount();
                highestTeam = aTeam;
                continue;
            } else {
                if (aTeam.getMemberCount() != lowestmembercount || aTeam.getMemberCount() != highestmembercount) {
                    if (aTeam.getMemberCount() < lowestmembercount) {
                        lowestmembercount = aTeam.getMemberCount(); // Reassign new low
                        lowestTeam = aTeam;
                    } else if (aTeam.getMemberCount() > highestmembercount) {
                        highestmembercount = aTeam.getMemberCount(); // Reassign new high
                        highestTeam = aTeam;
                    } else {
                        continue; // Logic error
                    }
                } else {
                    continue; // These teams are balanced.
                }
            }
        }

        difference = highestmembercount - lowestmembercount;
        if ((highestTeam == lowestTeam) || difference < balanceThreshold) {
            // The difference between the teams is not great enough to balance the teams as defined by balancethreshold.
            return true;
        }

        if (difference % mainArena.getTeams().size() == 0) {
            // The teams balance evenly.
        	String player = highestTeam.getRandomPlayer(this);
        	if(player != null) {
        		balancePlayer(player, lowestTeam); // Move one player from the team with the higher number of players to the lower.
        	}else {
        		loop++;
        		return false;
        	}
            
        } else {
        	String player = highestTeam.getRandomPlayer(this);
        	if(player != null) {
        		// The teams balance unevenly.
                balancePlayer(player, null); // Move one player from the team with the higher number of players to lobby.
        	}else {
        		loop++;
        		return false;
        	}
            
        }
        
        loop++;
        boolean balanced = balanceTeams(loop, balanceThreshold); // Check Teams again to check if balanced.
        return balanced;
    }

	private void balancePlayer (String p, Team newTeam) {      
        // Reseting player data       
        if (newTeam == null) {
            // Moving to Lobby
            playerData.get(p).getTeam().substractOneMemeberCount();
            //playerData.get(p).color = null;
            playerData.get(p).setTeam(null);
            playerData.get(p).setInArena(false);
            playerData.get(p).setInLobby(true);
            mainArena.getLobby().getPlayersInLobby().put(p, false);
            playerData.get(p).setReady(false);
            playerData.get(p).setJustJoined(true); // Flag for teleport
            playerData.get(p).setLobbyJoinTime(System.currentTimeMillis());     
            playerData.get(p).isWarned(false);
            playerData.get(p).setRole(null);
            
            Player player = getServer().getPlayer(p);
            
            // Remove Helmet
            player.getInventory().setHelmet(null);
            player.getInventory().remove(Material.WOOL);
            
            
            //It's deprecated but it's currently the only way to get the desired effect.
            player.updateInventory();
        
            // Get lobby location and move player to it.
            Location loc = new Location(getServer().getWorld(mainArena.getWorld()), mainArena.getLobby().getX(), mainArena.getLobby().getY() + 1, mainArena.getLobby().getZ());
            loc.setYaw((float) mainArena.getLobby().getDir());
            loc.getWorld().loadChunk(loc.getBlockX(), loc.getBlockZ());
            player.teleport(loc); // Teleport player to lobby
            Util.sendMessageToPlayers(this, ChatColor.GREEN + p + ChatColor.WHITE + " was moved to lobby! [Team-balancing]");
            
        } else {
            // Moving to other Team
            String oldteam = playerData.get(p).getTeam().getColor();
            ChatColor oldcc = playerData.get(p).getTeam().getChatColor();
            
            playerData.get(p).getTeam().substractOneMemeberCount();
            playerData.get(p).setTeam(newTeam);
            
            Player player = getServer().getPlayer(p);
                                   
            // Change wool colour and Helmet
            ItemStack[] contents = player.getInventory().getContents();
            int amountofwool = 0;
            for (ItemStack item : contents) {
                if (item == null) {
                    continue;
                }
                
                if (item.getType() == Material.WOOL) {
                    amountofwool += item.getAmount();
                }
            }
            
            player.getInventory().remove(Material.WOOL);
            
            //Give wool
            DyeColor color1 = DyeColor.valueOf(newTeam.getColor().toUpperCase());
            ItemStack helmet = new ItemStack(Material.WOOL, 1, color1.getData());
            player.getInventory().setHelmet(helmet);
            
            if (amountofwool !=0) {
                ItemStack wool = new ItemStack(Material.WOOL, amountofwool, color1.getData());
                player.getInventory().addItem(wool);
            }

            //It's deprecated but it's currently the only way to get the desired effect.
            player.updateInventory();
            
            // Get team spawn location and move player to it.
            Spawn spawn =
                    mainArena.getTeamSpawns().get(newTeam.getColor()) != null ?
                    mainArena.getTeamSpawns().get(newTeam.getColor()) :
                    newTeam.getSpawn();
            Location loc = new Location(getServer().getWorld(mainArena.getWorld()), spawn.getX(), spawn.getY(), spawn.getZ());
            loc.setYaw((float) spawn.getDir());
            getServer().getWorld(mainArena.getWorld()).loadChunk(loc.getBlockX(), loc.getBlockZ());
            boolean teleport = player.teleport(loc);
            if (!teleport) {
            	player.teleport(new Location(player.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ(), 0.0F, (float)spawn.getDir()));
            }
            Util.sendMessageToPlayers(this, 
                    newTeam.getChatColor() + player.getName() + ChatColor.WHITE + " changed teams from " 
                    + oldcc + oldteam + ChatColor.WHITE + " to "+ newTeam.getChatColor() + newTeam.getColor() + ChatColor.WHITE + "! [Team-balancing]");
            newTeam.addOneMemeberCount();
        }
    }

    public void checkForGameEndThenPlayerLeft () {
        if (this.playerData.size() < 2 && !isPreGame()) {
            //maybe dc or something. it should be moved to cheking to see players who left the game
            boolean zeroPlayers = true;
            for (int i = 0; i < mainArena.getTeams().size(); i++) {
                if (mainArena.getTeams().get(i).getMemberCount() == 1) {
                    zeroPlayers = false;
                    Util.sendMessageToPlayers(this, "The game has stopped because there are too few players. "
                            + mainArena.getTeams().get(i).getChatColor() + mainArena.getTeams().get(i).getColor().toUpperCase() + ChatColor.WHITE + " wins! (With a final score of "
                            + mainArena.getTeams().get(i).getScore() + ")");
                    blockListener.endGame(true);
                    break;
                }
            }
            if (zeroPlayers == true) {
                Util.sendMessageToPlayers(this, "No players left. Resetting game.");
                blockListener.endGame(true);
            }
        }
    }

    /** Checks and calculates the Player's killstreak and deathstreak and outputs an appropriate message according to config.
     * @param player The player
     * @param died If they died (false if they were the killer). */
    public void checkForKillMSG (Player player, boolean died) {
        PlayerData data = playerData.get(player.getName());
        if (died) {
            data.addOneDeath();
            data.addOneDeathInARow();
            data.setKillsInARow(0);
        } else {
            data.addOneKill();
            data.addOneKillInARow();
            data.setDeathsInARow(0);
            String message = mainArena.getConfigOptions().killStreakMessages.getMessage(data.getKillsInARow());

            if (!message.isEmpty()) {
                Util.sendMessageToPlayers(this, message.replace("%player", playerData.get(player.getName()).getTeam().getChatColor() + player.getName() + ChatColor.WHITE));
            }
        }

        playerData.put(player.getName(), data);
    }

	//TODO: Change this up to the new way.
    
    /** Checks whether the current mainArena is fit for purpose.
     * @param p Player doing the checking
     * @return An error message, else empty if the arena is safe.
     * @deprecated
     */
    public String checkMainArena (CommandSender sender, Arena arena) {
        if (arena == null) {
            // Arenas were loaded but a main arena wasn't selected.
            if (arena_list == null) {
                return "An arena hasn't been built yet.";
            } else if (!arena_list.isEmpty() && arena_list.get(0) != null) {
                String anArena = arena_list.get(0);
                mainArena = loadArena(anArena);
                editingArena = mainArena;
                if (mainArena == null) {
                    return "An arena hasn't been built yet.";
                } else {
                    arena = mainArena;
                }
            }
            return "An arena hasn't been built yet, try again later when an arena has been built.";
        }
        
        if (arena.getLobby() == null) {
            return "No lobby for main arena " + arena.getName() + ".";
        }
        
        if (getServer().getWorld(arena.getWorld()) == null) {
            if (Permissions.canAccess(sender, true, new String[] { "ctp.*", "ctp.admin" })) {
                return "The arena config is incorrect. The world \"" + arena.getWorld() + "\" could not be found. Hint: your first world's name is \"" + getServer().getWorlds().get(0).getName() + "\".";
            } else {
                return "Sorry, this arena has not been set up properly. Please tell an admin. [Incorrect World]";
            }
        }
        
        // Kj -- Test that the spawn points are within the map boundaries
        for (Spawn aSpawn : arena.getTeamSpawns().values()) {
            if (!playerListener.isInside((int) aSpawn.getX(), arena.getX1(), arena.getX2()) || !playerListener.isInside((int) aSpawn.getZ(), arena.getZ1(), arena.getZ2())) {
                if (Permissions.canAccess(sender, true, new String[] { "ctp.*", "ctp.admin" })) {
                    return "The spawn point \"" + aSpawn.getName() + "\" in the arena \"" + arena.getName() + "\" is out of the arena boundaries. "
                            + "[Spawn is " + (int) aSpawn.getX() + ", " + (int) aSpawn.getZ() + ". Boundaries are " + arena.getX1() + "<==>" + arena.getX2() + ", " + arena.getZ1() + "<==>" + arena.getZ2() + "].";
                } else {
                    return "Sorry, this arena has not been set up properly. Please tell an admin. [Incorrect Boundaries]";
                }
            }
        }
        return "";
    }

    /** This method changes the mainArena to a suitable arena using the number of players you have.
     * Note, it will not change the mainArena if useSelectedArenaOnly is set to true.
     * @param numberofplayers The number of players that want to play.
     * @return The name of the selected mainArena, else empty String. */
    public String chooseSuitableArena (int numberofplayers) {    	
        // Is the config set to allow the random choosing of arenas?
        if (!mainArena.getConfigOptions().useSelectedArenaOnly) {
            int size = arena_list.size();

            if (size > 1) {
                // If there is more than 1 arena to choose from
                List<String> arenas = new ArrayList<String>();
                for (String arena : arena_list) {
                    Arena loadArena = loadArena(arena);
                    if (loadArena.getMaxPlayers() >= numberofplayers && loadArena.getMinPlayers() <= numberofplayers) {
                        arenas.add(arena);
                        mainArena = loadArena; // Change the mainArena based on this.
                    }
                }
                if (arenas.size() > 1) {
                    Random random = new Random();
                    int nextInt = random.nextInt(size); // Generate a random number between 0 (inclusive) -> Number of arenas (exclusive)
                    mainArena = loadArena(arena_list.get(nextInt)) == null
                            ? mainArena : loadArena(arena_list.get(nextInt)); // Change the mainArena based on this. (Ternary null check)
                }
                getLogger().info("ChooseSuitableArena: Players found: " + numberofplayers + ", total arenas found: " + size + " " + arena_list + ", of which " + arenas.size() + " were suitable: " + arenas);

                // else ctp.mainArena = ctp.mainArena;
            }
            getLogger().info("The selected arena, " + mainArena.getName() + ", has a minimum of " + mainArena.getMinPlayers() + ", and a maximum of " + mainArena.getMaxPlayers() + ".");
            return mainArena.getName();
        }
        return mainArena.getName() == null ? "" : mainArena.getName();
    }

    public void clearConfig () {
        if (this.blockListener.capturegame) {
            this.blockListener.endGame(true);
        }
        
        if (!this.playerData.isEmpty()) {
            for (String player : playerData.keySet()) {
            	Player p = getServer().getPlayer(player);
                blockListener.restoreThings(p);
                sendMessage(p, ChatColor.RED + "Reloading plugin configuration. The CTP game has been terminated.");  // Kj
            }
        }
        
        clearSchedule();
        arena_list.clear();
        playerData.clear();
        healingItems.clear();
        rewards = null;
        mainArena = null;
        editingArena = null;
        roles.clear();
    }

    public void clearSchedule () {
        CTP_Scheduler.healingItemsCooldowns = 0;
        CTP_Scheduler.helmChecker = 0;
        CTP_Scheduler.lobbyActivity = 0;
        CTP_Scheduler.money_Score = 0;
        CTP_Scheduler.playTimer = 0;
        CTP_Scheduler.pointMessenger = 0;
        getServer().getScheduler().cancelTasks(this);
    }

    /** This method finds if a suitable arena exists.
     * If useSelectedArenaOnly is true, this method will only search the main arena.
     * @param numberofplayers The number of players that want to play.
     * @return If a suitable arena exists, else false. */
    public boolean hasSuitableArena (int numberofplayers) {
        // No arenas built
        if (arena_list == null || arena_list.isEmpty()) {
            return false;
        }
        
        // Is the config set to allow the random choosing of arenas?
        if (!mainArena.getConfigOptions().useSelectedArenaOnly) {
            int size = arena_list.size();
            if (size > 1) {
                // If there is more than 1 arena to choose from
                for (String arena : arena_list) {
                    Arena loadArena = loadArena(arena);
                    if (loadArena.getMaxPlayers() >= numberofplayers && loadArena.getMinPlayers() <= numberofplayers) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            if (mainArena.getMaxPlayers() >= numberofplayers && mainArena.getMinPlayers() <= numberofplayers) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean isGameRunning() {
        return this.blockListener.capturegame;
    }

    public boolean isPreGame() {
        return this.blockListener.preGame;
    }

    public void leaveGame(Player player, ArenaLeaveReason reason) {
        //On exit we get double signal
        if (playerData.get(player.getName()) == null) {
            return;
        }
        
        if (playerListener.waitingToMove != null && !playerListener.waitingToMove.isEmpty()) {
            if (player.getName() == playerListener.waitingToMove.get(0) && playerListener.waitingToMove.size() == 1) {
                playerListener.clearWaitingQueue(); // The player who left was someone in the lobby waiting to join. We need to remove them from the queue
            } else {
                playerListener.waitingToMove.remove(player.getName());
            }
        }

        // Removing player cooldowns
        for (HealingItems item : healingItems) {
            if (item != null && item.cooldowns != null && item.cooldowns.size() > 0) {
                for (String playName : item.cooldowns.keySet()) {
                    if (playName.equalsIgnoreCase(player.getName())) {
                        item.cooldowns.remove(playName);
                    }
                }
            }
        }
        
        Util.sendMessageToPlayers(this, player, ChatColor.GREEN + player.getName() + ChatColor.WHITE + " left the CTP game!"); // Won't send to "player".
        
        //Remove the number count from the teamdata
        if (playerData.get(player.getName()).getTeam() != null) {
            for (int i = 0; i < mainArena.getTeams().size(); i++) {
                if (mainArena.getTeams().get(i) == (playerData.get(player.getName()).getTeam())) {
                    mainArena.getTeams().get(i).substractOneMemeberCount();
                    break;
                }
            }
        }

        CTPPlayerLeaveEvent event = new CTPPlayerLeaveEvent(player, editingArena, playerData.get(player.getName()), reason);
        getPluginManager().callEvent(event);
        
        this.mainArena.getLobby().getPlayersInLobby().remove(player.getName());
        this.blockListener.restoreThings(player);
        this.previousLocation.remove(player.getName());
        this.playerData.remove(player.getName());

        // Check for player replacement if there is someone waiting to join the game
        boolean wasReplaced = false;
        if (mainArena.getConfigOptions().exactTeamMemberCount && isGameRunning()) {
            for (String playerName : playerData.keySet()) {
                if (playerData.get(playerName).inLobby() && playerData.get(playerName).isReady()) {
                    this.playerListener.moveToSpawns(playerName);
                    wasReplaced = true;
                    break;
                }
            }
        }

        //check for player count, only then were no replacement
        if (!wasReplaced) {
            checkForGameEndThenPlayerLeft();
        }
            
        //If there was no replacement we should move one member to lobby
        if (!wasReplaced && mainArena.getConfigOptions().exactTeamMemberCount && this.isGameRunning()) {
            if (mainArena.getConfigOptions().balanceTeamsWhenPlayerLeaves > 0) {
                balanceTeams(0, mainArena.getConfigOptions().balanceTeamsWhenPlayerLeaves);
            }
        }
    }

    public void loadArenas (File file) {
        if (file.isDirectory()) {
        	if(firstTime) {
        		file.mkdirs();
        		return;
        	}else {
                String[] internalNames = file.list();
                for (String name : internalNames) {
                	if (!name.startsWith("."))
                		loadArena(new File(file.getAbsolutePath() + File.separator + name));
                }
        	}
        }
    }
    
    public void loadArena(File file) {
    	String fileName = file.getName().split("\\.")[0];
    	if(!arenas.containsKey(fileName)) {
    		arenas.put(fileName, new Arena(fileName));
    	}
    }

    public void loadConfigFiles () {
        loadRoles();
        loadRewards();
        loadHealingItems();

        //Load existing arenas
        File dir = new File(mainDir + File.separator + "Arenas");
        loadArenas(dir);

        // Load arenas boundaries
        for(int i = 0; i < arena_list.size(); i++) {
            Arena tmp = loadArena(arena_list.get(i));
            ArenaBoundaries tmpBound = new ArenaBoundaries();
            //tmpBound.arenaName = tmp.name;
            tmpBound.setWorld(tmp.getWorld());
            tmpBound.setx1(tmp.getX1());
            tmpBound.setx2(tmp.getX2());
            tmpBound.sety1(tmp.getY1());
            tmpBound.sety2(tmp.getY2());
            tmpBound.setz1(tmp.getZ1());
            tmpBound.setz2(tmp.getZ2());

            arenasBoundaries.put(tmp.getName(), tmpBound);
        }

        globalConfigOptions = ConfigTools.getConfigOptions(globalConfigFile);
        FileConfiguration globalConfig = load();

        String arenaName = globalConfig.getString("Arena");
        if (arenaName == null) {
            mainArena = null;
        } else {
            mainArena = loadArena(arenaName);
        }
        editingArena = mainArena;
        if (mainArena == null) {
            globalConfig.set("Arena", null);
            try {
                globalConfig.options().copyDefaults(true);
                globalConfig.save(globalConfigFile);
            } catch (IOException ex) {
               ex.printStackTrace();
               getLogger().severe("Error while saving the main arena config.");
            }
        }

        CTP_Scheduler.money_Score = 0;
        CTP_Scheduler.playTimer = 0;
        CTP_Scheduler.pointMessenger = 0;
        CTP_Scheduler.helmChecker = 0;
        CTP_Scheduler.lobbyActivity = 0;
        CTP_Scheduler.healingItemsCooldowns = 0;
    }

    /**Loads ArenaData data ready for assignment to mainArena */
    public Arena loadArena (String name) {
        Arena arena = new Arena();

        if (arena_list.contains(name))  {
            File arenaFile = new File(mainDir + File.separator + "Arenas" + File.separator + name + ".yml");
            FileConfiguration arenaConf = YamlConfiguration.loadConfiguration(arenaFile);
            
            String world = arenaConf.getString("World");
            
            // Kj -- check the world to see if it exists. 
            try {
                getServer().getWorld(world);
                arena.setWorld(world);
            } catch (Exception ex) {
            	getLogger().warning("WARNING: " + name + " has an incorrect World. The World in the config, \"" + world + "\", could not be found. ###");
                List<String> worlds = new LinkedList<String>();
                for (World aWorld : getServer().getWorlds()) {
                    worlds.add(aWorld.getName());
                }
                if (worlds.size() == 1) {
                    arena.setWorld(worlds.get(0));
                    getLogger().info("Successfully resolved the world. \"" + arena.getWorld() + "\" will be used.");
                } else {
                	getLogger().info("This usually happens on the first load, create an arena and this message should go away.");
                	getLogger().info("Could not resolve the world. Please fix this manually. Hint: Your installed worlds are: " + worlds);
                }
            }

            arena.setName(name);
            
            if(!arenaConf.contains("MaximumPlayers"))
                arenaConf.set("MaximumPlayers", 9999);
            if(!arenaConf.contains("MinimumPlayers"))
                arenaConf.set("MinimumPlayers", 4);

            arena.setMaxPlayers(arenaConf.getInt("MaximumPlayers", 9999));
            arena.setMinPlayers(arenaConf.getInt("MinimumPlayers", 4));
            if (arenaConf.contains("Points")) {
                for (String str : arenaConf.getConfigurationSection("Points").getKeys(false)) {
                    Points tmps = new Points();
                    tmps.setName(str);
                    str = "Points." + str;
                    tmps.setX(arenaConf.getInt(str + ".X", 0));
                    tmps.setY(arenaConf.getInt(str + ".Y", 0));
                    tmps.setZ(arenaConf.getInt(str + ".Z", 0));

                    // Load teams that are not allowed to capture
                    String teamColors = arenaConf.getString(str + ".NotAllowedToCaptureTeams");
                    if(teamColors == null) {
                        tmps.setNotAllowedToCaptureTeams(null);
                    } else {
                        // Trim commas and whitespace, and split items by commas
                        teamColors = teamColors.toLowerCase();
                        teamColors = teamColors.trim();
                        teamColors = teamColors.replaceAll(" ", "");

                        if (teamColors.endsWith(",")) {
                            teamColors = teamColors.substring(0, teamColors.length() - 1);
                        }
                        
                        String[] tc = teamColors.split(",");

                        tmps.getNotAllowedToCaptureTeams().addAll(Arrays.asList(tc));
                    }

                    if (arenaConf.contains(str + ".Dir")) {
                        tmps.setPointDirection(arenaConf.getString(str + ".Dir"));
                    }
                    arena.getCapturePoints().add(tmps);
                }
            }
            if (arenaConf.contains("Team-Spawns")) {
                for (String str : arenaConf.getConfigurationSection("Team-Spawns").getKeys(false)) {
                    Spawn spawn = new Spawn();
                    spawn.setName(str);
                    str = "Team-Spawns." + str;
                    spawn.setX(arenaConf.getDouble(str + ".X", 0.0D));
                    spawn.setY(arenaConf.getDouble(str + ".Y", 0.0D));
                    spawn.setZ(arenaConf.getDouble(str + ".Z", 0.0D));
                    spawn.setDir(arenaConf.getDouble(str + ".Dir", 0.0D));
                    arena.getTeamSpawns().put(spawn.getName(), spawn);

                    Team team = new Team();
                    team.setSpawn(spawn);
                    team.setColor(spawn.getName());
                    team.setMemberCount(0);
                    
                    try {
                        team.setChatColor(ChatColor.valueOf(spawn.getName().toUpperCase()));
                    } catch (Exception ex) {
                        team.setChatColor(ChatColor.GREEN);
                    }

                    // Check if this spawn is already in the list
                    boolean hasTeam = false;

                    for (Team aTeam : arena.getTeams()) {
                        if (aTeam.getColor().equalsIgnoreCase(spawn.getName())) {
                            hasTeam = true;
                            break;
                        }
                    }

                    if (!hasTeam) {
                        arena.getTeams().add(team);
                    }
                }
            }
            // Arena boundaries
            arena.setX1(arenaConf.getInt("Boundarys.X1", 0));
            arena.setY1(arenaConf.getInt("Boundarys.Y1", 0));
            arena.setZ1(arenaConf.getInt("Boundarys.Z1", 0));
            arena.setX2(arenaConf.getInt("Boundarys.X2", 0));
            arena.setY2(arenaConf.getInt("Boundarys.Y2", 0));
            arena.setZ2(arenaConf.getInt("Boundarys.Z2", 0));


            Lobby lobby = new Lobby(
                    arenaConf.getDouble("Lobby.X", 0.0D),
                    arenaConf.getDouble("Lobby.Y", 0.0D),
                    arenaConf.getDouble("Lobby.Z", 0.0D),
                    arenaConf.getDouble("Lobby.Dir", 0.0D));
            arena.setLobby(lobby);
            if ((lobby.getX() == 0.0D) && (lobby.getY() == 0.0D) && (lobby.getZ() == 0.0D) && (lobby.getDir() == 0.0D)) {
                arena.setLobby(null);
            }

            // Kj -- Test that the spawn points are within the map boundaries
            for (Spawn aSpawn : arena.getTeamSpawns().values()) {
                if (!playerListener.isInside((int) aSpawn.getX(), arena.getX1(), arena.getX2()) || !playerListener.isInside((int) aSpawn.getZ(), arena.getZ1(), arena.getZ2())) {
                	getLogger().warning("WARNING: The spawn point \"" + aSpawn.getName() + "\" in the arena \"" + arena.getName() + "\" is out of the arena boundaries. ###");
                    continue;
                }
            }

            try {
                arenaConf.options().copyDefaults(true);
                arenaConf.save(arenaFile);
            } catch (IOException ex) {
                Logger.getLogger(CaptureThePoints.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            arena.setConfigOptions(ConfigTools.getArenaConfigOptions(arenaFile));

            return arena;
        } else {
        	getLogger().warning("Could not load arena! Check your config file and existing arenas");
            return null;
        }
    }

    public void loadHealingItems () {
        FileConfiguration config = load();
        // Healing items loading
        if (!config.contains("HealingItems")) {
            config.set("HealingItems.BREAD.HOTHeal", 1);
            config.set("HealingItems.BREAD.HOTInterval", 1);
            config.set("HealingItems.BREAD.Duration", 5);
            config.set("HealingItems.BREAD.Cooldown", 0);
            config.set("HealingItems.BREAD.ResetCooldownOnDeath", false);
            config.set("HealingItems.GOLDEN_APPLE.InstantHeal", 20);
            config.set("HealingItems.GOLDEN_APPLE.Cooldown", 60);
            config.set("HealingItems.GOLDEN_APPLE.ResetCooldownOnDeath", true);
            config.set("HealingItems.GRILLED_PORK.HOTHeal", 1);
            config.set("HealingItems.GRILLED_PORK.HOTInterval", 3);
            config.set("HealingItems.GRILLED_PORK.Duration", 5);
            config.set("HealingItems.GRILLED_PORK.Cooldown", 10);
            config.set("HealingItems.GRILLED_PORK.InstantHeal", 5);
            config.set("HealingItems.GRILLED_PORK.ResetCooldownOnDeath", true);
        }
        
        int itemNR = 0;
        for (String str : config.getConfigurationSection("HealingItems").getKeys(false)) {
            itemNR++;
            HealingItems hItem = new HealingItems();
            try {
                hItem.item = Util.getItemListFromString(str).get(0);
                hItem.instantHeal = config.getInt("HealingItems." + str + ".InstantHeal", 0);
                hItem.hotHeal = config.getInt("HealingItems." + str + ".HOTHeal", 0);
                hItem.hotInterval = config.getInt("HealingItems." + str + ".HOTInterval", 0);
                hItem.duration = config.getInt("HealingItems." + str + ".Duration", 0);
                hItem.cooldown = config.getInt("HealingItems." + str + ".Cooldown", 0);
                hItem.resetCooldownOnDeath = config.getBoolean("HealingItems." + str + ".ResetCooldownOnDeath", true);
            } catch (Exception e) {
            	getLogger().warning("Error while loading Healing items! " + itemNR + " item!");
            }

            healingItems.add(hItem);
        }
        
        try {
            config.options().copyDefaults(true);
            config.save(globalConfigFile);
        } catch (IOException ex) {
        	ex.printStackTrace();
            logSevere("Couldn't save the global config file, please see the StackTrace above.");
        }
    }

    public void loadRoles () {
        FileConfiguration config = load();
        if (!config.contains("Roles")) {
            config.set("Roles.Tank.Items", "268, 297:16, DIAMOND_CHESTPLATE, 308, 309, SHEARS, CAKE");
            config.set("Roles.Fighter.Items", "272, 297:4, 261, 262:32, CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_BOOTS");
            config.set("Roles.Ranger.Items", "268, 297:6, 261, 262:256, 299, 300, 301");
            config.set("Roles.Berserker.Items", "267, GOLDEN_APPLE:2");
        }
        
        for (String str : config.getConfigurationSection("Roles").getKeys(false)) {
            String text = config.getString("Roles." + str + ".Items");
            roles.put(str.toLowerCase(), Util.getItemListFromString(text));
        }
        
        try {
            config.options().copyDefaults(true);
            config.save(globalConfigFile);
        } catch (IOException ex) {
            ex.printStackTrace();
            logSevere("Couldn't save the global config file, please see the StackTrace above.");
        }
    }

    public void loadRewards () {
        FileConfiguration config = load();
        if (!config.contains("Rewards")) {
            config.set("Rewards.WinnerTeam.ItemCount", "2");
            config.set("Rewards.WinnerTeam.Items", "DIAMOND_LEGGINGS, DIAMOND_HELMET, DIAMOND_CHESTPLATE, DIAMOND_BOOTS, DIAMOND_AXE, DIAMOND_HOE, DIAMOND_PICKAXE, DIAMOND_SPADE, DIAMOND_SWORD");
            config.set("Rewards.OtherTeams.ItemCount", "1");
            config.set("Rewards.OtherTeams.Items", "CAKE, RAW_FISH:5, COAL:5, 56, GOLDEN_APPLE");
            config.set("Rewards.ForKillingEnemy", "APPLE, BREAD, ARROW:10");
            config.set("Rewards.ForCapturingThePoint", "CLAY_BRICK, SNOW_BALL:2, SLIME_BALL, IRON_INGOT");
            config.set("Rewards.ExpRewardForKillingOneEnemy", "0");
        }
        
        rewards = new Rewards();
        rewards.setExpRewardForKillingEnemy(config.getInt("Rewards.ExpRewardForKillingOneEnemy", 0));
        rewards.setWinnerRewardCount(config.getInt("Rewards.WinnerTeam.ItemCount", 2));
        rewards.setWinnerRewards(Util.getItemListFromString(config.getString("Rewards.WinnerTeam.Items")));
        rewards.setOtherTeamRewardCount(config.getInt("Rewards.OtherTeams.ItemCount", 1));
        rewards.setLooserRewards(Util.getItemListFromString(config.getString("Rewards.OtherTeams.Items")));
        rewards.setRewardsForCapture(Util.getItemListFromString(config.getString("Rewards.ForCapturingThePoint")));
        rewards.setRewardsForKill(Util.getItemListFromString(config.getString("Rewards.ForKillingEnemy")));
        
        try {
            config.options().copyDefaults(true);
            config.save(globalConfigFile);
        } catch (IOException ex) {
            ex.printStackTrace();
            logSevere("Unable to save the global config file, please see the above StackTrace.");
        }
    }

    public void moveToLobby (Player player) {
        String mainArenaCheckError = checkMainArena(player, mainArena); // Kj -- Check arena, if there is an error, an error message is returned.
        if (!mainArenaCheckError.isEmpty()) {
            sendMessage(player, mainArenaCheckError);
            return;
        }

        // Some more checks
        if (player.isInsideVehicle()) {
            try {
                player.leaveVehicle();
            } catch (Exception e) {
                // May sometimes reach this if player is riding an entity other than a Minecart
            }
        }
        if (player.isSleeping()) {
            player.kickPlayer("Banned for life... Nah, just don't join from a bed ;)");
            return;
        }

        if (playerData.isEmpty()) {
            mainArena.getLobby().getPlayersInLobby().clear();   //Reset if first to come
        }

        if(economyHandler != null && this.mainArena.getConfigOptions().economyMoneyCostForJoiningArena != 0) {
            EconomyResponse r = economyHandler.bankWithdraw(player.getName(), mainArena.getConfigOptions().economyMoneyCostForJoiningArena);
            if(r.transactionSuccess()) {
                sendMessage(player, "You were charged " + ChatColor.GREEN + r.amount + ChatColor.WHITE + " for entering " + ChatColor.GREEN + mainArena.getName() + ChatColor.WHITE + " arena.");
            } else {
                sendMessage(player, "You dont have enough money to join arena!");
                return;
            }
        }
        
        // Assign player's PlayerData
        PlayerData data = new PlayerData();
        data.setDeaths(0);
        data.setDeathsInARow(0);
        data.setKills(0);
        data.setKillsInARow(0);
        data.setMoney(mainArena.getConfigOptions().moneyAtTheLobby);
        data.setPointsCaptured(0);
        data.setReady(false);
        data.setInArena(false);
        data.setFoodLevel(player.getFoodLevel());
        data.setHealth(player.getHealth());
        data.setLobbyJoinTime(System.currentTimeMillis());
        
        // Store and remove potion effects on player
        data.setPotionEffects(PotionManagement.storePlayerPotionEffects(player));
        PotionManagement.removeAllEffects(player);
        
        playerData.put(player.getName(), data);

        // Save player's previous state 
        player.setFoodLevel(20);
        if (player.getGameMode() == GameMode.CREATIVE) {
            data.inCreative(true);
            player.setGameMode(GameMode.SURVIVAL);
        }

        mainArena.getLobby().getPlayersInLobby().put(player.getName(), false); // Kj
        mainArena.getLobby().getPlayersWhoWereInLobby().add(player.getName()); // Kj

        //Set the player's health and also trigger an event to happen because of it, add compability with other plugins
        player.setHealth(mainArena.getConfigOptions().maxPlayerHealth);
        EntityRegainHealthEvent regen = new EntityRegainHealthEvent(player, mainArena.getConfigOptions().maxPlayerHealth, RegainReason.CUSTOM);
    	pluginManager.callEvent(regen);
        
        // Get lobby location and move player to it.
        Location loc = new Location(getServer().getWorld(mainArena.getWorld()), mainArena.getLobby().getX(), mainArena.getLobby().getY() + 1, mainArena.getLobby().getZ());
        loc.setYaw((float) mainArena.getLobby().getDir());
        if(!loc.getWorld().isChunkLoaded(loc.getChunk())) {
        	loc.getWorld().loadChunk(loc.getChunk());
        }

        Double X = Double.valueOf(player.getLocation().getX());
        Double y = Double.valueOf(player.getLocation().getY());
        Double z = Double.valueOf(player.getLocation().getZ());

        Location previous = new Location(player.getWorld(), X.doubleValue(), y.doubleValue(), z.doubleValue());
        previousLocation.put(player.getName(), previous);

        Util.sendMessageToPlayers(this, ChatColor.GREEN + player.getName() + ChatColor.WHITE + " joined a CTP game.");

        // Get lobby location and move player to it.        
        player.teleport(loc); // Teleport player to lobby
        sendMessage(player, ChatColor.GREEN + "Joined CTP lobby " + ChatColor.GOLD + mainArena.getName() + ChatColor.GREEN + ".");
        playerData.get(player.getName()).setInLobby(true);
        InvManagement.saveInv(player);
        
        //Call a custom event for when players join the arena
        CTPPlayerJoinEvent event = new CTPPlayerJoinEvent(player, editingArena, playerData.get(player.getName()));
        getPluginManager().callEvent(event);
    }

    /** Add the CTP commands to the master commands list */
    private void populateCommands () {
        commands.clear();
        commands.add(new AliasesCommand(this));
        commands.add(new AutoCommand(this));
        commands.add(new BuildCommand(this));
        commands.add(new ColorsCommand(this));
        commands.add(new DebugCommand(this));
        commands.add(new HelpCommand(this));
        commands.add(new JoinAllCommand(this));
        commands.add(new JoinCommand(this));
        commands.add(new KickCommand(this));
        commands.add(new LateJoinCommand(this));
        commands.add(new LeaveCommand(this));
        commands.add(new PJoinCommand(this));
        commands.add(new RejoinCommand(this));
        commands.add(new ReloadCommand(this));
        //commands.add(new SaveCommand(this));
        commands.add(new SelectCommand(this));
        commands.add(new SetpointsCommand(this));
        //commands.add(new SetpointCommand(this));
        commands.add(new StartCommand(this));
        commands.add(new StatsCommand(this));
        commands.add(new StopCommand(this));
        commands.add(new TeamCommand(this));
        commands.add(new VersionCommand(this));
    }

    public void resetArenaList () {
        arena_list.clear();
        
        //Load existing arenas
        File dir = new File(mainDir + File.separator + "Arenas");
        loadArenas(dir);
    }
    
    private boolean setupPermissions() {
    	RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
    	
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
            getLogger().info("Vault plugin found, permission support enabled.");
            UsePermissions = true;
        }else {
        	getLogger().info("Permission system not detected, defaulting to OP");
            UsePermissions = false;
        }
        
        return (permission != null);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
        	getLogger().info("Vault plugin not detected, disabling economy support.");
            return false;
        }

        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economyHandler = economyProvider.getProvider();
        }

        if(economyHandler != null)
        	getLogger().info("Vault plugin found, economy support enabled.");

        return economyHandler != null;
    }
    
    /** Returns the plugin manager, non statically. */
    public PluginManager getPluginManager() {
    	return this.pluginManager;
    }
    
    public File getGlobalConfig() {
    	return this.globalConfigFile;
    }
    
    public String getMainDirectory() {
    	return this.mainDir;
    }
    
    public ConfigOptions getGlobalConfigOptions() {
    	return this.globalConfigOptions;
    }
    
    /**
     * Returns the Rewards data.
     * @see Rewards
     */
    public Rewards getRewards() {
    	return this.rewards;
    }
    
    /** Returns the Hashmap of the player inventories. */
    public HashMap<String, ItemStack[]> getInventories() {
    	return this.Inventories;
    }
    
    /** Returns the Hashmap of the armor stored. */
    public HashMap<String, ItemStack[]> getArmor() {
    	return this.armor;
    }
    
    /**
     * Send a message to the player, with color and a prefix.
     * 
     * @param p The player in which to send the message to.
     * @param message The message to send. "[CTP] " has been included.
     */
    public void sendMessage(Player p, String message) {
    	p.sendMessage(ChatColor.AQUA + "[CTP] " + ChatColor.WHITE + message);
    }
    
    /**
     * Provide a way to get the logger.
     */
    public void logSevere(String msg) {
    	getLogger().severe(msg);
    }
    
	public void logWarning(String msg) {
		getLogger().warning(msg);
	}
    
    public void logInfo(String msg) {
    	getLogger().info(msg);
    }
}