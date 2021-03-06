package me.dalton.capturethepoints.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.ConfigOptions;
import me.dalton.capturethepoints.KillStreakMessages;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigTools {
	private CaptureThePoints ctp;
	private ConfigOptions globalConfigOptions = null;
	private String global = "GlobalSettings.";
	private String countDown = global + "CountDowns.";
	private String mySql = global + "MySql.";
	private String gamemode = global + "GameMode.";
	private String pointCapture = gamemode + "PointCapture.";
	private String pointCaptureWithScore = gamemode + "PointCaptureWithScoreGeneration.";
	private String players = gamemode + "Players.";
	private String playerLives = players + "Lives.";
	private String playerTime = players + "Time.";
	
	public ConfigTools(CaptureThePoints ctp) {
		this.ctp = ctp;
	}
	
    /** Load from CaptureSettings.yml */
    public FileConfiguration load () { //Yaml Configuration
        return load(ctp.getGlobalConfig());
    }

    /** Load yml from specified file */
    public FileConfiguration load(File file) {
        try {
            FileConfiguration PluginPropConfig = YamlConfiguration.loadConfiguration(file);
            return PluginPropConfig;
        } catch (Exception localException) {} return null;
    }
	
    /** Get the configOptions from the config file. */
    public ConfigOptions getConfigOptions(File arenafile) {
    	if(globalConfigOptions == null) {
    		globalConfigOptions = ctp.getGlobalConfigOptions();
    	}
    	
        setConfigOptions(arenafile);
        FileConfiguration config = load(arenafile);
        
        config.addDefault("Version", 2);
        ConfigOptions co = new ConfigOptions();

        co.language = config.getString("Language", globalConfigOptions.language);
        
        //Game mode configuration
        co.pointsToWin = config.getInt(pointCapture + "PointsToWin", globalConfigOptions.pointsToWin);
        co.playTime = config.getInt(pointCapture + "PlayTime", globalConfigOptions.playTime);

        // Score mod
        co.useScoreGeneration = config.getBoolean(pointCaptureWithScore + "UseScoreGeneration", globalConfigOptions.useScoreGeneration);
        co.scoreMyltiplier = config.getInt(pointCaptureWithScore + "ScoreMultiplier", globalConfigOptions.scoreMyltiplier);
        if(co.scoreMyltiplier < 1) {
            co.scoreMyltiplier = 2;
            config.addDefault(pointCaptureWithScore + "ScoreMultiplier", co.scoreMyltiplier);
        }
        
        co.scoreToWin = config.getInt(pointCaptureWithScore + "ScoreToWin", globalConfigOptions.scoreToWin);
        co.onePointGeneratedScoreEvery30sec = config.getInt(pointCaptureWithScore + "OnePointGeneratedScoreEvery30sec", globalConfigOptions.onePointGeneratedScoreEvery30sec);
        co.scoreAnnounceTime = config.getInt(pointCaptureWithScore + "ScoreAnnounceTime", globalConfigOptions.scoreAnnounceTime);

        //Count down options
        co.useStartCountDown = config.getBoolean(countDown + "UseStartCountDown", globalConfigOptions.useStartCountDown);
        co.startCountDownTime = config.getInt(countDown + "StartCountDownTime", globalConfigOptions.startCountDownTime);
        co.useEndCountDown = config.getBoolean(countDown + "UseEndCountDown", globalConfigOptions.useEndCountDown);
        co.endCountDownTime = config.getInt(countDown + "EndCountDownTime", globalConfigOptions.endCountDownTime);
        
        // My sql
        co.mysqlAddress = config.getString(mySql + "Address", globalConfigOptions.mysqlAddress);
        co.mysqlDatabase = config.getString(mySql + "Database", globalConfigOptions.mysqlDatabase);
        co.mysqlPort = config.getInt(mySql + "Port", globalConfigOptions.mysqlPort);
        co.mysqlUser = config.getString(mySql + "User", globalConfigOptions.mysqlUser);
        co.mysqlPass = config.getString(mySql + "Pass", globalConfigOptions.mysqlPass);
        
        // Debug
        co.debugMessages = config.getBoolean(global + "displayDebugMessages", globalConfigOptions.debugMessages);

        // Global configuration
        // Kj -- documentation for the different options, including their default values, can be found under the ConfigOptions class.
        co.allowBlockBreak = config.getBoolean(global + "AllowBlockBreak", globalConfigOptions.allowBlockBreak);
        co.allowExplosionBlockBreak = config.getBoolean(global + "AllowExplosionBlockBreak", globalConfigOptions.allowExplosionBlockBreak);
        co.allowBlockPlacement = config.getBoolean(global + "AllowBlockPlacement", globalConfigOptions.allowBlockPlacement);
        co.allowBreakingOwnCapturedPointWool = config.getBoolean(global + "AllowBreakingOwnCapturedPointWool", globalConfigOptions.allowBreakingOwnCapturedPointWool);
        co.allowCommands = config.getBoolean(global + "AllowCommands", globalConfigOptions.allowCommands);
        co.allowDropItems = config.getBoolean(global + "AllowDropItems", globalConfigOptions.allowDropItems);
        co.allowLateJoin = config.getBoolean(global + "AllowLateJoin", globalConfigOptions.allowLateJoin);
        co.autoStart = config.getBoolean(global + "AutoStart", globalConfigOptions.autoStart);
        co.breakingBlocksDropsItems = config.getBoolean(global + "BreakingBlocksDropsItems", globalConfigOptions.breakingBlocksDropsItems);
        co.protectionDistance = config.getInt(global + "DamageImmunityNearSpawnDistance", globalConfigOptions.protectionDistance);
        co.disableKillMessages = config.getBoolean(global + "DisableKillMessages", globalConfigOptions.disableKillMessages);
        co.dropWoolOnDeath = config.getBoolean(global + "DropWoolOnDeath", globalConfigOptions.dropWoolOnDeath);
        co.enableHardArenaRestore = config.getBoolean(global + "EnableHardArenaRestore", globalConfigOptions.enableHardArenaRestore);
        co.economyMoneyCostForJoiningArena = config.getInt(global + "EconomyMoneyCostForJoiningArena", globalConfigOptions.economyMoneyCostForJoiningArena);
        co.exactTeamMemberCount = config.getBoolean(global + "ExactTeamMemberCount", globalConfigOptions.exactTeamMemberCount);
        co.balanceTeamsWhenPlayerLeaves = config.getInt(global + "BalanceTeamsWhenPlayerLeaves", globalConfigOptions.balanceTeamsWhenPlayerLeaves);
        co.giveNewRoleItemsOnRespawn = config.getBoolean(global + "GiveNewRoleItemsOnRespawn", globalConfigOptions.giveNewRoleItemsOnRespawn);
        co.givenWoolNumber = config.getInt(global + "GivenWoolNumber", 64) <= 0
                ? -1
                : config.getInt(global + "GivenWoolNumber", globalConfigOptions.givenWoolNumber);
        co.lobbyKickTime = config.getInt(global + "LobbyKickTime", globalConfigOptions.lobbyKickTime);
        co.maxPlayerHealth = config.getInt(global + "MaxPlayerHealth", globalConfigOptions.maxPlayerHealth);
        co.regainHealth = config.getBoolean(global + "HealthRegenFromHunger", globalConfigOptions.regainHealth);
        co.moneyAtTheLobby = config.getInt(global + "MoneyAtTheLobby", globalConfigOptions.moneyAtTheLobby);
        co.moneyEvery30Sec = config.getInt(global + "MoneyEvery30sec", globalConfigOptions.moneyEvery30Sec);
        co.moneyForKill = config.getInt(global + "MoneyForKill", globalConfigOptions.moneyForKill);
        co.moneyForPointCapture = config.getInt(global + "MoneyForPointCapture", globalConfigOptions.moneyForPointCapture);
        co.ringBlock = config.getInt(global + "RingBlock", globalConfigOptions.ringBlock);
        co.useSelectedArenaOnly = config.getBoolean(global + "UseSelectedArenaOnly", globalConfigOptions.useSelectedArenaOnly);
        co.eggsAreGrenades = config.getBoolean(global + "EggsGrenades.enabled", globalConfigOptions.eggsAreGrenades);
        co.grenadePower = config.getDouble(global + "EggsGrenades.power", globalConfigOptions.grenadePower);

        KillStreakMessages ksm = new KillStreakMessages();

        HashMap<Integer, String> hm = new HashMap<Integer, String>();
        for (int i = 0; i < 50; i++) {
            if (config.getString("StreakMessage." + i) != null) {
                hm.put(i, config.getString("StreakMessage." + i));
            } else if (!ksm.getMessage(i).isEmpty()) {
                hm.put(i, ksm.getMessage(i));
                config.addDefault("StreakMessage." + i, ksm.getMessage(i));
            }
        }
        co.killStreakMessages = new KillStreakMessages(hm);
        
        try {
            config.options().copyDefaults(true);
            config.save(arenafile);
        } catch (IOException ex) {
            ex.printStackTrace();
            ctp.logSevere("Unabled to save the global config file, please see the StackTrace above.");
        }

        return co;
    }
    
    //Sets config options if they does not exist
    public void setConfigOptions(File arenafile) {
        FileConfiguration config = load(arenafile);

        //Language
        if(!config.contains("Language"))
        	config.set("Language", "en");
        
        //Game mode configuration
        if(!config.contains(pointCapture + "PointsToWin"))
            config.set(pointCapture + "PointsToWin", globalConfigOptions.pointsToWin);
        if(!config.contains(pointCapture + "PlayTime"))
            config.set(pointCapture + "PlayTime", globalConfigOptions.playTime);

        // Score mod
        if(!config.contains(pointCaptureWithScore + "UseScoreGeneration"))
            config.set(pointCaptureWithScore + "UseScoreGeneration", globalConfigOptions.useScoreGeneration);
        if(!config.contains(pointCaptureWithScore + "ScoreMultiplier"))
            config.set(pointCaptureWithScore + "ScoreMultiplier", globalConfigOptions.scoreMyltiplier);
        if(!config.contains(pointCaptureWithScore + "ScoreToWin"))
            config.set(pointCaptureWithScore + "ScoreToWin", globalConfigOptions.scoreToWin);
        if(!config.contains(pointCaptureWithScore + "OnePointGeneratedScoreEvery30sec"))
            config.set(pointCaptureWithScore + "OnePointGeneratedScoreEvery30sec", globalConfigOptions.onePointGeneratedScoreEvery30sec);
        if(!config.contains(pointCaptureWithScore + "ScoreAnnounceTime"))
            config.set(pointCaptureWithScore + "ScoreAnnounceTime", globalConfigOptions.scoreAnnounceTime);

        // Count down configuration
        if(!config.contains(countDown + "UseStartCountDown"))
        	config.set(countDown + "UseStartCountDown", globalConfigOptions.useStartCountDown);
        if(!config.contains(countDown + "StartCountDownTime"))
        	config.set(countDown + "StartCountDownTime", globalConfigOptions.startCountDownTime);
        if(!config.contains(countDown + "UseEndCountDown"))
        	config.set(countDown + "UseEndCountDown", globalConfigOptions.useEndCountDown);
        if(!config.contains(countDown + "StartEndDownTime"))
        	config.set(countDown + "EndCountDownTime", globalConfigOptions.endCountDownTime);
        
        // My sql
        if(!config.contains(mySql + "Address"))
            config.set(mySql + "Address", globalConfigOptions.mysqlAddress);
        if(!config.contains(mySql + "Database"))
            config.set(mySql + "Database", globalConfigOptions.mysqlDatabase);
        if(!config.contains(mySql + "Port"))
            config.set(mySql + "Port", globalConfigOptions.mysqlPort);
        if(!config.contains(mySql + "User"))
            config.set(mySql + "User", globalConfigOptions.mysqlUser);
        if(!config.contains(mySql + "Pass"))
            config.set(mySql + "Pass", globalConfigOptions.mysqlPass);
        
        // Debug
        if(!config.contains(global + "displayDebugMessages"))
        	config.set(global + "displayDebugMessages", globalConfigOptions.debugMessages);

        // Global configuration
        if(!config.contains(global + "AllowBlockBreak"))
            config.set(global + "AllowBlockBreak", globalConfigOptions.allowBlockBreak);
        if(!config.contains(global + "AllowExplosionBlockBreak"))
        	config.set(global + "AllowExplosionBlockBreak", globalConfigOptions.allowExplosionBlockBreak);
        if(!config.contains(global + "AllowBlockPlacement"))
            config.set(global + "AllowBlockPlacement", globalConfigOptions.allowBlockPlacement);
        if(!config.contains(global + "AllowBreakingOwnCapturedPointWool"))
            config.set(global + "AllowBreakingOwnCapturedPointWool", globalConfigOptions.allowBreakingOwnCapturedPointWool);
        if(!config.contains(global + "AllowCommands"))
            config.set(global + "AllowCommands", globalConfigOptions.allowCommands);
        if(!config.contains(global + "AllowDropItems"))
            config.set(global + "AllowDropItems", globalConfigOptions.allowDropItems);
        if(!config.contains(global + "AllowLateJoin"))
            config.set(global + "AllowLateJoin", globalConfigOptions.allowLateJoin);
        if(!config.contains(global + "AutoStart"))
            config.set(global + "AutoStart", globalConfigOptions.autoStart);
        if(!config.contains(global + "BreakingBlocksDropsItems"))
            config.set(global + "BreakingBlocksDropsItems", globalConfigOptions.breakingBlocksDropsItems);
        if(!config.contains(global + "DamageImmunityNearSpawnDistance"))
            config.set(global + "DamageImmunityNearSpawnDistance", globalConfigOptions.protectionDistance);
        if(!config.contains(global + "DisableKillMessages"))
            config.set(global + "DisableKillMessages", globalConfigOptions.disableKillMessages);
        if(!config.contains(global + "DropWoolOnDeath"))
            config.set(global + "DropWoolOnDeath", globalConfigOptions.dropWoolOnDeath);
        if(!config.contains(global + "EnableHardArenaRestore"))
            config.set(global + "EnableHardArenaRestore", globalConfigOptions.enableHardArenaRestore);
        if(!config.contains(global + "EconomyMoneyCostForJoiningArena"))
            config.set(global + "EconomyMoneyCostForJoiningArena", globalConfigOptions.economyMoneyCostForJoiningArena);
        if(!config.contains(global + "ExactTeamMemberCount"))
            config.set(global + "ExactTeamMemberCount", globalConfigOptions.exactTeamMemberCount);
        if(!config.contains(global + "BalanceTeamsWhenPlayerLeaves"))
            config.set(global + "BalanceTeamsWhenPlayerLeaves", globalConfigOptions.balanceTeamsWhenPlayerLeaves);
        if(!config.contains(global + "GiveNewRoleItemsOnRespawn"))
            config.set(global + "GiveNewRoleItemsOnRespawn", globalConfigOptions.giveNewRoleItemsOnRespawn);
        if(!config.contains(global + "GivenWoolNumber"))
            config.set(global + "GivenWoolNumber", globalConfigOptions.givenWoolNumber);
        if(!config.contains(global + "LobbyKickTime"))
            config.set(global + "LobbyKickTime", globalConfigOptions.lobbyKickTime);
        if(!config.contains(global + "MaxPlayerHealth"))
            config.set(global + "MaxPlayerHealth", globalConfigOptions.maxPlayerHealth);
        if(!config.contains(global + "HealthRegenFromHunger"))
        	config.set(global + "HealthRegenFromHunger", globalConfigOptions.regainHealth);
        if(!config.contains(global + "MoneyAtTheLobby"))
            config.set(global + "MoneyAtTheLobby", globalConfigOptions.moneyAtTheLobby);
        if(!config.contains(global + "MoneyEvery30sec"))
            config.set(global + "MoneyEvery30sec", globalConfigOptions.moneyEvery30Sec);
        if(!config.contains(global + "MoneyForKill"))
            config.set(global + "MoneyForKill", globalConfigOptions.moneyForKill);
        if(!config.contains(global + "MoneyForPointCapture"))
            config.set(global + "MoneyForPointCapture", globalConfigOptions.moneyForPointCapture);
        if(!config.contains(global + "RingBlock"))
            config.set(global + "RingBlock", globalConfigOptions.ringBlock);
        if(!config.contains(global + "UseSelectedArenaOnly"))
            config.set(global + "UseSelectedArenaOnly", globalConfigOptions.useSelectedArenaOnly);
        if(!config.contains(global + "EggsGrenades.enabled"))
        	config.set(global + "EggsGrenades.enabled", globalConfigOptions.eggsAreGrenades);
        if(!config.contains(global + "EggsGrenades.power"))
        	config.set(global + "EggsGrenades.power", globalConfigOptions.grenadePower);
        
        try {
            config.options().copyDefaults(true);
            config.save(arenafile);
        } catch (IOException ex) {
            ex.printStackTrace();
            ctp.logSevere("Unable to save the global config, please see the StackTrace above.");
        }
    }
    
    //For arena config options!
    public ConfigOptions getArenaConfigOptions (File arenafile) {
    	if(globalConfigOptions == null)
    		globalConfigOptions = ctp.getGlobalConfigOptions();
    	
        setArenaConfigOptions(arenafile);
        FileConfiguration config = load(arenafile);

        ConfigOptions co = new ConfigOptions();

        //Game mode configuration
        co.pointsToWin = config.getInt(pointCapture + "PointsToWin", globalConfigOptions.pointsToWin);
        co.playTime = config.getInt(pointCapture + "PlayTime", globalConfigOptions.playTime);

        // Score mod
        co.useScoreGeneration = config.getBoolean(pointCaptureWithScore + "UseScoreGeneration", globalConfigOptions.useScoreGeneration);
        co.scoreMyltiplier = config.getInt(pointCaptureWithScore + "ScoreMultiplier", globalConfigOptions.scoreMyltiplier);
        if(co.scoreMyltiplier < 1) {
            co.scoreMyltiplier = 2;
            config.addDefault(pointCaptureWithScore + "ScoreMultiplier", co.scoreMyltiplier);
        }
        co.scoreToWin = config.getInt(pointCaptureWithScore + "ScoreToWin", globalConfigOptions.scoreToWin);
        co.onePointGeneratedScoreEvery30sec = config.getInt(pointCaptureWithScore + "OnePointGeneratedScoreEvery30sec", globalConfigOptions.onePointGeneratedScoreEvery30sec);
        co.scoreAnnounceTime = config.getInt(pointCaptureWithScore + "ScoreAnnounceTime", globalConfigOptions.scoreAnnounceTime);

        //Player lives
        co.usePlayerLives = config.getBoolean(playerLives + "Enabled", globalConfigOptions.usePlayerLives);
        co.playerLives = config.getInt(playerLives + "Amount", globalConfigOptions.playerLives);
        
        //Player time
        co.usePlayerTime = config.getBoolean(playerTime + "Enabled", globalConfigOptions.usePlayerTime);
        co.playerTime = config.getString(playerTime + "Value", globalConfigOptions.playerTime);
        
        //Count down options
        co.useStartCountDown = config.getBoolean(countDown + "UseStartCountDown", globalConfigOptions.useStartCountDown);
        co.startCountDownTime = config.getInt(countDown + "StartCountDownTime", globalConfigOptions.startCountDownTime);
        co.useEndCountDown = config.getBoolean(countDown + "UseEndCountDown", globalConfigOptions.useEndCountDown);
        co.endCountDownTime = config.getInt(countDown + "EndCountDownTime", globalConfigOptions.endCountDownTime);

        // Global configuration
        // Kj -- documentation for the different options, including their default values, can be found under the ConfigOptions class.
        co.allowBlockBreak = config.getBoolean(global + "AllowBlockBreak", globalConfigOptions.allowBlockBreak);
        co.allowExplosionBlockBreak = config.getBoolean(global + "AllowExplosionBlockBreak", globalConfigOptions.allowExplosionBlockBreak);
        co.allowBlockPlacement = config.getBoolean(global + "AllowBlockPlacement", globalConfigOptions.allowBlockPlacement);
        co.allowBreakingOwnCapturedPointWool = config.getBoolean(global + "AllowBreakingOwnCapturedPointWool", globalConfigOptions.allowBreakingOwnCapturedPointWool);
        co.allowCommands = globalConfigOptions.allowCommands;
        co.allowDropItems = config.getBoolean(global + "AllowDropItems", globalConfigOptions.allowDropItems);
        co.allowLateJoin = globalConfigOptions.allowLateJoin;
        co.autoStart = globalConfigOptions.autoStart;
        co.breakingBlocksDropsItems = config.getBoolean(global + "BreakingBlocksDropsItems", globalConfigOptions.breakingBlocksDropsItems);
        co.protectionDistance = config.getInt(global + "DamageImmunityNearSpawnDistance", globalConfigOptions.protectionDistance);
        co.dropWoolOnDeath = config.getBoolean(global + "DropWoolOnDeath", globalConfigOptions.dropWoolOnDeath);
        co.exactTeamMemberCount = config.getBoolean(global + "ExactTeamMemberCount", globalConfigOptions.exactTeamMemberCount);
        co.economyMoneyCostForJoiningArena = config.getInt(global + "EconomyMoneyCostForJoiningArena", globalConfigOptions.economyMoneyCostForJoiningArena);
        co.balanceTeamsWhenPlayerLeaves = config.getInt(global + "BalanceTeamsWhenPlayerLeaves", globalConfigOptions.balanceTeamsWhenPlayerLeaves);
        co.giveNewRoleItemsOnRespawn = config.getBoolean(global + "GiveNewRoleItemsOnRespawn", globalConfigOptions.giveNewRoleItemsOnRespawn);
        co.keepBoughtItemsOnRespawn = config.getBoolean(global + "KeepBoughtItemsOnRespawn", globalConfigOptions.keepBoughtItemsOnRespawn);
        co.givenWoolNumber = config.getInt(global + "GivenWoolNumber", 64) <= 0
                ? -1
                : config.getInt(global + "GivenWoolNumber", globalConfigOptions.givenWoolNumber);
        co.lobbyKickTime = globalConfigOptions.lobbyKickTime;
        co.maxPlayerHealth = config.getInt(global + "MaxPlayerHealth", globalConfigOptions.maxPlayerHealth);
        co.regainHealth = config.getBoolean(global + "HealthRegenFromHunger", globalConfigOptions.regainHealth);
        co.moneyAtTheLobby = config.getInt(global + "MoneyAtTheLobby", globalConfigOptions.moneyAtTheLobby);
        co.moneyEvery30Sec = config.getInt(global + "MoneyEvery30sec", globalConfigOptions.moneyEvery30Sec);
        co.moneyForKill = config.getInt(global + "MoneyForKill", globalConfigOptions.moneyForKill);
        co.moneyForPointCapture = config.getInt(global + "MoneyForPointCapture", globalConfigOptions.moneyForPointCapture);
        co.ringBlock = globalConfigOptions.ringBlock;
        co.useSelectedArenaOnly = globalConfigOptions.useSelectedArenaOnly;
        co.eggsAreGrenades = config.getBoolean(global + "EggsGrenades.enabled", globalConfigOptions.eggsAreGrenades);
        co.grenadePower = config.getDouble(global + "EggsGrenades.power", globalConfigOptions.grenadePower);
        
        KillStreakMessages ksm = new KillStreakMessages();

        HashMap<Integer, String> hm = new HashMap<Integer, String>();
        for (int i = 0; i < 50; i++) {
            if (config.getString("StreakMessage." + i) != null) {
                hm.put(i, config.getString("StreakMessage." + i));
            } else if (!ksm.getMessage(i).isEmpty()) {
                hm.put(i, ksm.getMessage(i));
                config.addDefault("StreakMessage." + i, ksm.getMessage(i));
            }
        }
        co.killStreakMessages = new KillStreakMessages(hm);
        
        try {
            config.options().copyDefaults(true);
            config.save(arenafile);
        } catch (IOException ex) {
            ex.printStackTrace();
            ctp.logSevere("Unable to save the config file for an arena, see the StackTrace above.");
        }

        return co;
    }

    public void setArenaConfigOptions(File arenafile) {
    	if(ctp.getGlobalConfigOptions().debugMessages) ctp.getLogger().info("Update the config file: " + arenafile.getAbsolutePath());
        FileConfiguration config = load(arenafile);

        //Game mode configuration
        if(!config.contains(pointCapture + "PointsToWin"))
            config.set(pointCapture + "PointsToWin", globalConfigOptions.pointsToWin);
        if(!config.contains(pointCapture + "PlayTime"))
            config.set(pointCapture + "PlayTime", globalConfigOptions.playTime);
        
        // Max and min players
        if(!config.contains(players + "MaximumPlayers"))
        	config.set(players + "MaximumPlayers", 9999);
        if(!config.contains(players + "MinimumPlayers"))
        	config.set(players + "MinimumPlayers", 4);
        
        //Player lives
        if(!config.contains(playerLives + "Enabled"))
        	config.set(playerLives + "Enabled", globalConfigOptions.usePlayerLives);
        if(!config.contains(playerLives + "Amount"))
        	config.set(playerLives + "Amount", globalConfigOptions.playerLives);
        
        //Player time
        if(!config.contains(playerTime + "Enabled"))
        	config.set(playerTime + "Enabled", globalConfigOptions.usePlayerTime);
        if(!config.contains(playerTime + "Value"))
        	config.set(playerTime + "Value", globalConfigOptions.playerTime);

        // Score mod
        if(!config.contains(pointCaptureWithScore + "UseScoreGeneration"))
            config.set(pointCaptureWithScore + "UseScoreGeneration", globalConfigOptions.useScoreGeneration);
        if(!config.contains(pointCaptureWithScore + "ScoreMultiplier"))
            config.set(pointCaptureWithScore + "ScoreMultiplier", globalConfigOptions.scoreMyltiplier);
        if(!config.contains(pointCaptureWithScore + "ScoreToWin"))
            config.set(pointCaptureWithScore + "ScoreToWin", globalConfigOptions.scoreToWin);
        if(!config.contains(pointCaptureWithScore + "OnePointGeneratedScoreEvery30sec"))
            config.set(pointCaptureWithScore + "OnePointGeneratedScoreEvery30sec", globalConfigOptions.onePointGeneratedScoreEvery30sec);
        if(!config.contains(pointCaptureWithScore + "ScoreAnnounceTime"))
            config.set(pointCaptureWithScore + "ScoreAnnounceTime", globalConfigOptions.scoreAnnounceTime);

        // Count down configuration
        if(!config.contains(countDown + "UseStartCountDown"))
        	config.set(countDown + "UseStartCountDown", globalConfigOptions.useStartCountDown);
        if(!config.contains(countDown + "StartCountDownTime"))
        	config.set(countDown + "StartCountDownTime", globalConfigOptions.startCountDownTime);
        if(!config.contains(countDown + "UseEndCountDown"))
        	config.set(countDown + "UseEndCountDown", globalConfigOptions.useEndCountDown);
        if(!config.contains(countDown + "StartEndDownTime"))
        	config.set(countDown + "EndCountDownTime", globalConfigOptions.endCountDownTime);
        
        // Global configuration
        if(!config.contains(global + "AllowBlockBreak"))
            config.set(global + "AllowBlockBreak", globalConfigOptions.allowBlockBreak);
        if(!config.contains(global + "AllowExplosionBlockBreak"))
            config.set(global + "AllowExplosionBlockBreak", globalConfigOptions.allowExplosionBlockBreak);
        if(!config.contains(global + "AllowBlockPlacement"))
            config.set(global + "AllowBlockPlacement", globalConfigOptions.allowBlockPlacement);
        if(!config.contains(global + "AllowBreakingOwnCapturedPointWool"))
            config.set(global + "AllowBreakingOwnCapturedPointWool", globalConfigOptions.allowBreakingOwnCapturedPointWool);
        if(!config.contains(global + "AllowDropItems"))
            config.set(global + "AllowDropItems", globalConfigOptions.allowDropItems);
        if(!config.contains(global + "BreakingBlocksDropsItems"))
            config.set(global + "BreakingBlocksDropsItems", globalConfigOptions.breakingBlocksDropsItems);
        if(!config.contains(global + "DamageImmunityNearSpawnDistance"))
            config.set(global + "DamageImmunityNearSpawnDistance", globalConfigOptions.protectionDistance);
        if(!config.contains(global + "DropWoolOnDeath"))
            config.set(global + "DropWoolOnDeath", globalConfigOptions.dropWoolOnDeath);
        if(!config.contains(global + "ExactTeamMemberCount"))
            config.set(global + "ExactTeamMemberCount", globalConfigOptions.exactTeamMemberCount);
        if(!config.contains(global + "EconomyMoneyCostForJoiningArena"))
            config.set(global + "EconomyMoneyCostForJoiningArena", globalConfigOptions.economyMoneyCostForJoiningArena);
        if(!config.contains(global + "BalanceTeamsWhenPlayerLeaves"))
            config.set(global + "BalanceTeamsWhenPlayerLeaves", globalConfigOptions.balanceTeamsWhenPlayerLeaves);
        if(!config.contains(global + "GiveNewRoleItemsOnRespawn"))
            config.set(global + "GiveNewRoleItemsOnRespawn", globalConfigOptions.giveNewRoleItemsOnRespawn);
        if(!config.contains(global + "KeepBoughtItemsOnRespawn"))
        	config.set(global + "KeepBoughtItemsOnRespawn", globalConfigOptions.keepBoughtItemsOnRespawn);
        if(!config.contains(global + "GivenWoolNumber"))
            config.set(global + "GivenWoolNumber", globalConfigOptions.givenWoolNumber);
        if(!config.contains(global + "MaxPlayerHealth"))
            config.set(global + "MaxPlayerHealth", globalConfigOptions.maxPlayerHealth);
        if(!config.contains(global + "HealthRegenFromHunger"))
        	config.set(global + "HealthRegenFromHunger", globalConfigOptions.regainHealth);
        if(!config.contains(global + "MoneyAtTheLobby"))
            config.set(global + "MoneyAtTheLobby", globalConfigOptions.moneyAtTheLobby);
        if(!config.contains(global + "MoneyEvery30sec"))
            config.set(global + "MoneyEvery30sec", globalConfigOptions.moneyEvery30Sec);
        if(!config.contains(global + "MoneyForKill"))
            config.set(global + "MoneyForKill", globalConfigOptions.moneyForKill);
        if(!config.contains(global + "MoneyForPointCapture"))
            config.set(global + "MoneyForPointCapture", globalConfigOptions.moneyForPointCapture);
        if(!config.contains(global + "EggsGrenades.enabled"))
        	config.set(global + "EggsGrenades.enabled", globalConfigOptions.eggsAreGrenades);
        if(!config.contains(global + "EggsGrenades.power"))
        	config.set(global + "EggsGrenades.power", globalConfigOptions.grenadePower);
        
        try {
            config.options().copyDefaults(true);
            config.save(arenafile);
        } catch (IOException ex) {
            ex.printStackTrace();
            ctp.logSevere("Error saving the config file for an arena config file.");
        }
    }
}
