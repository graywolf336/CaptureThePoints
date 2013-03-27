package me.dalton.capturethepoints.listeners;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.HealingItems;
import me.dalton.capturethepoints.beans.Arena;
import me.dalton.capturethepoints.beans.Items;
import me.dalton.capturethepoints.beans.Lobby;
import me.dalton.capturethepoints.beans.PlayerData;
import me.dalton.capturethepoints.beans.PlayersAndCooldowns;
import me.dalton.capturethepoints.beans.Spawn;
import me.dalton.capturethepoints.commands.PJoinCommand;
import me.dalton.capturethepoints.enums.ArenaLeaveReason;
import me.dalton.capturethepoints.util.PotionManagement;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;

public class CaptureThePointsPlayerListener implements Listener {
    private final CaptureThePoints ctp;

    public CaptureThePointsPlayerListener (CaptureThePoints plugin) {
        this.ctp = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess (PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Arena arena = ctp.getArenaMaster().getArenaPlayerIsIn(player.getName());
        
        if (arena != null && arena.getConfigOptions() != null && !arena.getConfigOptions().allowCommands) {
            String[] args = event.getMessage().split(" ");
            if (!ctp.getPermissions().canAccess(player, false, new String[] { "ctp.*", "ctp.admin" }) && arena.isGameRunning() && arena.getPlayersData().containsKey(player.getName())
                    && !args[0].equalsIgnoreCase("/ctp")) {
            	ctp.sendMessage(player, ChatColor.RED + "You can't use commands while playing!");
                event.setCancelled(true);
                if(ctp.getGlobalConfigOptions().debugMessages)
                	ctp.getLogger().info("Just cancelled a PlayerCommandPreprocessEvent because the player is playing.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem (PlayerDropItemEvent event) {
    	if(ctp.getArenaMaster().isPlayerInAnArena(event.getPlayer().getName())) {
            Player player = event.getPlayer();
            Arena a = ctp.getArenaMaster().getArenaPlayerIsIn(player.getName());
            
            //Player in the lobby
            if (a.getPlayerData(player.getName()).inLobby()) {
                event.setCancelled(true);
                if(ctp.getGlobalConfigOptions().debugMessages)
                	ctp.getLogger().info("Just cancelled a PlayerDropItemEvent because the player is in the lobby.");
                ctp.sendMessage(player, ChatColor.RED + "You cannot drop items in the lobby!");
                return;
            }
            if (!a.getConfigOptions().allowDropItems) {
                event.setCancelled(true);
                if(ctp.getGlobalConfigOptions().debugMessages)
                	ctp.getLogger().info("Just cancelled a PlayerDropItemEvent because you have the config option allowDropItems set to false.");
                ctp.sendMessage(player, ChatColor.RED + "You may not drop items.");
                return;
            }
        }else return;
    }
    
	@EventHandler (priority = EventPriority.HIGHEST)
	public void invClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		
		if(ctp.getArenaMaster().isPlayerInAnArena(p.getName())) {
			if (event.getInventory().getName().equalsIgnoreCase("container.crafting") && event.getRawSlot() == 5 && event.getSlotType() == SlotType.ARMOR) {
				ctp.sendMessage(p, ChatColor.RED + "You can't remove your helmet.");
				event.setCancelled(true);
				if(ctp.getGlobalConfigOptions().debugMessages)
                	ctp.getLogger().info("Just cancelled a InventoryClickEvent because the player tried to remove their helmet.");
				return;
			}
		}else return;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract (PlayerInteractEvent event){
        if (ctp.getArenaMaster().getArenas().isEmpty())
        	return; //if there are no arenas, then do nothing.
        
        if(ctp.getArenaMaster().isPlayerInAnArena(event.getPlayer())) {
            Player p = event.getPlayer();
            Arena a = ctp.getArenaMaster().getArenaPlayerIsIn(p.getName());
            
            //Potion Throwing In Lobby
            if (a.getPlayerData(p.getName()).inLobby() && p.getItemInHand().getTypeId() == 373){
            	if (event.hasBlock() && !((event.getClickedBlock().getState()) instanceof Sign) && event.getClickedBlock().getTypeId() != 42){
                   event.setCancelled(true);
                   if(ctp.getGlobalConfigOptions().debugMessages)
                   	ctp.getLogger().info("Just cancelled a PlayerInteractEvent because the player tried to throw a potion in the lobby.");
                   ctp.sendMessage(p, ChatColor.RED + "You cannot throw potions in the Lobby!");
                   p.updateInventory();
                   return;
                   
            	}
            	
            	if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
            		if(event.getClickedBlock().getState() instanceof Sign || event.getClickedBlock().getTypeId() == 42){
                        p.updateInventory();
            			event.setCancelled(true);
            			if(ctp.getGlobalConfigOptions().debugMessages)
                           	ctp.getLogger().info("Just cancelled a PlayerInteractEvent because the player tried to throw a potion in the lobby.");
            		}
            	}else if(event.getAction() == Action.RIGHT_CLICK_AIR){
                    event.setCancelled(true);
                    ctp.sendMessage(p, ChatColor.RED + "You cannot throw potions in the Lobby!");
                    if(ctp.getGlobalConfigOptions().debugMessages)
                       	ctp.getLogger().info("Just cancelled a PlayerInteractEvent because the player tried to throw a potion in the lobby.");
                    p.updateInventory();
                    return;
            	}
            	
            }
            
            // Iron block
            if (event.hasBlock() && event.getClickedBlock().getTypeId() == 42) {
                //If this role exists
                if (ctp.getRoles().containsKey(a.getPlayerData(p.getName()).getRole())) {
                    if (!a.getPlayerData(p.getName()).isReady()) {
                        ctp.getUtil().sendMessageToPlayers(a, p, ChatColor.GREEN + p.getName() + ChatColor.WHITE + " is ready.");
                    }
                    a.getPlayerData(p.getName()).setReady(true);
                    a.getLobby().getPlayersInLobby().put(p.getName(), true);
                    checkLobby(a, p);
                } else {
                	ctp.sendMessage(p, ChatColor.RED + "Please select a role.");
                }
                return;
            }

            // Sign
            if (event.hasBlock() && event.getClickedBlock().getState() instanceof Sign) {
                // Cast the block to a sign to get the text on it.
                Sign sign = (Sign) event.getClickedBlock().getState();
                // Check if the first line of the sign is a class name.
                String role = sign.getLine(0);

                if (role.equalsIgnoreCase("[CTP]")) {
                    shop(a, p, sign);
                } else if (!ctp.getRoles().containsKey(role.toLowerCase()) && !role.equalsIgnoreCase("random")) {
                    return;
                } else {
                    /* Sign looks like:
                     * ########## 
                     * #  ROLE  # <-- getLine(0)
                     * #  PRICE # <-- getLine(1)
                     * #        # <-- getLine(2)
                     * #        # <-- getLine(3)
                     * ##########
                     *     #
                     *     # 
                     */
                    
                    // Player is in Lobby choosing role.
                    if (a.getLobby().getPlayersInLobby().containsKey(p.getName())) {
                        // Kj's
                        if (role.equalsIgnoreCase("random")) {
                            int size = ctp.getRoles().size();
                            if (size > 1) { // If there is more than 1 role to choose from
                                Random random = new Random();
                                int nextInt = random.nextInt(size); // Generate a random number between 0 (inclusive) -> Number of roles (exclusive)
                                List<String> roles = new LinkedList<String>(ctp.getRoles().keySet()); // Get a list of available roles and convert to a String List

                                role =
                                        roles.get(nextInt) == null
                                        ? roles.get(0)
                                        : roles.get(nextInt); // Change the role based on the random number. (Ternary null check)
                            }
                        }
                        
                        PotionManagement.removeAllEffects(p);
                        
                        String oldRole = "";
                        
                        if (a.getPlayerData(p.getName()).getRole() != null && !a.getPlayerData(p.getName()).getRole().isEmpty()){
                        	oldRole = a.getPlayerData(p.getName()).getRole();
                        }
                        
                        if(!ctp.getInvManagement().assignRole(a, p, role.toLowerCase()))
                            return;

                        if (a.getPlayerData(p.getName()).getRole() != null && !a.getPlayerData(p.getName()).getRole().isEmpty() && !oldRole.isEmpty()) {
                            
                        	ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "Changing your role from " + ChatColor.GOLD + oldRole.substring(0, 1).toUpperCase() + oldRole.substring(1).toLowerCase()
                                    + ChatColor.LIGHT_PURPLE + " to " + ChatColor.GOLD + role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase() + ChatColor.LIGHT_PURPLE + ".");
                            ctp.sendMessage(p, "Remember to hit the iron block to ready up!");

                        } else {
                        	ctp.sendMessage(p, ChatColor.GOLD + role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase()
                                    + ChatColor.LIGHT_PURPLE + " selected. Hit the iron block to ready up!");
                        }
                        
                        a.getPlayerData(p.getName()).setReady(false); // Un-ready the player
                        a.getLobby().getPlayersInLobby().put(p.getName(), false);
                        return;

                        // Player is in game choosing role.
                    } else if (a.getPlayerData(p.getName()).inArena()) {
                        
                        int price = 0;
                        String pricestr = sign.getLine(1) == null ? "" : sign.getLine(1);
                        
                        if (!pricestr.isEmpty()) {
                            try {
                                price = Integer.parseInt(pricestr); // Get price. 
                            } catch (Exception e) {
                                price = Integer.MAX_VALUE; // Sign's price is illegal.
                            }
                        }                                                    

                        // Kj's
                        if (role.equalsIgnoreCase("random")) {
                            int size = ctp.getRoles().size();
                            if (size > 1) { // If there is more than 1 role to choose from
                                Random random = new Random();
                                int nextInt = random.nextInt(size); // Generate a random number between 0 (inclusive) -> Number of roles (exclusive)
                                List<String> roles = new LinkedList<String>(ctp.getRoles().keySet()); // Get a list of available roles and convert to a String List
                                role =
                                        roles.get(nextInt) == null
                                        ? roles.get(0)
                                        : roles.get(nextInt); // Change the role based on the random number. (Ternary null check)
                            }
                        }
                        
                        if (price == 0) {
                            String oldRole = a.getPlayerData(p.getName()).getRole();
                            ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "Changing your role from " + ChatColor.GOLD + oldRole.substring(0, 1).toUpperCase() + oldRole.substring(1).toLowerCase()
                                    + ChatColor.LIGHT_PURPLE + " to " + ChatColor.GOLD + role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase() + ChatColor.LIGHT_PURPLE + ".");

                            ctp.getInvManagement().assignRole(a, p, role.toLowerCase());
                        } else {
                            if (ctp.getMoneyUtil().canPay(p.getName(), price)) {
                            	ctp.getMoneyUtil().chargeAccount(p.getName(), price);
                                String oldRole = a.getPlayerData(p.getName()).getRole();
                                ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "Successfully bought new role for " + ChatColor.GREEN + price + ChatColor.LIGHT_PURPLE + ". "
                                        + "You changed from " + ChatColor.GOLD + oldRole.substring(0, 1).toUpperCase() + oldRole.substring(1).toLowerCase()
                                        + ChatColor.LIGHT_PURPLE + " to " + ChatColor.GOLD + role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase() + ChatColor.LIGHT_PURPLE + ".");
                                ctp.getInvManagement().assignRole(a, p, role.toLowerCase());
                                return;
                            } else {
                                String message =
                                        price != Integer.MAX_VALUE
                                        ? "Not enough money! You have " + ChatColor.GREEN + a.getPlayerData(p.getName()).getMoney() + ChatColor.WHITE + " money, but you need " + ChatColor.GREEN + price + ChatColor.WHITE + " money."
                                        : ChatColor.RED + "This sign does not have a legal price. Please inform an admin.";
                                ctp.sendMessage(p, message);
                                return;
                            }
                        }
                    }
                }
                return;
            }

            // Wool for team selection
            selectTeam(event, a, p);

            // check for Healing item usage
            useHealingItem(event, a, p);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
    	if(!ctp.getArenaMaster().isPlayerInAnArena(event.getPlayer()))
    		return;
    	
        Location loc = event.getTo();
        Player p = event.getPlayer();
        Arena a = ctp.getArenaMaster().getArenaPlayerIsIn(p.getName());
        
        // Find if player is in arena
        if (a.getPlayerData(p.getName()) != null && !a.getPlayerData(p.getName()).inLobby()) {
            if (a.getPlayerData(p.getName()).getMoveChecker() >= 10) {
            	a.getPlayerData(p.getName()).setMoveChecker(0);
                if (ctp.getArenaUtil().isInside(loc.getBlockY(), a.getY1(), a.getY2())
                		&& ctp.getArenaUtil().isInside(loc.getBlockX(), a.getX1(), a.getX2())
                		&& ctp.getArenaUtil().isInside(loc.getBlockZ(), a.getZ1(), a.getZ2())
                		&& loc.getWorld().getName().equalsIgnoreCase(a.getWorld().getName())) {
                    return;
                } else {
                    String color = a.getPlayerData(p.getName()).getTeam().getColor();
                    Location loc2 = new Location(a.getWorld(), a.getTeamSpawns().get(color).getX(), a.getTeamSpawns().get(color).getY() + 1, a.getTeamSpawns().get(color).getZ());
                    loc2.setYaw((float) a.getTeamSpawns().get(color).getDir());
                    loc2.getWorld().loadChunk(loc2.getBlockX(), loc2.getBlockZ());
                    p.teleport(loc2);
                }
            } else {
            	a.getPlayerData(p.getName()).addOneMoveChecker();
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit (PlayerQuitEvent event) {
        if(ctp.getArenaMaster().isPlayerInAnArena(event.getPlayer()))
        	ctp.getArenaMaster().getArenaPlayerIsIn(event.getPlayer().getName()).leaveGame(event.getPlayer(), ArenaLeaveReason.PLAYER_QUIT);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn (PlayerRespawnEvent event) {
        if(!ctp.getArenaMaster().isPlayerInAnArena(event.getPlayer()))
            return;

        Player p = event.getPlayer();
        Arena a = ctp.getArenaMaster().getArenaPlayerIsIn(p.getName());
        
        if(!a.isGameRunning() && a.getPlayerData(p.getName()).inLobby()) {
        	a.getPlayerData(p.getName()).setInArena(false);
        	a.getPlayerData(p.getName()).setInLobby(false);
            a.getLobby().getPlayersInLobby().remove(p.getName());
            event.setRespawnLocation(ctp.getPrevoiusPosition().get(p.getName()));
            a.leaveGame(p, ArenaLeaveReason.PLAYER_RESPAWN);
            p.sendMessage(ChatColor.LIGHT_PURPLE + "[CTP] You left the CTP game.");
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport (PlayerTeleportEvent event) {
    	Player play = event.getPlayer();
    	if(!ctp.getArenaMaster().isPlayerInAnArena(play.getName()))
    		return;
    	
    	Arena a = ctp.getArenaMaster().getArenaPlayerIsIn(play.getName());
    	
        if (!(a.isGameRunning())) {
            if (a.getPlayerData(play.getName()) != null && a.getPlayerData(play.getName()).inLobby()) {
                if (ctp.getArenaUtil().isInside(event.getTo().getBlockX(), a.getX1(), a.getX2())
                		&& ctp.getArenaUtil().isInside(event.getTo().getBlockY(), a.getY1(), a.getY2())
                		&& ctp.getArenaUtil().isInside(event.getTo().getBlockZ(), a.getZ1(), a.getZ2())
                		&& event.getTo().getWorld().getName().equalsIgnoreCase(a.getWorld().getName())) {
                	a.getPlayerData(play.getName()).setJustJoined(false);
                    return;
                } else {
                    if (a.getPlayerData(play.getName()).getJustJoined()) { // allowed to teleport
                    	a.getPlayerData(play.getName()).setJustJoined(false);
                        return;
                    } else {
                        event.setCancelled(true);
                        a.getPlayerData(play.getName()).setInArena(false);
                        a.getPlayerData(play.getName()).setInLobby(false);
                        a.getLobby().getPlayersInLobby().remove(play.getName());
                        a.leaveGame(play, ArenaLeaveReason.PLAYER_TELEPORT);
                        ctp.sendMessage(play, ChatColor.LIGHT_PURPLE + "You left the CTP game.");
                    }
                }
            }
            return;
        }

        if (a.getPlayerData(play.getName()) == null) {
            return;
        }

        //If ctp leave command
        if (a.getPlayerData(play.getName()).getJustJoined()) {
        	a.getPlayerData(play.getName()).setJustJoined(false);
            return;
        }

        // Find if player is in arena
        if (a.getPlayerData(play.getName()).inArena()) {
            Spawn playerspawn = a.getPlayerData(play.getName()).getTeam().getSpawn(); // Get the player's spawnpoint
            if (event.getTo().getX() == playerspawn.getX() && event.getTo().getZ() == playerspawn.getZ()) {
                // The player is going to their spawn.
                return;
            }
            if (ctp.getArenaUtil().isInside(event.getTo().getBlockX(), a.getX1(), a.getX2())
            		&& ctp.getArenaUtil().isInside(event.getTo().getBlockZ(), a.getZ1(), a.getZ2())
            		&& event.getTo().getWorld().getName().equalsIgnoreCase(a.getWorld().getName())) {
                // The player is teleporting in the arena.
                return;
            } else {
                // The player is teleporting out of the arena!
                event.setCancelled(true);
                if(ctp.getGlobalConfigOptions().debugMessages)
                   	ctp.getLogger().info("Just cancelled a PlayerTeleportEvent because the player tried to teleport out of the arena.");
                ctp.sendMessage(play, ChatColor.RED + "Not allowed to teleport out of the arena!");
            }
        }
    }

    /** Check the lobby to see if player[s] can be transferred.  */
    private void checkLobby(Arena arena, Player p) {
        // Kj -- If autostart is turned off, might as well ignore this. However, if a game has started and someone wants to join, that's different.
        if (arena.getConfigOptions().autoStart || !arena.isPreGame()) {
            Lobby lobby = arena.getLobby();
            int readypeople = lobby.countReadyPeople();

            // The maximum number of players must be greater than the players already playing.
            if (arena.getMaxPlayers() > arena.getPlayersPlaying().size()) {
                // Game not yet started
                if (arena.isPreGame()) {
                    if (!lobby.hasUnreadyPeople()) {
                        if (readypeople >= arena.getMinPlayers()) {//We have more than the required amount of people
                            if (readypeople % arena.getTeams().size() == 0) {  // There may be more than two teams playing. 
                            	ctp.getArenaUtil().moveToSpawns(arena);
                            } else {
                                if (arena.getConfigOptions().exactTeamMemberCount) {
                                    if (readypeople / arena.getTeams().size() >= 1) {
                                    	ctp.getArenaUtil().moveToSpawns(arena);
                                    } else {
                                    	ctp.getUtil().sendMessageToPlayers(arena, "There are already an odd number of players, please wait for a new player to ready up.");
                                        return;
                                    }
                                } else {   // Does not require exact count and everyone is ready. Move them.
                                	ctp.getArenaUtil().moveToSpawns(arena);
                                }
                            }
                        }else {
                            //Save variable for minor bug that results from player error
                            Arena mainArenaTmp = arena;
                            if (ctp.getArenaMaster().hasSuitableArena(readypeople)) {
                                ctp.getUtil().sendMessageToPlayers(arena, ChatColor.RED + "Not enough players for a game. Attempting to change arena. [Needed " + arena.getMinPlayers() + " players, found " + readypeople + "].");
                                List<String> transport = new LinkedList<String>(lobby.getPlayersInLobby().keySet());
                                arena.endGame(true);
                                //ctp.chooseSuitableArena(readypeople);
                                for (String aPlayer : transport) {
                                    PJoinCommand pj = new PJoinCommand(ctp); 
                                    pj.execute(ctp.getServer().getConsoleSender(), Arrays.asList("ctp", "pjoin", aPlayer, ctp.getArenaMaster().getSelectedArena().getName()));
                                }
                            } else {
                            	//Reseting main Arena back
                            	arena = mainArenaTmp;
                                ctp.getUtil().sendMessageToPlayers(arena, ChatColor.RED + "Not enough players for a game. No other suitable arenas found. [Needed " + arena.getMinPlayers() + " players, found " + readypeople + "].");
                            }
                        }
                    } else {
                    	String notReady = "";
                    	for(String player : lobby.getPlayersInLobby().keySet()){
                    		if(!arena.getPlayerData(player).isReady()){
                    			notReady += player + ", ";
                    		}
                    	}
                    	
                    	if (!notReady.isEmpty()){
                    		notReady = notReady.substring(0, notReady.length() - 2);
                    	}
                    	
                    	ctp.sendMessage(p, ChatColor.GREEN + "Thank you for readying. Waiting for " + lobby.countUnreadyPeople() + "/" + lobby.countAllPeople() + " Players Not Ready: "+notReady+"."); // Kj
                    }
                } else { // Game already started
                    if (!arena.getConfigOptions().allowLateJoin) {
                    	ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "A game has already started. You may not join."); // Kj
                        return;
                    }

                    // Player is ready.
                    if (lobby.getPlayersInLobby().get(p.getName())) {
                        if (arena.getConfigOptions().exactTeamMemberCount) {
                            // Uneven number of people and balanced teams is on.  
                            if (arena.getPlayersPlaying().size() % arena.getTeams().size() != 0) {
                            	ctp.getArenaUtil().moveToSpawns(arena, p.getName());
                                return; 
                            } else if (lobby.getPlayersInLobby().get(p.getName())) { // Even number of people and balanced teams is on.  
                                if (arena.getWaitingToMove().size() < arena.getTeams().size() - 1) {
                                    if(arena.getWaitingToMove().contains(p.getName())) {//The player is already in the queue
                                    	ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "There is an even number of players. Please wait or do /ctp leave.");
                                        return;
                                    }
                                    
                                    arena.getWaitingToMove().add(p.getName()); // Player wasn't in the queue, so let's add them
                                    ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "There is an even number of players. Please wait or do /ctp leave."); // Kj
                                } else {
                                    if(arena.getWaitingToMove().contains(p.getName())) {
                                    	ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "There is an even number of players. Please wait or do /ctp leave.");
                                        return;
                                    }
                                    
                                    // Already someone waiting. Queue is cleared.
                                    for(String pl : arena.getWaitingToMove()) {
                                    	ctp.getArenaUtil().moveToSpawns(arena, pl);
                                    }
                                    
                                    ctp.getArenaUtil().moveToSpawns(arena, p.getName());
                                    
                                    arena.getWaitingToMove().clear();
                                } return;
                            }
                        } else {//doesn't have to be exact even teams
                            ctp.getArenaUtil().moveToSpawns(arena, p.getName());
                        }
                    }
                }
            } else {
            	ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "This arena is full."); // Kj
                return;
            }
        }
    }

	@SuppressWarnings("deprecation")
	public void shop(Arena arena, Player p, Sign sign) {
		PlayerData playerdata = arena.getPlayerData(p);
        /* Sign looks like:
         * #######################
         * #        [CTP]        # <-- getLine(0)
         * #  MaterialInt:Amount # <-- getLine(1)
         * #        Price        # <-- getLine(2)
         * #      Team Color     # <-- getLine(3)
         * #######################
         *            #
         *            # 
         */
		
        String teamcolor = sign.getLine(3) == null ? "" : sign.getLine(3);

        // If sign requires team color to buy
        if(!teamcolor.isEmpty()) {
            if (playerdata.getTeam() == null || playerdata.getTeam().getColor() == null) {
                return;
            }

            // Kj -- If player does not match the teamcolour if it is specified.
            if (!teamcolor.isEmpty() && !playerdata.getTeam().getColor().trim().equalsIgnoreCase(teamcolor.trim())) {
            	ctp.sendMessage(p, ChatColor.RED + "You are not on the " + teamcolor.toUpperCase() + " team.");
                return;
            }
        }

        List<Items> list = new LinkedList<Items>();
        list = ctp.getUtil().getItemListFromString(sign.getLine(1));

        if (list.isEmpty() || (list.get(0).getItem() == null) || list.get(0).getItem().equals(Material.AIR)) { // Kj -- changed bracing from != null ... to == null return;
            return;
        }

        String pricestr = sign.getLine(2) == null ? "" : sign.getLine(2);
        int price = 0;

        if (!pricestr.isEmpty()) {
            try {
                price = Integer.parseInt(pricestr); // Get price. 
            } catch (Exception e) {
                price = Integer.MAX_VALUE; // Sign's price is illegal.
            }
        }    

        if (ctp.getMoneyUtil().canPay(p.getName(), price)) {
            int amount = list.get(0).getAmount();
            if (list.get(0).getItem() == Material.ARROW) {
                amount = 64;
            }

            ItemStack stack;
            if (list.get(0).getType() == -1) {
                stack = new ItemStack(list.get(0).getItem(), amount);
            } else {
                stack = new ItemStack(list.get(0).getItem());
                stack.setAmount(amount);
                stack.setDurability(list.get(0).getType());
            }
            
            // Add enchantments
            for(int j = 0; j < list.get(0).getEnchantments().size(); j++) {
                stack.addEnchantment(list.get(0).getEnchantments().get(j), list.get(0).getEnchantmentLevels().get(j));
            }
            
            p.getInventory().addItem(stack);
            ctp.getMoneyUtil().chargeAccount(p.getName(), price);

            ctp.sendMessage(p, "You bought " + ChatColor.AQUA + amount + " " + list.get(0).getItem().toString().toLowerCase() + ChatColor.WHITE + " for " + ChatColor.GREEN + price + ChatColor.WHITE + " money.");
            ctp.sendMessage(p, "You now have " + ChatColor.GREEN + playerdata.getMoney() + ChatColor.WHITE + " money.");
            
            	//It's deprecated but it's currently the only way to get the desired effect.
            	p.updateInventory();
            return;
        } else {
            String message = price != Integer.MAX_VALUE
                    ? "Not enough money! You have " + ChatColor.GREEN + playerdata.getMoney() + ChatColor.WHITE + " money, but you need " + ChatColor.GREEN + price + ChatColor.WHITE + " money."
                    : ChatColor.RED + "This sign does not have a legal price. Please inform an admin.";
            ctp.sendMessage(p, message);
            return;
        }
    }

	/**
	 * Used to check if the player is trying to heal.
	 * 
	 * @param event The PlayerInteractEvent
	 * @param p The Player in which is doing the action.
	 */
    public void useHealingItem(PlayerInteractEvent event, Arena arena, Player p) {
    	//Check if the game is running or not
    	if (!arena.isGameRunning()) return;
    	
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Material mat = p.getItemInHand().getType();
            
            if(ctp.getGlobalConfigOptions().debugMessages) ctp.sendMessage(p, "The material in hand is: " + mat.toString());
            
            for (HealingItems item : ctp.getHealingItems()) {
                if (item.item.getItem() == mat) {
                    PlayersAndCooldowns cooldownData = null;
                    boolean alreadyExists = false;
                    
                    if (item.cooldowns != null && item.cooldowns.size() > 0) {
                        for (String playName : item.cooldowns.keySet()) {
                            if (p.getHealth() >= arena.getConfigOptions().maxPlayerHealth) {
                                p.sendMessage(ChatColor.RED + "You are healthy!");
                                return;
                            }
                            if (playName.equalsIgnoreCase(p.getName()) && item.cooldowns.get(playName).getCooldown() > 0) {
                                p.sendMessage(ChatColor.GREEN + item.item.getItem().toString().toLowerCase() + ChatColor.WHITE + " is on cooldown! Time left: " + ChatColor.GREEN + item.cooldowns.get(playName).getCooldown());
                                return;
                            } else if (playName.equalsIgnoreCase(p.getName())) {
                                cooldownData = item.cooldowns.get(playName);
                                break;
                            }
                        }
                    }
                    
                    if (cooldownData == null) {
                        cooldownData = new PlayersAndCooldowns();
                    } else {
                        alreadyExists = true;
                    }

                    // If we are here item has no cooldown, but it can have HOT ticking, but we do not check that.
                    if (item.cooldown == 0) {
                        cooldownData.setCooldown(-1);
                    } else {
                        cooldownData.setCooldown(item.cooldown);
                    }

                    if (p.getHealth() + item.instantHeal > arena.getConfigOptions().maxPlayerHealth) {
                    	p.setHealth(arena.getConfigOptions().maxPlayerHealth);
                    	p.setFoodLevel(20);
                    	EntityRegainHealthEvent regen = new EntityRegainHealthEvent(p, arena.getConfigOptions().maxPlayerHealth, RegainReason.CUSTOM);
                    	ctp.getPluginManager().callEvent(regen);
                    } else {
                    	p.setHealth(p.getHealth() + item.instantHeal);
                    	p.setFoodLevel(20);
                    	EntityRegainHealthEvent regen = new EntityRegainHealthEvent(p, p.getHealth() + item.instantHeal, RegainReason.CUSTOM);
                    	ctp.getPluginManager().callEvent(regen);
                    }

                    if (item.duration > 0) {
                    	cooldownData.setHealingTimesLeft(item.duration);
                        cooldownData.setIntervalTimeLeft(item.hotInterval);
                    }

                    if (!alreadyExists) {
                        item.cooldowns.put(p.getName(), cooldownData);
                    }

                    if (p.getItemInHand().getAmount() > 1) {
                        p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
                    } else {
                        p.setItemInHand(new ItemStack(Material.AIR));
                    }
                    
                    // Cancel event to not heal like with golden apple
                    event.setCancelled(true);
                    if(ctp.getGlobalConfigOptions().debugMessages)
                       	ctp.getLogger().info("Just cancelled a PlayerInteractEvent because the player just 'consumed' an item.");
                    return;
                }
            }
        }else return;
    }

	@SuppressWarnings("deprecation")
	public void selectTeam(PlayerInteractEvent event, Arena arena, Player p) {
        if(arena.isGameRunning() || !arena.getLobby().getPlayersInLobby().containsKey(p.getName()))
            return;
        
        if (event.hasBlock() && event.getClickedBlock().getType().equals(Material.WOOL)) {
            Block block = event.getClickedBlock();
            BlockState state = block.getState();
            MaterialData data = state.getData();
            String color = ((Wool) data).getColor().toString().toLowerCase();

            int hasThatTeam = -1;
            for(int i = 0; i < arena.getTeams().size(); i++) {
                if(arena.getTeams().get(i).getColor().equals(color)) {
                    hasThatTeam = i;
                    break;
                }
            }
            
            if(hasThatTeam == -1) {
            	ctp.sendMessage(p, ChatColor.RED + "[CTP] This arena does not contain this color -> " + ChatColor.GREEN + color.toUpperCase());
                return;
            }
            
            arena.getPlayerData(p.getName()).setTeam(arena.getTeams().get(hasThatTeam));

            try {
            	arena.getTeams().get(hasThatTeam).setChatColor(ChatColor.valueOf(color.toUpperCase())); // Kj
            } catch (Exception ex) {
            	arena.getTeams().get(hasThatTeam).setChatColor(ChatColor.GREEN);
            }

            arena.getTeams().get(hasThatTeam).addOneMemeberCount();


            DyeColor color1 = DyeColor.valueOf(arena.getPlayerData(p.getName()).getTeam().getColor().toUpperCase());

            ItemStack helmet = new ItemStack(Material.WOOL, 1, color1.getData());
            p.getInventory().setHelmet(helmet);
            
    		//It's deprecated but it's currently the only way to get the desired effect.
    		p.updateInventory();

            ctp.sendMessage(p, "You selected " + ChatColor.GREEN + color.toUpperCase() + ChatColor.WHITE + " team.");
        }
    }
}