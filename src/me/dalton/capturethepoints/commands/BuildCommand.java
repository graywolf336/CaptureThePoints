package me.dalton.capturethepoints.commands;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.beans.ArenaBoundaries;
import me.dalton.capturethepoints.beans.Arena;
import me.dalton.capturethepoints.beans.Lobby;
import me.dalton.capturethepoints.beans.Points;
import me.dalton.capturethepoints.beans.Spawn;
import me.dalton.capturethepoints.beans.Stands;
import me.dalton.capturethepoints.beans.Team;
import me.dalton.capturethepoints.enums.Status;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class BuildCommand extends CTPCommand {

    // Kj -- This could be broken down further, perhaps into a new package completely.
    public BuildCommand(CaptureThePoints instance) {
        super.ctp = instance;
        super.aliases.add("build");
        super.aliases.add("create");
        super.aliases.add("make");
        super.aliases.add("b");
        super.notOpCommand = false;
        super.requiredPermissions = new String[]{"ctp.*", "ctp.admin",
            "ctp.admin.setpoint", "ctp.admin.removepoint", "ctp.admin.create", "ctp.admin.delete", "ctp.admin.editarena",
            "ctp.admin.setarena", "ctp.admin.setlobby", "ctp.admin.arenalist", "ctp.admin.pointlist", "ctp.admin.setboundary",
            "ctp.admin.maximumplayers", "ctp.admin.minimumplayers" , "ctp.admin.save", "ctp.admin.restore"};
        super.senderMustBePlayer = true;
        super.minParameters = 2;
        super.maxParameters = 99;    // Lol cant make in the other way
        super.usageTemplate = "/ctp build [help] [pagenumber]";
    }

    @Override
    public void perform() {
        int size = parameters.size();
        // ctp = parameters.get(0)
        // build = parameters.get(1)
        String arg = size > 2 ? parameters.get(2) : "help"; // Kj -- grab the arguments with null -> empty checking. If only /ctp build, assume help.
        String arg2 = size > 3 ? parameters.get(3) : "";
        String arg3 = size > 4 ? parameters.get(4) : "";
        if (arg.equals("1")) { // ctp build 1
            arg = "help";
            arg2 = "1";
        } else if(arg.equals("2")) {  // ctp build 2
            arg = "help";
            arg2 = "2";
        }else if(arg.equals("3")){
            arg = "help";
            arg2 = "3";
        }

        if (arg.equalsIgnoreCase("help")){
            String pagenumber = arg2;
            if (pagenumber.isEmpty() || pagenumber.equals("1")) {
                sendMessage(ChatColor.RED + "CTP Build Commands: " + ChatColor.GOLD + " Page 1/3");
                sendMessage(ChatColor.DARK_GREEN + "/ctp b help [pagenumber] " + ChatColor.WHITE + "- view this menu.");

                if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.arenalist"})) {
                    sendMessage(ChatColor.GREEN + "/ctp b arenalist " + ChatColor.WHITE + "- show list of existing arenas");
                }
                if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.create"})) {
                    sendMessage(ChatColor.GREEN + "/ctp b create <Arena name> " + ChatColor.WHITE + "- create an arena");
                }
                if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.delete"})) {
                    sendMessage(ChatColor.GREEN + "/ctp b delete <Arena name> " + ChatColor.WHITE + "- delete an existing arena");
                }
                if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.maximumplayers"})) {
                    sendMessage(ChatColor.GREEN + "/ctp b maximumplayers <number> " + ChatColor.WHITE + "- set maximum players of the arena");
                }
                if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.minimumplayers"})) {
                    sendMessage(ChatColor.GREEN + "/ctp b minimumplayers <number> " + ChatColor.WHITE + "- set minimum players of the arena");
                }
                if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.pointlist"})) {
                    sendMessage(ChatColor.GREEN + "/ctp b pointlist " + ChatColor.WHITE + "- shows selected arena capture points list");
                }
                if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.removepoint"})) {
                    sendMessage(ChatColor.GREEN + "/ctp b removepoint <Point name> " + ChatColor.WHITE + "- removes an existing capture point");
                }
            } else if (pagenumber.equals("2")) {
                sendMessage(ChatColor.RED + "CTP Build Commands: " + ChatColor.GOLD + " Page 2/3");
                if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin.removespawn", "ctp.admin"})) {
                    sendMessage(ChatColor.GREEN + "/ctp b removespawn <Team color> " + ChatColor.WHITE + "- removes spawn point of selected color");
                }
                if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.editarena"})) {
                	ctp.sendMessage(player, ChatColor.GREEN + "/ctp b editarena <Arena name> " + ChatColor.WHITE + "- selects arena for editing");
                }
                if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.setarena"})) {
                	ctp.sendMessage(player, ChatColor.GREEN + "/ctp b setarena <Arena name> " + ChatColor.WHITE + "- sets main arena for playing");
                }
                if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.setboundary"})) {
                	ctp.sendMessage(player, ChatColor.GREEN + "/ctp b setboundary <1 | 2> " + ChatColor.WHITE + "- sets boundary (1 or 2) of the arena");
                }
                if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.setlobby"})) {
                	ctp.sendMessage(player, ChatColor.GREEN + "/ctp b setlobby " + ChatColor.WHITE + "- sets arena lobby");
                }
                if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.setpoint"})) {
                	ctp.sendMessage(player, ChatColor.GREEN + "/ctp b setpoint <Point name> <vert | hor> [teams which can't capture]" + ChatColor.WHITE + "- creates new capture point");
                }
                if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin.setspawn", "ctp.admin"})) {
                	ctp.sendMessage(player, ChatColor.GREEN + "/ctp b setspawn <Team color> " + ChatColor.WHITE + "- sets the place people are teleported to when they die or when they join the game");
                }
            } else if(pagenumber.equals("3")) {
            	ctp.sendMessage(player, ChatColor.RED + "CTP Build Commands: " + ChatColor.GOLD + " Page 3/3");
                if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin.save", "ctp.admin"})) {
                	ctp.sendMessage(player, ChatColor.GREEN + "/ctp b save " + ChatColor.WHITE + "- saves selected for editing arena data to mySQL database");
                }
                if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin.restore", "ctp.admin"})) {
                	ctp.sendMessage(player, ChatColor.GREEN + "/ctp b restore " + ChatColor.WHITE + "- restores arena from mySQL database");
                }
                if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin.findchests", "ctp.admin"})) {
                	ctp.sendMessage(player, ChatColor.GREEN + "/ctp b findchests <arena name>" + ChatColor.WHITE + "- shows all chests in arena");
                }
                if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin.check", "ctp.admin"})) {
                	ctp.sendMessage(player, ChatColor.GREEN + "/ctp b check" + ChatColor.WHITE + "- checks the status of the editing arena");
                }
                if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.setstands", "ctp.admin.stands"})) {
                	ctp.sendMessage(player, ChatColor.GREEN + "/ctp b setstands " + ChatColor.WHITE + "- sets arena stands");
                }
                if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.enable"})) {
                	ctp.sendMessage(player, ChatColor.GREEN + "/ctp build enable <Arena name> <true/false>" + ChatColor.WHITE + "- sets arena stands");
                }
            }
            return;
        }
        
        if (arg.equalsIgnoreCase("arenalist")) {
            if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.arenalist"})) {
                String arenas = "";
                boolean firstTime = true;
                for (Arena arena : ctp.getArenaMaster().getArenas()) {
                    if (firstTime) {
                        arenas = arena.getName();
                        firstTime = false;
                    } else {
                        arenas = arena.getName() + ", " + arenas;
                    }
                }
                
                if(arenas.equalsIgnoreCase("")) {
                	sendMessage("There are currently no arenas.");
                	return;
                }else {
                	sendMessage("Arena list:");
                	sendMessage("  " + arenas);
                	return;
                } 
            }
            sendMessage(ctp.getLanguage().NO_PERMISSION);
            return;
        }
        
        //sets arena for editing/creating
        if (arg.equalsIgnoreCase("editarena")) {
            if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.editarena"})) {
                if (parameters.size() < 4) {
                    sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GREEN + "/ctp build editarena <Arena name>");
                    return;
                }
                
                arg2 = arg2.toLowerCase();
                if (ctp.getArenaMaster().getArena(arg2) == null) {
                    sendMessage(ChatColor.RED + "This arena does not exist! -----> " + ChatColor.GREEN + arg2);
                    return;
                }
                
                ctp.getArenaMaster().setEditingArena(arg2);
                if(!ctp.getArenaMaster().getEditingArena().getName().equalsIgnoreCase(arg2)) {
                	sendMessage(ChatColor.RED + "We have failed you, please try setting the editing arena again.");
                	return;
                }

                ctp.getArenaMaster().getEditingArena().setStatus(Status.CREATING);
                sendMessage(ChatColor.WHITE + "Arena selected for editing: " + ChatColor.GREEN + arg2);

                return;
            }
            sendMessage(ctp.getLanguage().NO_PERMISSION);
            return;
        }
        
        //enables/disables the arena
        if (arg.equalsIgnoreCase("enable")) {
            if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.editarena"})) {
                if (parameters.size() < 4) {
                    sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GREEN + "/ctp build enable <Arena name> <true/false>");
                    return;
                }
                
                arg2 = arg2.toLowerCase();
                if (ctp.getArenaMaster().getArena(arg2) == null) {
                    sendMessage(ChatColor.RED + "This arena does not exist! -----> " + ChatColor.GREEN + arg2);
                    return;
                }
                
                if(arg3.equalsIgnoreCase("true")) {
                	ctp.getArenaMaster().getArena(arg2).setStatus(Status.JOINABLE);
                	sendMessage(ChatColor.WHITE + "You have enabled the arena called " + arg2 + ".");
                	return;
                }else if(arg3.equalsIgnoreCase("false")) {
                	ctp.getArenaMaster().getArena(arg2).setStatus(Status.DISABLED);
                	sendMessage(ChatColor.WHITE + "You have disabled the arena called " + arg2 + ".");
                	return;
                }else {
                	sendMessage(ChatColor.RED + "Please state enable/disable.");
                	return;
                }
            }
            sendMessage(ctp.getLanguage().NO_PERMISSION);
            return;
        }
        
        if (arg.equalsIgnoreCase("setarena")) {
            if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.setarena"})) {
                if (parameters.size() < 4) {
                    sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GREEN + "/ctp build setarena <Arena name>");
                    return;
                }
                
                if(ctp.getArenaMaster().getArenas().size() == 0) {
                	sendMessage(ChatColor.RED + "There are no arenas currently, please make one.");
                	return;
                }
                
                arg2 = arg2.toLowerCase();
                if (!ctp.getArenaMaster().isArena(arg2)) {
                    sendMessage(ChatColor.RED + "This arena does not exist! -----> " + ChatColor.GREEN + arg2);
                    return;
                }

                Arena arena = ctp.getArenaMaster().getArena(arg2);
                boolean canLoad = true;
                if (arena.getCapturePoints().size() < 1) {
                    sendMessage(ChatColor.RED + "Please add at least one capture point");
                    canLoad = false;
                }
                if (arena.getTeamSpawns().size() < 2) {
                    sendMessage(ChatColor.RED + "Please add at least two teams' spawn points");
                    canLoad = false;
                }
                if (arena.getLobby() == null) {
                    sendMessage(ChatColor.RED + "Please create an arena lobby");
                    canLoad = false;
                }
                if (arena.getFirstCorner() == null || arena.getSecondCorner() == null) {
                    sendMessage(ChatColor.RED + "Please set arena boundaries");
                    canLoad = false;
                }

                String mainArenaCheckError = ctp.getArenaMaster().checkArena(arena, player); //Check arena, if there is an error, an error message is returned.
                if (!mainArenaCheckError.isEmpty() && canLoad) {
                    sendMessage(mainArenaCheckError);
                    return;
                }

                if (canLoad) {
                    FileConfiguration config = ctp.getConfigTools().load();
                    config.addDefault("Arena", arg2);
                    try {
                        config.options().copyDefaults(true);
                        config.save(ctp.getGlobalConfig());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        ctp.logSevere("There was an error while saving the global config when getting the selected arena.");
                    }
                    
                    ctp.getArenaMaster().setSelectedArena(arena);
                    sendMessage(ChatColor.WHITE + "Arena selected for playing: " + ChatColor.GREEN + arg2);
                } else {
                    sendMessage(ChatColor.GREEN + "If you wanted to edit this arena instead, use " + ChatColor.WHITE + "/ctp b editarena " + arg2);
                }

                return;
            }
            sendMessage(ctp.getLanguage().NO_PERMISSION);
            return;
        }
        
        if (arg.equalsIgnoreCase("create")) {
            if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.create"})) {
                if (parameters.size() < 4) {
                    sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GREEN + "/ctp build create <Arena name>");
                    return;
                }
                
                arg2 = arg2.toLowerCase();
                if (ctp.getArenaMaster().isArena(arg2)) {
                    sendMessage(ChatColor.RED + "This arena already exists! -----> " + ChatColor.GREEN + arg2); // Kj -- typo
                    return;
                }
                
                ctp.getArenaMaster().addNewArena(new Arena(ctp, arg2, Status.CREATING, ctp.getGlobalConfigOptions().startCountDownTime, ctp.getGlobalConfigOptions().endCountDownTime, ctp.getGlobalConfigOptions().playTime)); //Create the new arena!
                ctp.getArenaMaster().setEditingArena(arg2); //After creating, set the editing arena to the one we created
                
                FileConfiguration config = ctp.getConfigTools().load();
                
                //Loads the default configuration options on creation of it, this way the 'co' isn't null
                File arenaFile = new File(ctp.getMainDirectory() + File.separator + "Arenas" + File.separator + ctp.getArenaMaster().getEditingArena().getName() + ".yml");
                ctp.getArenaMaster().getEditingArena().setConfigOptions(ctp.getConfigTools().getArenaConfigOptions(arenaFile));
                
                if(!config.contains("Arena")) {
                	config.addDefault("Arena", ctp.getArenaMaster().getEditingArena().getName());
                	try {
                        config.options().copyDefaults(true);
                        config.save(ctp.getGlobalConfig());
                    } catch (IOException ex) {
                    	ex.printStackTrace();
                    	ctp.logSevere("Unable to save the main config file, see the StackTrace above for more information.");
                    }
                	
                	ctp.getArenaMaster().setSelectedArena(ctp.getArenaMaster().getEditingArena());
                }
                
                sendMessage("You are creating an arena called: " + ChatColor.GREEN + arg2);
                sendMessage("Now please create the first corner for the arena's boundary. Use: " + ChatColor.AQUA + "/ctp b setboundary 1");
                return;
            }
            
            sendMessage(ctp.getLanguage().NO_PERMISSION);
            return;
        }
        
        if (arg.equalsIgnoreCase("delete")) {
            if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.delete"})) {
                if (parameters.size() < 4) {
                    sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GREEN + "/ctp build delete <Arena name>");
                    return;
                }
                
                arg2 = arg2.toLowerCase();
                if (!ctp.getArenaMaster().isArena(arg2)) {
                    sendMessage(ChatColor.RED + "This arena does not exist! -----> " + ChatColor.GREEN + arg2);
                    return;
                }
                
                if (ctp.getArenaMaster().getArena(arg2).getStatus().isRunning()) {
                    sendMessage(ChatColor.RED + "Cannot delete arena while game is running in it!");
                    return;
                }
                File arenaFile = new File("plugins/CaptureThePoints" + File.separator + "Arenas" + File.separator + arg2 + ".yml");

                //Remove blocks
                FileConfiguration arenaConf = YamlConfiguration.loadConfiguration(arenaFile);
                FileConfiguration config = ctp.getConfigTools().load();

                if (arenaConf.getString("Points") != null) {
                    for (String str : arenaConf.getConfigurationSection("Points").getKeys(false)) {
                        str = "Points." + str;
                        int start_x = arenaConf.getInt(str + ".X", 0);
                        int start_y = arenaConf.getInt(str + ".Y", 0);
                        int start_z = arenaConf.getInt(str + ".Z", 0);

                        if (arenaConf.getString(str + ".Dir") == null) {
                            for (int x = start_x + 2; x >= start_x - 1; x--) {
                                for (int y = start_y - 1; y <= start_y; y++) {
                                    for (int z = start_z - 1; z <= start_z + 2; z++) {
                                        if (player.getWorld().getBlockAt(x, y, z).getTypeId() == ctp.getGlobalConfigOptions().ringBlock) {
                                            player.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
                                        }
                                    }
                                }
                            }
                        } else {
                            String direction = arenaConf.getString(str + ".Dir");
                            ctp.getUtil().removeVertPoint(player, direction, start_x, start_y, start_z, ctp.getGlobalConfigOptions().ringBlock);
                        }
                    }
                }

                //Delete mysql data
                ctp.getArenaRestore().arenaToDelete = arg2;
                if(ctp.getGlobalConfigOptions().enableHardArenaRestore) {
                    ctp.getServer().getScheduler().runTaskLaterAsynchronously(ctp, new Runnable() {
                        public void run () {
                            ctp.getMysqlConnector().connectToMySql();
                            ctp.getArenaRestore().deleteArenaData(ctp.getArenaRestore().arenaToDelete);
                            ctp.getArenaRestore().arenaToDelete = null;
                        }
                    }, 5L);
                }

                if (arg2.equals(ctp.getArenaMaster().getSelectedArena())) {
                    ctp.getArenaMaster().getArenasBoundaries().remove(ctp.getArenaMaster().getSelectedArena().getName());
                    ctp.getArenaMaster().setEditingArena(null);

                    config.set("Arena", null);
                    try {
                        config.options().copyDefaults(true);
                        config.save(ctp.getGlobalConfig());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        ctp.logSevere("Unable to save the default config while setting the default arena back to nothing.");
                    }
                }
                
                arenaFile.delete();
                
                //remove it and then verify it has been removed
                ctp.getArenaMaster().removeArena(ctp.getArenaMaster().getArena(arg2));
                if(ctp.getArenaMaster().getArena(arg2) != null) {
                	sendMessage(ChatColor.RED + "I couldn't seem to delete the reason for some reason, sorry.");
                	return;
                }
                
                if (arg2.equals(ctp.getArenaMaster().getEditingArena().getName())) {
                    ctp.getArenaMaster().setEditingArena(null);
                }
                sendMessage("You deleted arena: " + ChatColor.GREEN + arg2);
                return;
            }
            
            sendMessage(ctp.getLanguage().NO_PERMISSION);
            return;
        }
        
        //if editing arena is null and they're not creating a new one, return with an error first.
        if (ctp.getArenaMaster().getEditingArena() == null) {
        	sendMessage(ChatColor.RED + "Please select which arena you want to edit first or create an arena to edit first.");
            return;
        }

        if (arg.equalsIgnoreCase("setspawn")) {
            if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin.setspawn", "ctp.admin"})) {
                if (parameters.size() < 4) {
                	ctp.sendMessage(player, ChatColor.WHITE + "Usage: " + ChatColor.GREEN + "/ctp build setspawn <Team color> ");
                    return;
                }
                
                Location loc = player.getLocation();

                File arenaFile = new File(ctp.getMainDirectory() + File.separator + "Arenas" + File.separator + ctp.getArenaMaster().getEditingArena().getName() + ".yml");
                FileConfiguration arenaConf = YamlConfiguration.loadConfiguration(arenaFile);

                if ((arenaConf.getString("World") != null) && (!arenaConf.getString("World").equals(loc.getWorld().getName()))) {
                	ctp.sendMessage(player, ChatColor.RED + "Please build all arena team spawns in the same world ---->" + ChatColor.GREEN + arenaConf.getString("World"));
                    return;
                }

                if ((arg2.equalsIgnoreCase("white")) || (arg2.equalsIgnoreCase("lightgray")) || (arg2.equalsIgnoreCase("gray")) || (arg2.equalsIgnoreCase("black"))
                		|| (arg2.equalsIgnoreCase("red")) || (arg2.equalsIgnoreCase("orange")) || (arg2.equalsIgnoreCase("yellow"))
                        || (arg2.equalsIgnoreCase("lime")) || (arg2.equalsIgnoreCase("green")) || (arg2.equalsIgnoreCase("blue"))
                        || (arg2.equalsIgnoreCase("cyan")) || (arg2.equalsIgnoreCase("lightblue")) || (arg2.equalsIgnoreCase("purple"))
                        || (arg2.equalsIgnoreCase("pink")) || (arg2.equalsIgnoreCase("magenta")) || (arg2.equalsIgnoreCase("brown"))) {
                    Spawn spawn = new Spawn();
                    spawn.setName(arg2.toLowerCase());
                    spawn.setX(Double.valueOf(loc.getX()).doubleValue());
                    spawn.setY(Double.valueOf(loc.getY()).doubleValue());
                    spawn.setZ(Double.valueOf(loc.getZ()).doubleValue());
                    spawn.setDir(loc.getYaw());

                    String aWorld = arenaConf.getString("World");
                    if (aWorld == null) {
                        arenaConf.addDefault("World", loc.getWorld().getName());
                        ctp.getArenaMaster().getEditingArena().setWorld(loc.getWorld().getName());
                    } else if (!aWorld.equals(loc.getWorld().getName())) {
                    	ctp.sendMessage(player, ChatColor.RED + "Please build arena lobby in same world as its spawns and capture points!");
                        return;
                    }
                    
                    arenaConf.addDefault("Team-Spawns." + arg2 + ".X", Double.valueOf(loc.getX()));
                    arenaConf.addDefault("Team-Spawns." + arg2 + ".Y", Double.valueOf(loc.getY()));
                    arenaConf.addDefault("Team-Spawns." + arg2 + ".Z", Double.valueOf(loc.getZ()));
                    arenaConf.addDefault("Team-Spawns." + arg2 + ".Dir", Double.valueOf(spawn.getDir()));
                    
                    try {
                        arenaConf.options().copyDefaults(true);
                        arenaConf.save(arenaFile);
                    } catch (IOException ex) {
                    	ex.printStackTrace();
                    	ctp.logSevere("Was unable to save the config file for the arena \"" + ctp.getArenaMaster().getEditingArena().getName() + "\", please see the above Stacktrace.");
                    }
                    
                    ctp.getArenaMaster().getEditingArena().getTeamSpawns().put(arg2, spawn);
                    
                    Team team = new Team();
                    team.setSpawn(spawn);
                    team.setColor(arg2);
                    team.setMemberCount(0);
                    try {
                        team.setChatColor(ChatColor.valueOf(spawn.getName().toUpperCase())); // Kj -- init teamchat colour
                    } catch (Exception ex) {
                    	if(spawn.getName().equalsIgnoreCase("cyan"))
                    		team.setChatColor(ChatColor.DARK_AQUA);
                    	else
                    		team.setChatColor(ChatColor.GREEN);
                    }
                    
                    // Check if this spawn is already in the list
                    boolean hasTeam = false;

                    for (Team aTeam : ctp.getArenaMaster().getEditingArena().getTeams())
                        if (aTeam.getColor().equalsIgnoreCase(arg2))
                            hasTeam = true;

                    if (!hasTeam)
                    	ctp.getArenaMaster().getEditingArena().getTeams().add(team);
                    sendMessage(ChatColor.GREEN + "You set the " + team.getChatColor() + arg2 + ChatColor.GREEN + " team spawn point.");
                    sendMessage(ChatColor.BOLD + "If" + ChatColor.WHITE +  " you are done creating the spawns, create the points next. Use: " + ChatColor.AQUA + "/ctp setpoint <name> <vert | hor> [no capture teams]");
                    return;
                }

                sendMessage(ChatColor.RED + "There is no such color!");
                return;
            }
            sendMessage(ctp.getLanguage().NO_PERMISSION);
            return;
        }

        if (arg.equalsIgnoreCase("removespawn")) {
            if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin.removespawn", "ctp.admin"})) {
                if (parameters.size() < 4) {
                	ctp.sendMessage(player, ChatColor.WHITE + "Usage: " + ChatColor.GREEN + "/ctp build removespawn <Team color> ");
                    return;
                }
                arg2 = arg2.toLowerCase();

                File arenaFile = new File("plugins/CaptureThePoints" + File.separator + "Arenas" + File.separator + ctp.getArenaMaster().getEditingArena().getName() + ".yml");
                FileConfiguration arenaConf = YamlConfiguration.loadConfiguration(arenaFile);

                if (arenaConf.getString("Team-Spawns." + arg2 + ".X") == null) {
                	ctp.sendMessage(player, ChatColor.RED + "This arena spawn does not exist! -----> " + ChatColor.GREEN + arg2);
                    return;
                }
                arenaConf.set("Team-Spawns." + arg2, null);
                try {
                    arenaConf.options().copyDefaults(true);
                    arenaConf.save(arenaFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    ctp.logSevere("Error saving the config file for the arena " + ctp.getArenaMaster().getEditingArena().getName() + " while removing " + arg2 + "'s spawn point.");
                }

                if (ctp.getArenaMaster().getEditingArena().getName().equalsIgnoreCase(ctp.getArenaMaster().getEditingArena().getName()))
                	ctp.getArenaMaster().getEditingArena().getTeamSpawns().remove(arg2);
                	
                for (int i = 0; i < ctp.getArenaMaster().getEditingArena().getTeams().size(); i++) {
                    if (!ctp.getArenaMaster().getEditingArena().getTeams().get(i).getColor().equals(arg2)) {
                        continue;
                    }
                    ctp.getArenaMaster().getEditingArena().getTeams().remove(i);
                    break;
                }

                ctp.sendMessage(player, ChatColor.GREEN + arg2 + " " + ChatColor.WHITE + "spawn was removed.");
                return;
            }
            ctp.sendMessage(player, ctp.getLanguage().NO_PERMISSION);
            return;
        }
        
        if (arg.equalsIgnoreCase("setpoint")) {
            if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.setpoint"})) {
                if (parameters.size() < 5) {
                	ctp.sendMessage(player, ChatColor.WHITE + "Usage: " + ChatColor.GREEN + "/ctp build setpoint <Point name> <vert | hor>");
                    return;
                }
                arg2 = arg2.toLowerCase();
                arg3 = arg3.toLowerCase();

                if ((!arg3.equals("vert")) && (!arg3.equals("hor"))) {
                	ctp.sendMessage(player, ChatColor.RED + "Points can be vertical or horizontal: " + ChatColor.GREEN + "vert | hor");
                    return;
                }
                
                Points tmps = new Points();
                tmps.setName(arg2);
                Location loc = player.getLocation();
                int start_x;
                tmps.setX(start_x = loc.getBlockX());
                int start_y;
                tmps.setY(start_y = loc.getBlockY());
                int start_z;
                tmps.setZ(start_z = loc.getBlockZ());

                File arenaFile = new File(ctp.getMainDirectory() + File.separator + "Arenas" + File.separator + ctp.getArenaMaster().getEditingArena().getName() + ".yml");
                FileConfiguration arenaConf = YamlConfiguration.loadConfiguration(arenaFile);

                if ((arenaConf.getString("World") != null) && (!arenaConf.getString("World").equals(loc.getWorld().getName()))) {
                    ctp.sendMessage(player, ChatColor.RED + "Please build all arena points in same world ----> " + ChatColor.GREEN + arenaConf.getString("World"));
                    return;
                }

                for (Points point : ctp.getArenaMaster().getEditingArena().getCapturePoints()) {
                    Location protectionPoint = new Location(player.getWorld(), point.getX(), point.getY(), point.getZ());
                    double distance = loc.distance(protectionPoint);
                    if (distance < 5.0D) {
                    	ctp.sendMessage(player, ChatColor.RED + "You are trying to build too close to another point!"); // Kj to -> too
                        return;
                    }
                }

                if (arg3.equals("vert")) {
                    double yaw = loc.getYaw();

                    while (yaw < 0.0D) {
                        yaw += 360.0D;
                    }
                    BlockFace direction;
                    if ((yaw > 315.0D) || (yaw <= 45.0D)) {
                        direction = BlockFace.WEST;
                    } else {
                        if ((yaw > 45.0D) && (yaw <= 135.0D)) {
                            direction = BlockFace.NORTH;
                        } else {
                            if ((yaw > 135.0D) && (yaw <= 225.0D)) {
                                direction = BlockFace.EAST;
                            } else {
                                direction = BlockFace.SOUTH;
                            }
                        }
                    }
                    switch (direction) {
                        case NORTH:
                            ctp.getUtil().buildVert(player, start_x, start_y - 1, start_z - 1, 2, 4, 4, ctp.getGlobalConfigOptions().ringBlock);
                            player.getWorld().getBlockAt(start_x, start_y, start_z).setType(Material.AIR);
                            player.getWorld().getBlockAt(start_x, start_y + 1, start_z).setType(Material.AIR);
                            player.getWorld().getBlockAt(start_x, start_y, start_z + 1).setType(Material.AIR);
                            player.getWorld().getBlockAt(start_x, start_y + 1, start_z + 1).setType(Material.AIR);
                            arenaConf.addDefault("Points." + arg2 + ".Dir", "NORTH");
                            tmps.setPointDirection("NORTH");
                            break;
                        case EAST:
                            ctp.getUtil().buildVert(player, start_x - 1, start_y - 1, start_z, 4, 4, 2, ctp.getGlobalConfigOptions().ringBlock);
                            player.getWorld().getBlockAt(start_x, start_y, start_z).setType(Material.AIR);
                            player.getWorld().getBlockAt(start_x, start_y + 1, start_z).setType(Material.AIR);
                            player.getWorld().getBlockAt(start_x + 1, start_y, start_z).setType(Material.AIR);
                            player.getWorld().getBlockAt(start_x + 1, start_y + 1, start_z).setType(Material.AIR);
                            arenaConf.addDefault("Points." + arg2 + ".Dir", "EAST");
                            tmps.setPointDirection("EAST");
                            break;
                        case SOUTH:
                            ctp.getUtil().buildVert(player, start_x - 1, start_y - 1, start_z - 1, 2, 4, 4, ctp.getGlobalConfigOptions().ringBlock);
                            player.getWorld().getBlockAt(start_x, start_y, start_z).setType(Material.AIR);
                            player.getWorld().getBlockAt(start_x, start_y + 1, start_z).setType(Material.AIR);
                            player.getWorld().getBlockAt(start_x, start_y, start_z + 1).setType(Material.AIR);
                            player.getWorld().getBlockAt(start_x, start_y + 1, start_z + 1).setType(Material.AIR);
                            arenaConf.addDefault("Points." + arg2 + ".Dir", "SOUTH");
                            tmps.setPointDirection("SOUTH");
                            break;
                        case WEST:
                            ctp.getUtil().buildVert(player, start_x - 1, start_y - 1, start_z - 1, 4, 4, 2, ctp.getGlobalConfigOptions().ringBlock);
                            player.getWorld().getBlockAt(start_x, start_y, start_z).setType(Material.AIR);
                            player.getWorld().getBlockAt(start_x, start_y + 1, start_z).setType(Material.AIR);
                            player.getWorld().getBlockAt(start_x + 1, start_y, start_z).setType(Material.AIR);
                            player.getWorld().getBlockAt(start_x + 1, start_y + 1, start_z).setType(Material.AIR);
                            arenaConf.addDefault("Points." + arg2 + ".Dir", "WEST");
                            tmps.setPointDirection("WEST");
                            break;
                        default:
                        	break;
                    }
                }

                if (arg3.equals("hor")) {
                    for (int x = start_x + 2; x >= start_x - 1; x--) {
                        for (int y = start_y - 1; y <= start_y; y++) {
                            for (int z = start_z - 1; z <= start_z + 2; z++) {
                                player.getWorld().getBlockAt(x, y, z).setTypeId(ctp.getGlobalConfigOptions().ringBlock);
                            }
                        }
                    }

                    player.getWorld().getBlockAt(start_x, start_y, start_z).setType(Material.AIR);
                    player.getWorld().getBlockAt(start_x + 1, start_y, start_z).setType(Material.AIR);
                    player.getWorld().getBlockAt(start_x + 1, start_y, start_z + 1).setType(Material.AIR);
                    player.getWorld().getBlockAt(start_x, start_y, start_z + 1).setType(Material.AIR);
                }

                // save arena point data
                if(parameters.size() > 5) {
                    tmps.setNotAllowedToCaptureTeams(new ArrayList<String>());
                    String colors = "";
                    for(int i = 5; i < parameters.size(); i++) {
                        tmps.getNotAllowedToCaptureTeams().add(parameters.get(i).toLowerCase());
                        colors = colors + parameters.get(i) + ", ";
                    }
                    
                    colors = colors.substring(0, colors.length() - 2);
                    arenaConf.addDefault("Points." + arg2 + ".NotAllowedToCaptureTeams", colors);
                } else {
                    tmps.setNotAllowedToCaptureTeams(null);
                }

                arenaConf.addDefault("Points." + arg2 + ".X", Double.valueOf(tmps.getX()));
                arenaConf.addDefault("Points." + arg2 + ".Y", Double.valueOf(tmps.getY()));
                arenaConf.addDefault("Points." + arg2 + ".Z", Double.valueOf(tmps.getZ()));
                try {
                    arenaConf.options().copyDefaults(true);
                    arenaConf.save(arenaFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    ctp.logSevere("Unable to save the arena's config file while adding a new point named " + arg2);
                }

                if (ctp.getArenaMaster().getEditingArena().getWorld() == null)
                	ctp.getArenaMaster().getEditingArena().setWorld(player.getWorld().getName());
                
                ctp.getArenaMaster().getEditingArena().getCapturePoints().add(tmps);
                
                sendMessage(ChatColor.WHITE + "You created capture point -----> " + ChatColor.GREEN + arg2);
                sendMessage(ChatColor.BOLD + "If" + ChatColor.WHITE + " you are done creating all the points, go setup the lobby. Use: " + ChatColor.AQUA + "/ctp b setlobby");
                return;
            }
            sendMessage(ctp.getLanguage().NO_PERMISSION);
            return;
        }
        
        if (arg.equalsIgnoreCase("removepoint")) {
            if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.removepoint"})) {
                if (parameters.size() < 4) {
                    sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GREEN + "/ctp build removepoint <Point name>");
                    return;
                }
                
                arg2 = arg2.toLowerCase();
                
                Arena a = ctp.getArenaMaster().getEditingArena();
                File arenaFile = new File(ctp.getMainDirectory() + File.separator + "Arenas" + File.separator + a.getName() + ".yml");
                FileConfiguration arenaConf = YamlConfiguration.loadConfiguration(arenaFile);

                if (arenaConf.getString("Points." + arg2 + ".X") == null) {
                    sendMessage(ChatColor.RED + "This arena point does not exist! -----> " + ChatColor.GREEN + arg2);
                    return;
                }
                
                if ((arenaConf.getConfigurationSection("Points").getKeys(false).size() == 1) && (!arenaConf.contains("Team-Spawns"))) {
                    arenaConf.set("World", null);
                }
                
                int start_x = arenaConf.getInt("Points." + arg2 + ".X", 0);
                int start_y = arenaConf.getInt("Points." + arg2 + ".Y", 0);
                int start_z = arenaConf.getInt("Points." + arg2 + ".Z", 0);

                // Kj -- s -> aPoint
                for (Points aPoint : a.getCapturePoints()) {
                    if (aPoint.getName().equalsIgnoreCase(arg2)) {
                    	a.getCapturePoints().remove(aPoint);
                        break;
                    }
                }
                
                //Remove blocks
                if (arenaConf.getString("Points." + arg2 + ".Dir") == null) {
                    for (int x = start_x + 2; x >= start_x - 1; x--) {
                        for (int y = start_y - 1; y <= start_y; y++) {
                            for (int z = start_z - 1; z <= start_z + 2; z++) {
                                if (player.getWorld().getBlockAt(x, y, z).getTypeId() == ctp.getGlobalConfigOptions().ringBlock) {
                                    player.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
                                }
                            }
                        }
                    }
                } else {
                    String direction = arenaConf.getString("Points." + arg2 + ".Dir");
                    ctp.getUtil().removeVertPoint(player, direction, start_x, start_y, start_z, ctp.getGlobalConfigOptions().ringBlock);
                }

                arenaConf.set("Points." + arg2, null);
                
                try {
                    arenaConf.options().copyDefaults(true);
                    arenaConf.save(arenaFile);
                } catch (IOException ex) {
                	ex.printStackTrace();
                	ctp.logSevere("Unable to save the config while removing the point " + arg2);
                }
                sendMessage(ChatColor.WHITE + "You removed capture point -----> " + ChatColor.GREEN + arg2);

                return;
            }
            sendMessage(ctp.getLanguage().NO_PERMISSION);
            return;
        }
        
        if (arg.equalsIgnoreCase("setlobby") || arg.equalsIgnoreCase("lobby")) {
            if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.setlobby", "ctp.admin.lobby"})) {
                File arenaFile = new File(ctp.getMainDirectory() + File.separator + "Arenas" + File.separator + ctp.getArenaMaster().getEditingArena().getName() + ".yml");

                FileConfiguration arenaConf = YamlConfiguration.loadConfiguration(arenaFile);

                String aWorld = arenaConf.getString("World");
                if (aWorld == null) {
                    arenaConf.addDefault("World", player.getWorld().getName());
                    ctp.getArenaMaster().getEditingArena().setWorld(player.getWorld().getName());
                } else if (!aWorld.equals(player.getWorld().getName())) {
                    sendMessage(ChatColor.RED + "Please build arena lobby in same world as its spawns and capture points!");
                    return;
                }
                // Kj -- changed from CTPoints
                Lobby lobby = new Lobby(
                        player.getLocation().getX(),
                        player.getLocation().getY(),
                        player.getLocation().getZ(),
                        player.getLocation().getYaw());

                ctp.getArenaMaster().getEditingArena().setLobby(lobby);

                arenaConf.addDefault("Lobby.X", Double.valueOf(lobby.getX()));
                arenaConf.addDefault("Lobby.Y", Double.valueOf(lobby.getY()));
                arenaConf.addDefault("Lobby.Z", Double.valueOf(lobby.getZ()));
                arenaConf.addDefault("Lobby.Dir", Double.valueOf(lobby.getDir()));
                
                try {
                    arenaConf.options().copyDefaults(true);
                    arenaConf.save(arenaFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    ctp.logSevere("Unable to save an arena's config file, please see the StackTrace for more information.");
                }
                
                sendMessage(ChatColor.GREEN + ctp.getArenaMaster().getEditingArena().getName() + ChatColor.WHITE + " arena lobby created");
                return;
            }
            sendMessage(ctp.getLanguage().NO_PERMISSION);
            return;
        }
        
        if (arg.equalsIgnoreCase("setstands") || arg.equalsIgnoreCase("stands")) {
            if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.setstands", "ctp.admin.stands"})) {
                File arenaFile = new File(ctp.getMainDirectory() + File.separator + "Arenas" + File.separator + ctp.getArenaMaster().getEditingArena().getName() + ".yml");

                FileConfiguration arenaConf = YamlConfiguration.loadConfiguration(arenaFile);

                String aWorld = arenaConf.getString("World");
                if (aWorld == null) {
                    arenaConf.addDefault("World", player.getWorld().getName());
                    ctp.getArenaMaster().getEditingArena().setWorld(player.getWorld().getName());
                } else if (!aWorld.equals(player.getWorld().getName())) {
                    sendMessage(ChatColor.RED + "Please build arena stands in same world as its spawns, lobby, and capture points!");
                    return;
                }
                
                // Kj -- changed from CTPoints
                Stands stands = new Stands(
                        player.getLocation().getX(),
                        player.getLocation().getY(),
                        player.getLocation().getZ(),
                        player.getLocation().getYaw());

                ctp.getArenaMaster().getEditingArena().setStands(stands);

                arenaConf.addDefault("Lobby.X", Double.valueOf(stands.getX()));
                arenaConf.addDefault("Lobby.Y", Double.valueOf(stands.getY()));
                arenaConf.addDefault("Lobby.Z", Double.valueOf(stands.getZ()));
                arenaConf.addDefault("Lobby.Dir", Double.valueOf(stands.getDir()));
                
                try {
                    arenaConf.options().copyDefaults(true);
                    arenaConf.save(arenaFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    ctp.logSevere("Unable to save an arena's config file while creating a stands.");
                }
                
                sendMessage(ChatColor.GREEN + ctp.getArenaMaster().getEditingArena().getName() + ChatColor.WHITE + " arena stands created");
                return;
            }
            sendMessage(ctp.getLanguage().NO_PERMISSION);
            return;
        }

        if (arg.equalsIgnoreCase("pointlist")) {
            if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.pointlist"})) {

                String points = "";
                boolean firstTime = true;

                //Kj -- s -> aPoint
                for (Points aPoint : ctp.getArenaMaster().getEditingArena().getCapturePoints()) {
                    if (firstTime) {
                        points = aPoint.getName();
                        firstTime = false;
                    } else {
                        points = aPoint.getName() + ", " + points;
                    }
                }
                
                if(points.equalsIgnoreCase("")) {
                	sendMessage("There are currently no points.");
                	return;
                }else {
                	sendMessage(ChatColor.GREEN + ctp.getArenaMaster().getEditingArena().getName() + ChatColor.WHITE + " point list:");
                    sendMessage(points);
                    return;
                }
            }
            sendMessage(ctp.getLanguage().NO_PERMISSION);
            return;
        }
        
        if (arg.equalsIgnoreCase("setboundary")) {
            if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.setboundary"})) {
                if (parameters.size() < 4) {
                    ctp.sendMessage(player, ChatColor.WHITE + "Usage: " + ChatColor.GREEN + "/ctp build setboundary <1 | 2>");
                    return;
                }

                Location loc = player.getLocation();
                if (arg2.equalsIgnoreCase("1")) {
                	ctp.getArenaMaster().getEditingArena().setFirstCorner(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

                    // Check arena world
                    if(ctp.getArenaMaster().getEditingArena().getWorld() == null || !ctp.getArenaMaster().getEditingArena().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName()))
                    	ctp.getArenaMaster().getEditingArena().setWorld(loc.getWorld().getName());

                    File arenaFile = new File("plugins/CaptureThePoints" + File.separator + "Arenas" + File.separator + ctp.getArenaMaster().getEditingArena().getName() + ".yml");
                    FileConfiguration arenaConf = YamlConfiguration.loadConfiguration(arenaFile);

                    arenaConf.addDefault("World", ctp.getArenaMaster().getEditingArena().getWorld().getName());
                    arenaConf.addDefault("Boundarys.X1", Integer.valueOf(loc.getBlockX()));
                    arenaConf.addDefault("Boundarys.Y1", Integer.valueOf(loc.getBlockY()));
                    arenaConf.addDefault("Boundarys.Z1", Integer.valueOf(loc.getBlockZ()));
                    
                    try {
                        arenaConf.options().copyDefaults(true);
                        arenaConf.save(arenaFile);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        ctp.logSevere("Unable to save an arena's config file, please see the StackTrace for more details.");
                    }

                    // To boundaries property
                    if(ctp.getArenaMaster().getArenasBoundaries().containsKey(ctp.getArenaMaster().getEditingArena().getName())) {
                        ArenaBoundaries bound = ctp.getArenaMaster().getArenasBoundaries().get(ctp.getArenaMaster().getEditingArena().getName());
                        bound.setWorld(ctp.getArenaMaster().getEditingArena().getWorld().getName());
                        bound.setFirstVector(ctp.getArenaMaster().getEditingArena().getFirstCorner());
                    } else {   // New arena
                        ArenaBoundaries bound = new  ArenaBoundaries();
                        bound.setWorld(loc.getWorld().getName());
                        bound.setFirstVector(ctp.getArenaMaster().getEditingArena().getFirstCorner());
                        ctp.getArenaMaster().getArenasBoundaries().put(ctp.getArenaMaster().getEditingArena().getName(), bound);
                    }

                    sendMessage(ChatColor.GREEN + "First boundary point set.");
                    sendMessage("Now set the second boundary point. Use: " + ChatColor.AQUA + "/ctp b setboundary 2");
                } else if (arg2.equalsIgnoreCase("2")) {
                	ctp.getArenaMaster().getEditingArena().setSecondCorner(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

                    // Check arena world
                    if(ctp.getArenaMaster().getEditingArena().getWorld() == null || !ctp.getArenaMaster().getEditingArena().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName()))
                    	ctp.getArenaMaster().getEditingArena().setWorld(loc.getWorld().getName());

                    File arenaFile = new File("plugins/CaptureThePoints" + File.separator + "Arenas" + File.separator + ctp.getArenaMaster().getEditingArena().getName() + ".yml");
                    FileConfiguration arenaConf = YamlConfiguration.loadConfiguration(arenaFile);

                    arenaConf.addDefault("World", ctp.getArenaMaster().getEditingArena().getWorld().getName());
                    arenaConf.addDefault("Boundarys.X2", Integer.valueOf(loc.getBlockX()));
                    arenaConf.addDefault("Boundarys.Y2", Integer.valueOf(loc.getBlockY()));
                    arenaConf.addDefault("Boundarys.Z2", Integer.valueOf(loc.getBlockZ()));
                    try {
                        arenaConf.options().copyDefaults(true);
                        arenaConf.save(arenaFile);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        ctp.logSevere("Unable to save the arena config file, please see the above StackTrace.");
                    }

                    // To boundaries property
                    if(ctp.getArenaMaster().getArenasBoundaries().containsKey(ctp.getArenaMaster().getEditingArena().getName())) {
                        ArenaBoundaries bound = ctp.getArenaMaster().getArenasBoundaries().get(ctp.getArenaMaster().getEditingArena().getName());
                        bound.setWorld(ctp.getArenaMaster().getEditingArena().getWorld().getName());
                        bound.setSecondVector(ctp.getArenaMaster().getEditingArena().getSecondCorner());
                    } else {   // New arena
                        ArenaBoundaries bound = new  ArenaBoundaries();
                        bound.setWorld(loc.getWorld().getName());
                        bound.setSecondVector(ctp.getArenaMaster().getEditingArena().getSecondCorner());
                        ctp.getArenaMaster().getArenasBoundaries().put(ctp.getArenaMaster().getEditingArena().getName(), bound);
                    }

                    sendMessage(ChatColor.GREEN + "Second boundary point set.");
                    sendMessage("Now set the spawn points for each team you want to have in this arena. Use: " + ChatColor.AQUA + "/ctp b setspawn <color>");
                }

                return;
            }
        }

        if (arg.equalsIgnoreCase("maximumplayers") || arg.equalsIgnoreCase("maxplayers") || arg.equalsIgnoreCase("max")) {
            if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.maximumplayers"})) {
                if (parameters.size() < 4) {
                    sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GREEN + "/ctp build maximumplayers <number>");
                    return;
                }

                int amount = 0;
                try {
                    amount = Integer.parseInt(arg2);
                } catch (Exception ex) {
                    sendMessage(ChatColor.WHITE + arg2 + " is not a number.");
                    return;
                }

                File arenaFile = new File("plugins/CaptureThePoints" + File.separator + "Arenas" + File.separator + ctp.getArenaMaster().getEditingArena().getName() + ".yml");
                FileConfiguration arenaConf = YamlConfiguration.loadConfiguration(arenaFile);
                arenaConf.addDefault("MaximumPlayers", amount);
                try {
                    arenaConf.options().copyDefaults(true);
                    arenaConf.save(arenaFile);
                } catch (IOException ex) {
                	ex.printStackTrace();
                	ctp.logSevere("Error while attempting to save the arena " + ctp.getArenaMaster().getEditingArena().getName() + "'s config file.");
                }

                ctp.getArenaMaster().getEditingArena().setMaxPlayers(amount);
                sendMessage(ChatColor.GREEN + "Set maximum players of " + ctp.getArenaMaster().getEditingArena().getName() + " to " + amount + ".");
                return;
            }
            sendMessage(ctp.getLanguage().NO_PERMISSION);
            return;
        }
        
        if (arg.equalsIgnoreCase("minimumplayers") || arg.equalsIgnoreCase("minplayers") || arg.equalsIgnoreCase("min")) {
            if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.minimumplayers"})) {
                if (parameters.size() < 4) {
                    sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GREEN + "/ctp build minimumplayers <number>");
                    return;
                }

                int amount = 0;
                try {
                    amount = Integer.parseInt(arg2);
                } catch (Exception ex) {
                    sendMessage(ChatColor.WHITE + arg2 + " is not a number.");
                    return;
                }

                File arenaFile = new File("plugins/CaptureThePoints" + File.separator + "Arenas" + File.separator + ctp.getArenaMaster().getEditingArena().getName() + ".yml");
                FileConfiguration arenaConf = YamlConfiguration.loadConfiguration(arenaFile);
                arenaConf.addDefault("MinimumPlayers", amount);
                
                try {
                    arenaConf.options().copyDefaults(true);
                    arenaConf.save(arenaFile);
                } catch (IOException ex) {
                	ex.printStackTrace();
                	ctp.logSevere("Unable to save the config file for the arena: " + ctp.getArenaMaster().getEditingArena().getName());
                }

                ctp.getArenaMaster().getEditingArena().setMinPlayers(amount);
                sendMessage(ChatColor.GREEN + "Set minimum players of " + ctp.getArenaMaster().getEditingArena().getName() + " to " + amount + ".");
                return;
            }
            sendMessage(ctp.getLanguage().NO_PERMISSION);
            return;
        }
        
        if (arg.equalsIgnoreCase("pointstowin") || arg.equalsIgnoreCase("ptw") || arg.equalsIgnoreCase("pointsneeded")) {
            if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.pointstowin"})) {
                if (parameters.size() < 4) {
                    sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GREEN + "/ctp build pointstowin <number>");
                    return;
                }

                int amount = 0;
                try {
                    amount = Integer.parseInt(arg2);
                } catch (Exception ex) {
                    sendMessage(ChatColor.WHITE + arg2 + " is not a number.");
                    return;
                }

                File arenaFile = new File("plugins/CaptureThePoints" + File.separator + "Arenas" + File.separator + ctp.getArenaMaster().getEditingArena().getName() + ".yml");
                FileConfiguration arenaConf = YamlConfiguration.loadConfiguration(arenaFile);
                arenaConf.addDefault("GlobalSettings.GameMode.PointCapture.PointsToWin", amount);
                
                try {
                    arenaConf.options().copyDefaults(true);
                    arenaConf.save(arenaFile);
                } catch (IOException ex) {
                	ex.printStackTrace();
                    ctp.logSevere("Unable to save the config file for the arena: " + ctp.getArenaMaster().getEditingArena().getName());
                }

                ctp.getArenaMaster().getEditingArena().getConfigOptions().pointsToWin = amount;
                sendMessage(ChatColor.GREEN + "Set the number of points needed to win " + ctp.getArenaMaster().getEditingArena().getName() + " to " + amount + ".");
                return;
            }
            sendMessage(ctp.getLanguage().NO_PERMISSION);
            return;
        }

        if (arg.equalsIgnoreCase("save")) {
            if (ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.save"})) {
                if(ctp.getGlobalConfigOptions().enableHardArenaRestore
                		&& ctp.getArenaMaster().getEditingArena().getFirstCorner() != null
                		&& ctp.getArenaMaster().getEditingArena().getSecondCorner() != null) {
                    ctp.getServer().getScheduler().runTaskLaterAsynchronously(ctp, new Runnable() {
                        public void run () {
                            int xlow = ctp.getArenaMaster().getEditingArena().getFirstCorner().getBlockX();
                            int xhigh = ctp.getArenaMaster().getEditingArena().getSecondCorner().getBlockX();
                            if (ctp.getArenaMaster().getEditingArena().getSecondCorner().getBlockX() < ctp.getArenaMaster().getEditingArena().getFirstCorner().getBlockX()) {
                                xlow = ctp.getArenaMaster().getEditingArena().getSecondCorner().getBlockX();
                                xhigh = ctp.getArenaMaster().getEditingArena().getFirstCorner().getBlockX();
                            }
                            
                            int ylow = ctp.getArenaMaster().getEditingArena().getFirstCorner().getBlockY();
                            int yhigh = ctp.getArenaMaster().getEditingArena().getSecondCorner().getBlockY();
                            if (ctp.getArenaMaster().getEditingArena().getSecondCorner().getBlockY() < ctp.getArenaMaster().getEditingArena().getFirstCorner().getBlockY()) {
                                ylow = ctp.getArenaMaster().getEditingArena().getSecondCorner().getBlockY();
                                yhigh = ctp.getArenaMaster().getEditingArena().getFirstCorner().getBlockY();
                            }
                            
                            int zlow = ctp.getArenaMaster().getEditingArena().getFirstCorner().getBlockZ();
                            int zhigh = ctp.getArenaMaster().getEditingArena().getSecondCorner().getBlockZ();
                            if (ctp.getArenaMaster().getEditingArena().getSecondCorner().getBlockZ() < ctp.getArenaMaster().getEditingArena().getFirstCorner().getBlockZ()) {
                                zlow = ctp.getArenaMaster().getEditingArena().getSecondCorner().getBlockZ();
                                zhigh = ctp.getArenaMaster().getEditingArena().getFirstCorner().getBlockZ();
                            }
                            ctp.getMysqlConnector().connectToMySql();

                            ctp.getArenaRestore().checkForArena(ctp.getArenaMaster().getEditingArena().getName(), ctp.getArenaMaster().getEditingArena().getWorld().getName());
                            World world = ctp.getArenaMaster().getEditingArena().getWorld();

                            Spawn firstPoint = new Spawn();
                            Spawn secondPoint = new Spawn();

                            for (int x = xlow; x <= xhigh; x++) {
                                for (int y = ylow; y <= yhigh; y++) {
                                    boolean first = true; // If it is first block in the stack
                                    int id = -1;
                                    int data = 0;
                                    firstPoint.setX(0); firstPoint.setY(0); firstPoint.setZ(0);
                                    secondPoint.setX(0); secondPoint.setY(0); secondPoint.setZ(0);
                                    
                                    for (int z = zlow; z <= zhigh; z++) {
                                        if(ctp.getArenaRestore().canStackBlocksToMySQL(world.getBlockAt(x, y, z).getTypeId(), id, first, data, world.getBlockAt(x, y, z).getData())) {
                                            if(first) { // First block in the stack
                                                first = false;
                                                id = world.getBlockAt(x, y, z).getTypeId();
                                                data = world.getBlockAt(x, y, z).getData();
                                                firstPoint.setX(x); firstPoint.setY(y); firstPoint.setZ(z);
                                                secondPoint.setX(x); secondPoint.setY(y); secondPoint.setZ(z);
                                            } else {  // Add one block to stack
                                                secondPoint.setZ(z);
                                            }
                                        } else { // Cant stack
                                            if(first) { // Only one block to write
                                                firstPoint.setX(x); firstPoint.setY(y); firstPoint.setZ(z);
                                                secondPoint.setX(x); secondPoint.setY(y); secondPoint.setZ(z);
                                                
                                                ctp.getArenaRestore().storeBlock(world.getBlockAt((int)firstPoint.getX(), (int)firstPoint.getY(), (int)firstPoint.getZ()), firstPoint, secondPoint, ctp.getArenaMaster().getEditingArena().getName());
                                                first = true;
                                                id = -1;
                                                data = 0;
                                            } else { // Last block in stack
                                                ctp.getArenaRestore().storeBlock(world.getBlockAt((int)firstPoint.getX(), (int)firstPoint.getY(), (int)firstPoint.getZ()), firstPoint, secondPoint, ctp.getArenaMaster().getEditingArena().getName());
                                                
                                                id = world.getBlockAt(x, y, z).getTypeId();
                                                data = world.getBlockAt(x, y, z).getData();
                                                firstPoint.setX(x); firstPoint.setY(y); firstPoint.setZ(z);
                                                secondPoint.setX(x); secondPoint.setY(y); secondPoint.setZ(z);
                                            }
                                        }
                                    }
                                    
                                    // Check if there is something to write to mySQL
                                    if(!first) {
                                        ctp.getArenaRestore().storeBlock(world.getBlockAt(x, y, (int)firstPoint.getZ()), firstPoint, secondPoint, ctp.getArenaMaster().getEditingArena().getName());
                                    }
                                }
                            }
                            sendMessage("Arena data saved.");
                        }
                    }, 5L);
                    return;
                } else {
                sendMessage(ChatColor.RED + "EnableHardArenaRestore is not enabled or some arena points are not defined. Arena: " + ChatColor.GREEN + ctp.getArenaMaster().getEditingArena().getName());
                return;
                }
            }
            sendMessage(ctp.getLanguage().NO_PERMISSION);
            return;
        }
        
        if (arg.equalsIgnoreCase("restore")) {
        	if (!ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.restore"})) {
        		sendMessage(ctp.getLanguage().NO_PERMISSION);
        		return;
        	}
        	
            if(!ctp.getGlobalConfigOptions().enableHardArenaRestore) {
                sendMessage(ChatColor.RED + "Hard arena restore is not enabled.");
                return;
            }
            
            ctp.getArenaRestore().restoreMySQLBlocks(ctp.getArenaMaster().getEditingArena());
            return;
        }

        if (arg.equalsIgnoreCase("findchests")) {
            if (!ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.findchests"})) {
                sendMessage(ctp.getLanguage().NO_PERMISSION);
                return;
            }

            String arenaName;
            if(arg2 == null || arg2 == "")
                arenaName = ctp.getArenaMaster().getEditingArena().getName();

            arenaName = arg2;
            ctp.getMysqlConnector().connectToMySql();

            ResultSet rezult = ctp.getMysqlConnector().getData("SELECT * FROM Simple_block WHERE Simple_block.arena_name = '" + arenaName + "' AND Simple_block.`block_type` = " + Material.CHEST.getId());
            try {
                int chestCount = 0;
                int totalItemsCount = 0;
                ctp.logInfo("------------------------------------------------------------");//60
                ctp.logInfo(String.format("|             Arena name:           %15s        |", arenaName));
                while (rezult.next()) {
                    chestCount++;
                    ctp.logInfo("------------------------------------------------------------");
                    ctp.logInfo(String.format("|             Skrynios NR.:                 %5d          |", chestCount));
                    ctp.logInfo("|----------------------------------------------------------|");
                    ctp.logInfo("| NR. | Daikto pav.   | Kiekis | Patvarumas | Vieta skryn. |");
                    ResultSet rezult2 = ctp.getMysqlConnector().getData("SELECT distinct `type` , `durability` , `amount` , `place_in_inv`"
                            + "FROM Simple_block, item WHERE Simple_block.arena_name = '" + arenaName + "' AND Simple_block.`block_type` = " + Material.CHEST.getId() + " "
                            + "AND item.`block_ID` = " + rezult.getInt("id"));

                    int itemCount = 0;
                    int itemCountInChest = 0;
                    while(rezult2.next()) {
                        if(rezult2.getInt("type") == 0)
                            continue;

                        itemCount++;
                        itemCountInChest = itemCountInChest + rezult2.getInt("amount");
                        ctp.logInfo("|-----+---------------+--------+------------+--------------|");
                        ctp.logInfo(String.format("|%4d |%-15s| %6d | %10d | %12d |", itemCount, Material.getMaterial(rezult2.getInt("type")).name(),
                                rezult2.getInt("amount"), rezult2.getInt("durability"), rezult2.getInt("place_in_inv")));
                    }
                    
                    totalItemsCount = totalItemsCount + itemCountInChest;
                    ctp.logInfo("|-----+----------------------------------------------------|");
                    ctp.logInfo(String.format("|     | Total in chests:         %10d          |", itemCountInChest));
                    ctp.logInfo("------+-----------------------------------------------------\n\n");
                }

                ctp.logInfo("Total chests: " + chestCount);
                ctp.logInfo("Total in chests: " + totalItemsCount);

                sendMessage(ChatColor.GREEN + "Report is ready, please check server console!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                ctp.logSevere("An error occured while trying to get a list of all the chests.");
            }
        }
        
        if (arg.equalsIgnoreCase("check")) {
        	if (!ctp.getPermissions().canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.check"})) {
        		sendMessage(ctp.getLanguage().NO_PERMISSION);
                return;
            }
        	
        	String error = ctp.getArenaMaster().checkArena(ctp.getArenaMaster().getEditingArena(), player);
        	if(error.isEmpty())
        		sendMessage(ChatColor.GREEN + "The arena " + ctp.getArenaMaster().getEditingArena().getName() + " appears to be ready to play. Don't forget to set up role signs in the lobby.");
        	else
        		sendMessage(error);
        }
    }
}
