package me.dalton.capturethepoints;

import me.dalton.capturethepoints.listeners.CaptureThePointsPlayerListener;
import me.dalton.capturethepoints.listeners.CaptureThePointsBlockListener;
import me.dalton.capturethepoints.listeners.CaptureThePointsEntityListener;
import me.dalton.capturethepoints.listeners.TagAPIListener;
import me.dalton.capturethepoints.util.ArenaUtils;
import me.dalton.capturethepoints.util.ConfigTools;
import me.dalton.capturethepoints.util.LangTools;
import me.dalton.capturethepoints.util.MoneyUtils;
import me.dalton.capturethepoints.util.InvManagement;
import me.dalton.capturethepoints.util.Permissions;
import me.dalton.capturethepoints.beans.ArenaBoundaries;
import me.dalton.capturethepoints.beans.Arena;
import me.dalton.capturethepoints.beans.Items;
import me.dalton.capturethepoints.beans.PlayerData;
import me.dalton.capturethepoints.beans.Rewards;
import me.dalton.capturethepoints.commands.*;
import me.dalton.capturethepoints.enums.ArenaLeaveReason;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.io.IOException;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class CaptureThePoints extends JavaPlugin {
	private Permission permission = null;
    private Economy economy = null;
    private boolean UsePermissions;
    private boolean useTag = false;

    /** "plugins/CaptureThePoints" */
    private String mainDir;

    /** "plugins/CaptureThePoints/CaptureSettings.yml" */
    //public static final File myFile = new File(mainDir + File.separator + "CaptureSettings.yml");
    private File globalConfigFile = null;

    private PluginManager pluginManager = null;
    
    /** The master control over most of the arena related stuff. */
    private ArenaMaster arenaMaster = null;

    /** List of commands accepted by CTP */
    private List<CTPCommand> commands = new ArrayList<CTPCommand>(); // Kj

    private final CaptureThePointsBlockListener blockListener = new CaptureThePointsBlockListener(this);//TODO: Make these all start off null and then onEnable set them.
    private final CaptureThePointsEntityListener entityListener = new CaptureThePointsEntityListener(this);
    private final CaptureThePointsPlayerListener playerListener = new CaptureThePointsPlayerListener(this);
    private ArenaUtils aUtil = new ArenaUtils(this);
    private ConfigTools cTools = new ConfigTools(this);
    private InvManagement invMan = new InvManagement(this);
    private MoneyUtils mUtil = new MoneyUtils(this);
    private Permissions perm = new Permissions(this);
    private Util util = new Util(this);
    private ArenaRestore arenaRestore = new ArenaRestore(this);
    private MysqlConnector mysqlConnector = new MysqlConnector(this);
    
    private LanguageOptions lo = null;
    
    //General scheduler ids
    private int lobbyActivity = 0;

    private final HashMap<String, ItemStack[]> Inventories = new HashMap<String, ItemStack[]>();

    private HashMap<String, ItemStack[]> armor = new HashMap<String, ItemStack[]>();
    
    /** The global config options for CTP. */
    private ConfigOptions globalConfigOptions = new ConfigOptions();

    /** The roles/classes stored by CTP. (HashMap: Role's name, and the Items it contains) 
     * @see Items */
    private HashMap<String, List<Items>> roles = new HashMap<String, List<Items>>();

    /** The list of Healing Items stored by CTP. */
    private List<HealingItems> healingItems = new LinkedList<HealingItems>();

    /** The list of Rewards stored by CTP. */
    private Rewards rewards = new Rewards();

    public int arenaRestoreTimesRestored = 0;
    public int arenaRestoreMaxRestoreTimes = 0;
    public int arenaRestoreTimesRestoredSec = 0;   //For second time
    public int arenaRestoreMaxRestoreTimesSec = 0;
    
    /** If we're loading the plugin for the first time, defaults to false. */
    private boolean firstTime = false;

    /** Name of the player who needs teleporting. */
    public String playerNameForTeleport = ""; // Block destroy - teleport protection

    @Override
    public void onEnable () {
    	pluginManager = getServer().getPluginManager();
    	if(!pluginManager.isPluginEnabled("Vault")) {
    		logSevere("Vault is required in order to use this plugin.");
    		logSevere("dev.bukkit.org/server-mods/vault/");
			pluginManager.disablePlugin(this);
			return;
		}
    	
    	mainDir = this.getDataFolder().toString();
    	if(mainDir.isEmpty()) firstTime = true;
    	globalConfigFile = new File(mainDir + File.separator + "CaptureSettings.yml");
    	
    	arenaMaster = new ArenaMaster(this);
    	
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
            useTag = setupTag();
            if(useTag)
            	logInfo("TagAPI hooked! Player's names will correlate to their team color.");

            // REGISTER EVENTS-----------------------------------------------------------------------------------
            pluginManager.registerEvents(blockListener, this);
            pluginManager.registerEvents(entityListener, this);
            pluginManager.registerEvents(playerListener, this);

            populateCommands();
        }
        
        loadConfigFiles(reloading);

        // Checks for mysql
        if(this.globalConfigOptions.enableHardArenaRestore)
            mysqlConnector.checkMysqlData();

        //Kj: LobbyActivity timer.
        lobbyActivity = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run () {
            	if(arenaMaster.getArenas().isEmpty()) return; //if we don't have any arenas, return and do nothing
            	
                if (globalConfigOptions.lobbyKickTime <= 0) {
                    return;
                }

                for(Arena a : arenaMaster.getArenas()) {
	                for (String player : a.getPlayersData().keySet()) {
	                    PlayerData data = a.getPlayerData(player);
	                    Player p = getServer().getPlayer(player);
	                    if (data.inLobby() && !data.isReady()) {
	                        // Kj -- Time inactivity warning.
	                        if (((System.currentTimeMillis() - data.getLobbyJoinTime()) >= ((globalConfigOptions.lobbyKickTime * 1000) / 2)) && !data.hasBeenWarned()) {
	                            sendMessage(p, getLanguage().READY_UP_REMINDER);
	                            data.isWarned(true);
	                        }
	
	                        // Kj -- Time inactive in the lobby is greater than the lobbyKickTime specified in config (in ms)
	                        if ((System.currentTimeMillis() - data.getLobbyJoinTime() >= (globalConfigOptions.lobbyKickTime * 1000)) && data.hasBeenWarned()) {
	                            data.setInLobby(false);
	                            data.setInArena(false);
	                            data.isWarned(false);
	                            a.leaveGame(p, ArenaLeaveReason.PLAYER_NOT_READY);
	                            sendMessage(p, getLanguage().NOT_READY_KICK);
	                        }
	                    }
	                }
                }
            }

        }, 200L, 200L); // 10 sec
        
        logInfo("Loaded " + arenaMaster.getArenas().size() + " arena" + ((arenaMaster.getArenas().size() > 1 || arenaMaster.getArenas().size() == 0) ? "s!" : "!"));
    }

    @Override
    public void onDisable () {
        if (lobbyActivity != 0) {
            getServer().getScheduler().cancelTask(lobbyActivity);
            lobbyActivity = 0;
        }
        
        arenaRestore.cancelArenaRestoreSchedules();
        clearConfig();
        pluginManager = null;
        economy = null;
        permission = null;
        commands.clear();
    }
    
    public void clearConfig() {
    	if(arenaMaster != null)
	    	for(Arena a : getArenaMaster().getArenas())
	    		a.endGame(false, false);//Don't give rewards, the game ended prematurely
    	
        healingItems.clear();
        rewards = null;
        roles.clear();
    }

    @Override
    public boolean onCommand (CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("ctp"))
            return true;

        if (commands == null || commands.isEmpty()) // Really weird bug that rarely occurs. Could call it a sanity check.
            populateCommands();

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

        logInfo(sender.getName() + " issued an unknown CTP command. It has " + parameters.size() + " Parameters: " + parameters + ". Displaying help to them.");
        sendHelp(sender);
        return true;
    }

    /** Send the CTP help to this sender */
    public void sendHelp(CommandSender sender) {
        HelpCommand helpCommand = new HelpCommand(this);
        helpCommand.execute(sender, Arrays.asList("ctp"));
    }

    private void loadConfigFiles(boolean reloading) {
    	if(reloading) {
    		for(Arena a : arenaMaster.getArenas())
    			a.endGame(false, false);//Don't give rewards as the game ended prematurely.
    		arenaMaster.resetArenas();
    	}
    	
        // Load the default and global config before the arenas are loaded.
        globalConfigOptions = getConfigTools().getConfigOptions(globalConfigFile);
        FileConfiguration globalConfig = getConfigTools().load();
    	
        loadRoles();
        loadRewards();
        loadHealingItems();
        
        //Load existing arenas
        getArenaMaster().loadArenas(new File(mainDir + File.separator + "Arenas"));

        // Load arenas boundaries
        for(Arena a : getArenaMaster().getArenas()) {
            ArenaBoundaries tmpBound = new ArenaBoundaries();
            tmpBound.setWorld(a.getWorld().getName());
            tmpBound.setFirstVector(a.getFirstCorner());
            tmpBound.setSecondVector(a.getSecondCorner());

            getArenaMaster().getArenasBoundaries().put(a.getName(), tmpBound);
        }

        String arenaName = globalConfig.getString("Arena");
        if (arenaName == null)
        	getArenaMaster().clearSelectedArena();
        else if (getArenaMaster().getArena(arenaName) == null)
        	getArenaMaster().clearSelectedArena();
        else
        	getArenaMaster().setSelectedArena(arenaName);
        
        lo = LangTools.getLanguageOptions(this);
    }

    private void loadHealingItems() {
        FileConfiguration config = getConfigTools().load();
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
                hItem.item = getUtil().getItemListFromString(str).get(0);
                hItem.instantHeal = config.getInt("HealingItems." + str + ".InstantHeal", 0);
                hItem.hotHeal = config.getInt("HealingItems." + str + ".HOTHeal", 0);
                hItem.hotInterval = config.getInt("HealingItems." + str + ".HOTInterval", 0);
                hItem.duration = config.getInt("HealingItems." + str + ".Duration", 0);
                hItem.cooldown = config.getInt("HealingItems." + str + ".Cooldown", 0);
                hItem.resetCooldownOnDeath = config.getBoolean("HealingItems." + str + ".ResetCooldownOnDeath", true);
            } catch (Exception e) {
            	logWarning("Error while loading Healing items! " + itemNR + " item!");
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

    private void loadRoles () {
        FileConfiguration config = getConfigTools().load();
        if (!config.contains("Roles")) {
            config.set("Roles.Tank.Items", "WOOD_SWORD:2|16~4|19~2|20~3{Grays Sword/Testing one/Testing two}, BREAD:16, DIAMOND_CHESTPLATE|0~4, 308, 309, SHEARS");
            config.set("Roles.Fighter.Items", "272, 297:4, 261, 262:32, CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_BOOTS|2~10");
            config.set("Roles.Ranger.Items", "268, 297:6, 261, 262:256, 299, 300, 301");
            config.set("Roles.Berserker.Items", "267, GOLDEN_APPLE:2");
        }
        
        for (String role : config.getConfigurationSection("Roles").getKeys(false)) {
            String text = config.getString("Roles." + role + ".Items");
            List<Items> items = getUtil().getItemListFromString(text);
            
            if(items == null || items.isEmpty()) {
            	logWarning("There was an error loading the role " + role + "'s items, please check it is correct.");
            	continue;
            }
            
            roles.put(role.toLowerCase(), items);
        }
        
        try {
            config.options().copyDefaults(true);
            config.save(globalConfigFile);
        } catch (IOException ex) {
            ex.printStackTrace();
            logSevere("Couldn't save the global config file, please see the StackTrace above.");
        }
    }

    private void loadRewards () {
        FileConfiguration config = getConfigTools().load();
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
        rewards.setWinnerRewards(getUtil().getItemListFromString(config.getString("Rewards.WinnerTeam.Items")));
        rewards.setOtherTeamRewardCount(config.getInt("Rewards.OtherTeams.ItemCount", 1));
        rewards.setLooserRewards(getUtil().getItemListFromString(config.getString("Rewards.OtherTeams.Items")));
        rewards.setRewardsForCapture(getUtil().getItemListFromString(config.getString("Rewards.ForCapturingThePoint")));
        rewards.setRewardsForKill(getUtil().getItemListFromString(config.getString("Rewards.ForKillingEnemy")));
        
        try {
            config.options().copyDefaults(true);
            config.save(globalConfigFile);
        } catch (IOException ex) {
            ex.printStackTrace();
            logSevere("Unable to save the global config file, please see the above StackTrace.");
        }
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
        commands.add(new ListCommand(this));
        commands.add(new PJoinCommand(this));
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
    
    private boolean setupPermissions() {
    	RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
    	
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
            logInfo("Vault plugin found, permission support enabled.");
            UsePermissions = true;
        }else {
        	logInfo("Permission system not detected, defaulting to OP");
            UsePermissions = false;
        }
        
        return (permission != null);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
        	logInfo("Vault plugin not detected, disabling economy support.");
            return false;
        }

        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) 
            economy = economyProvider.getProvider();

        if(economy != null)
        	logInfo("Vault plugin found, economy support enabled.");

        return economy != null;
    }
    
    private boolean setupTag() {
    	if(getPluginManager().getPlugin("TagAPI") == null)
    		return false;
    	
    	getPluginManager().registerEvents(new TagAPIListener(this), this);
    	return true;
    }
    
    public Economy getEconomy() {
    	return this.economy;
    }
    
    public boolean usePermissions() {
    	return this.UsePermissions;
    }
    
    public boolean useTag() {
    	return this.useTag;
    }
    
    /** Returns the plugin manager, non statically. */
    public PluginManager getPluginManager() {
    	return this.pluginManager;
    }
    
    /** Returns the ArenaMaster instance */
    public ArenaMaster getArenaMaster() {
    	return this.arenaMaster;
    }
    
    /** Returns the ArenaUtils instance. */
    public ArenaUtils getArenaUtil() {
    	return this.aUtil;
    }
    
    /** Returns the ConfigTools instance. */
    public ConfigTools getConfigTools() {
    	return this.cTools;
    }
    
    /** Returns the InvManagement instance. */
    public InvManagement getInvManagement() {
    	return this.invMan;
    }
    
    /** Returns the MoneyUtils instance. */
    public MoneyUtils getMoneyUtil() {
    	return this.mUtil;
    }
    
    /** Returns the Permissions instance. */
    public Permissions getPermissions() {
    	return this.perm;
    }
    
    /** Returns the Util instance. */
    public Util getUtil() {
    	return this.util;
    }
    
    /** Returns the ArenaRestore instance */
    public ArenaRestore getArenaRestore(){
    	return this.arenaRestore;
    }
    
    /** Returns the MySQL Connector */
    public MysqlConnector getMysqlConnector() {
    	return this.mysqlConnector;
    }
    
    public int getLobbyActivity() {
    	return this.lobbyActivity;
    }
    
    public boolean isFirstTime() {
    	return this.firstTime;
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
    
    public LanguageOptions getLanguage() {
    	return this.lo;
    }
    
    /**
     * Returns the roles defined in the config.
     * 
     * @return The roles
     */
    public HashMap<String, List<Items>> getRoles() {
    	return this.roles;
    }
    
    /** The list of Healing Items stored by CTP.
     * 
     * @return The list of healing items.
     */
    public List<HealingItems> getHealingItems() {
    	return this.healingItems;
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