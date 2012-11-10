package me.dalton.capturethepoints.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.HealingItems;
import me.dalton.capturethepoints.Util;
import me.dalton.capturethepoints.beans.ArenaData;
import me.dalton.capturethepoints.beans.Items;
import me.dalton.capturethepoints.beans.Lobby;
import me.dalton.capturethepoints.beans.PlayerData;
import me.dalton.capturethepoints.beans.PlayersAndCooldowns;
import me.dalton.capturethepoints.beans.Points;
import me.dalton.capturethepoints.beans.Spawn;
import me.dalton.capturethepoints.beans.Team;
import me.dalton.capturethepoints.commands.PJoinCommand;
import me.dalton.capturethepoints.util.PotionManagement;
import me.dalton.capturethepoints.util.Permissions;

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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;

public class CaptureThePointsPlayerListener implements Listener {
    private final CaptureThePoints ctp;
    public List<String> waitingToMove = new LinkedList<String>();

    public CaptureThePointsPlayerListener (CaptureThePoints plugin) {
        this.ctp = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess (PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        
        if (ctp.mainArena != null && ctp.mainArena.getConfigOptions() != null && !ctp.mainArena.getConfigOptions().allowCommands) {
            String[] args = event.getMessage().split(" ");
            if (!Permissions.canAccess(player, false, new String[] { "ctp.*", "ctp.admin" }) && ctp.isGameRunning() && ctp.playerData.containsKey(player.getName())
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
        if (ctp.playerData.containsKey(event.getPlayer().getName())) {
            Player player = event.getPlayer();
            
            //Player in the lobby
            if (ctp.playerData.get(player.getName()).inLobby()) {
                event.setCancelled(true);
                if(ctp.getGlobalConfigOptions().debugMessages)
                	ctp.getLogger().info("Just cancelled a PlayerDropItemEvent because the player is in the lobby.");
                ctp.sendMessage(player, ChatColor.RED + "You cannot drop items in the lobby!");
                return;
            }
            if (!ctp.mainArena.getConfigOptions().allowDropItems) {
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
		
		if (ctp.playerData.containsKey(p.getName())) {
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
        if (ctp.mainArena == null) return;
        if (ctp.mainArena.getLobby() == null) return;

        if (ctp.playerData.containsKey(event.getPlayer().getName())) {
            Player p = event.getPlayer();
            //Potion Throwing In Lobby
            if (ctp.playerData.get(p.getName()).inLobby() && p.getItemInHand().getTypeId() == 373){
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
                if (ctp.roles.containsKey(ctp.playerData.get(p.getName()).getRole())) {
                    if (!ctp.playerData.get(p.getName()).isReady()) {
                        Util.sendMessageToPlayers(ctp, p, ChatColor.GREEN + p.getName() + ChatColor.WHITE + " is ready.");
                    }
                    ctp.playerData.get(p.getName()).setReady(true);
                    ctp.mainArena.getLobby().getPlayersInLobby().put(p.getName(), true); // Kj
                    checkLobby(p);
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
                    shop(p, sign);
                } else if (!ctp.roles.containsKey(role.toLowerCase()) && !role.equalsIgnoreCase("random")) {
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
                    if (ctp.mainArena.getLobby().getPlayersInLobby().containsKey(p.getName())) {
                        // Kj's
                        if (role.equalsIgnoreCase("random")) {
                            int size = ctp.roles.size();
                            if (size > 1) { // If there is more than 1 role to choose from
                                Random random = new Random();
                                int nextInt = random.nextInt(size); // Generate a random number between 0 (inclusive) -> Number of roles (exclusive)
                                List<String> roles = new LinkedList<String>(ctp.roles.keySet()); // Get a list of available roles and convert to a String List

                                role =
                                        roles.get(nextInt) == null
                                        ? roles.get(0)
                                        : roles.get(nextInt); // Change the role based on the random number. (Ternary null check)
                            }
                        }
                        
                        PotionManagement.removeAllEffects(p);
                        
                        String oldRole = "";
                        
                        if (ctp.playerData.get(p.getName()).getRole() != null && !ctp.playerData.get(p.getName()).getRole().isEmpty()){
                        	oldRole = ctp.playerData.get(p.getName()).getRole();
                        }
                        
                        if(!ctp.blockListener.assignRole(p, role.toLowerCase())) // Try to assign new role
                            return;

                        if (ctp.playerData.get(p.getName()).getRole() != null && !ctp.playerData.get(p.getName()).getRole().isEmpty() && !oldRole.isEmpty()) {
                            
                        	ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "Changing your role from " + ChatColor.GOLD + oldRole.substring(0, 1).toUpperCase() + oldRole.substring(1).toLowerCase()
                                    + ChatColor.LIGHT_PURPLE + " to " + ChatColor.GOLD + role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase() + ChatColor.LIGHT_PURPLE + ".");
                            ctp.sendMessage(p, "Remember to hit the iron block to ready up!");

                        } else {
                        	ctp.sendMessage(p, ChatColor.GOLD + role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase()
                                    + ChatColor.LIGHT_PURPLE + " selected. Hit the iron block to ready up!");
                        }
                        
                        ctp.playerData.get(p.getName()).setReady(false); // Un-ready the player
                        ctp.mainArena.getLobby().getPlayersInLobby().put(p.getName(), false);
                        return;

                        // Player is in game choosing role.
                    } else if (ctp.playerData.get(p.getName()).inArena()) {
                        
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
                            int size = ctp.roles.size();
                            if (size > 1) { // If there is more than 1 role to choose from
                                Random random = new Random();
                                int nextInt = random.nextInt(size); // Generate a random number between 0 (inclusive) -> Number of roles (exclusive)
                                List<String> roles = new LinkedList<String>(ctp.roles.keySet()); // Get a list of available roles and convert to a String List
                                role =
                                        roles.get(nextInt) == null
                                        ? roles.get(0)
                                        : roles.get(nextInt); // Change the role based on the random number. (Ternary null check)
                            }
                        }
                        
                        if (price == 0) {
                            String oldRole = ctp.playerData.get(p.getName()).getRole();
                            ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "Changing your role from " + ChatColor.GOLD + oldRole.substring(0, 1).toUpperCase() + oldRole.substring(1).toLowerCase()
                                    + ChatColor.LIGHT_PURPLE + " to " + ChatColor.GOLD + role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase() + ChatColor.LIGHT_PURPLE + ".");

                            ctp.blockListener.assignRole(p, role.toLowerCase()); // Assign new role
                        } else {
                            if (canPay(p.getName(), price)) {
                                chargeAccount(p.getName(), price);
                                String oldRole = ctp.playerData.get(p.getName()).getRole();
                                ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "Successfully bought new role for " + ChatColor.GREEN + price + ChatColor.LIGHT_PURPLE + ". "
                                        + "You changed from " + ChatColor.GOLD + oldRole.substring(0, 1).toUpperCase() + oldRole.substring(1).toLowerCase()
                                        + ChatColor.LIGHT_PURPLE + " to " + ChatColor.GOLD + role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase() + ChatColor.LIGHT_PURPLE + ".");
                                ctp.blockListener.assignRole(p, role.toLowerCase()); // Assign new role
                                return;
                            } else {
                                String message =
                                        price != Integer.MAX_VALUE
                                        ? "Not enough money! You have " + ChatColor.GREEN + ctp.playerData.get(p.getName()).getMoney() + ChatColor.WHITE + " money, but you need " + ChatColor.GREEN + price + ChatColor.WHITE + " money."
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
            selectTeam(event, p);

            // check for Healing item usage
            useHealingItem(event, p);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!(ctp.isGameRunning())) return;
        Location loc = event.getTo();
        Player p = event.getPlayer();
        
        // Find if player is in arena
        if (this.ctp.playerData.get(p.getName()) != null && !ctp.playerData.get(p.getName()).inLobby()) {
            if (ctp.playerData.get(p.getName()).getMoveChecker() >= 10) {
                ctp.playerData.get(p.getName()).setMoveChecker(0);
                if (isInside(loc.getBlockY(), ctp.mainArena.getY1(), ctp.mainArena.getY2()) && isInside(loc.getBlockX(), ctp.mainArena.getX1(), ctp.mainArena.getX2()) && isInside(loc.getBlockZ(), ctp.mainArena.getZ1(), ctp.mainArena.getZ2()) && loc.getWorld().getName().equalsIgnoreCase(ctp.mainArena.getWorld())) {
                    return;
                } else {
                    String color = ctp.playerData.get(p.getName()).getTeam().getColor();
                    Location loc2 = new Location(ctp.getServer().getWorld(ctp.mainArena.getWorld()), ctp.mainArena.getTeamSpawns().get(color).getX(), ctp.mainArena.getTeamSpawns().get(color).getY() + 1, ctp.mainArena.getTeamSpawns().get(color).getZ());
                    loc2.setYaw((float) ctp.mainArena.getTeamSpawns().get(color).getDir());
                    loc2.getWorld().loadChunk(loc2.getBlockX(), loc2.getBlockZ());
                    p.teleport(loc2);
                }
            } else {
                ctp.playerData.get(p.getName()).addOneMoveChecker();
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit (PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (this.ctp.playerData.get(player.getName()) != null) {
            ctp.leaveGame(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn (PlayerRespawnEvent event) {
    	Player p = event.getPlayer();
    	
        if((this.ctp.playerData.get(p.getName()) == null))
            return;

        if(!ctp.isGameRunning() && ctp.playerData.get(p.getName()).inLobby()) {
            ctp.playerData.get(p.getName()).setInArena(false);
            ctp.playerData.get(p.getName()).setInLobby(false);
            ctp.mainArena.getLobby().getPlayersInLobby().remove(p.getName());
            event.setRespawnLocation(ctp.previousLocation.get(p.getName()));
            ctp.leaveGame(p);
            p.sendMessage(ChatColor.LIGHT_PURPLE + "[CTP] You left the CTP game.");
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport (PlayerTeleportEvent event) {
    	Player play = event.getPlayer();
    	
        if (!(ctp.isGameRunning())) {
            if (this.ctp.playerData.get(play.getName()) != null && ctp.playerData.get(play.getName()).inLobby()) {
                if (isInside(event.getTo().getBlockX(), ctp.mainArena.getX1(), ctp.mainArena.getX2()) && isInside(event.getTo().getBlockY(), ctp.mainArena.getY1(), ctp.mainArena.getY2()) && isInside(event.getTo().getBlockZ(), ctp.mainArena.getZ1(), ctp.mainArena.getZ2()) && event.getTo().getWorld().getName().equalsIgnoreCase(ctp.mainArena.getWorld())) {
                    ctp.playerData.get(play.getName()).setJustJoined(false);
                    return;
                } else {
                    if (this.ctp.playerData.get(play.getName()).getJustJoined()) { // allowed to teleport
                        this.ctp.playerData.get(play.getName()).setJustJoined(false);
                        return;
                    } else {
                        event.setCancelled(true);
                        ctp.playerData.get(play.getName()).setInArena(false);
                        ctp.playerData.get(play.getName()).setInLobby(false);
                        ctp.mainArena.getLobby().getPlayersInLobby().remove(play.getName());
                        ctp.leaveGame(play);
                        ctp.sendMessage(play, ChatColor.LIGHT_PURPLE + "You left the CTP game.");
                    }
                }
            }
            return;
        }

        if (ctp.playerData.get(play.getName()) == null) {
            return;
        }

        //If ctp leave command
        if (ctp.playerData.get(play.getName()).getJustJoined()) {
            ctp.playerData.get(play.getName()).setJustJoined(false);
            return;
        }

        // Find if player is in arena
        if (ctp.playerData.get(play.getName()).inArena()) {
            Spawn playerspawn = ctp.playerData.get(play.getName()).getTeam().getSpawn(); // Get the player's spawnpoint
            if (event.getTo().getX() == playerspawn.getX() && event.getTo().getZ() == playerspawn.getZ()) {
                // The player is going to their spawn.
                return;
            }
            if (isInside(event.getTo().getBlockX(), ctp.mainArena.getX1(), ctp.mainArena.getX2()) && isInside(event.getTo().getBlockZ(), ctp.mainArena.getZ1(), ctp.mainArena.getZ2()) && event.getTo().getWorld().getName().equalsIgnoreCase(ctp.mainArena.getWorld())) {
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

    /** Check if the player can afford this price */
    public boolean canPay(String player, int price) {
        return (price != Integer.MAX_VALUE && ctp.playerData.get(player).getMoney() >= price);
    }

    /** Deduct the price from the player's account. Returns boolean whether play had enough funds to do so. */
    public boolean chargeAccount(String player, int price) {
        if (ctp.playerData.get(player).getMoney() >= price) {
        	ctp.playerData.get(player).setMoney(ctp.playerData.get(player).getMoney() - price);
            return true;
        }
        return false;
    }

    // Ideally we want to take out the Player parameter (without losing its purpose, of course).
    /** Check the lobby to see if player[s] can be transferred.  */
    private void checkLobby(Player p) {
        // Kj -- If autostart is turned off, might as well ignore this. However, if a game has started and someone wants to join, that's different.
        if (ctp.mainArena.getConfigOptions().autoStart || !ctp.isPreGame()) {
            Lobby lobby = ctp.mainArena.getLobby();
            int readypeople = lobby.countReadyPeople();

            // The maximum number of players must be greater than the players already playing.
            if (ctp.mainArena.getMaxPlayers() > ctp.mainArena.getPlayersPlaying(ctp).size()) {
                // Game not yet started
                if (ctp.isPreGame()) {
                    if (!lobby.hasUnreadyPeople()) {
                        if (readypeople >= ctp.mainArena.getMinPlayers()) {
                            if (readypeople % ctp.mainArena.getTeams().size() == 0) {  // There may be more than two teams playing. 
                                moveToSpawns();
                            } else {
                                if (ctp.mainArena.getConfigOptions().exactTeamMemberCount) {
                                    if (readypeople / ctp.mainArena.getTeams().size() >= 1) {
                                        moveToSpawns();
                                    } else {
                                    	ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "[CTP] There are already an even number of players. Please wait for a new player to ready up."); // Kj
                                        return;
                                    }
                                } else {   // Does not require exact count and everyone is ready. Move them.
                                    moveToSpawns();
                                }
                            }
                        } 
                        else {
                            //Save variable for minor bug that results from player error
                            ArenaData mainArenaTmp = ctp.mainArena;
                            if (ctp.hasSuitableArena(readypeople)) {
                                Util.sendMessageToPlayers(ctp, ChatColor.RED + "Not enough players for a game. Attempting to change arena. [Needed " + ctp.mainArena.getMinPlayers() + " players, found " + readypeople + "].");
                                List<String> transport = new LinkedList<String>(lobby.getPlayersInLobby().keySet());
                                ctp.blockListener.endGame(true);
                                ctp.chooseSuitableArena(readypeople);
                                for (String aPlayer : transport) {
                                    PJoinCommand pj = new PJoinCommand(ctp); 
                                    pj.execute(ctp.getServer().getConsoleSender(), Arrays.asList("ctp", "pjoin", aPlayer));
                                }
                            } else {
                            	//Reseting main Arena back
                            	ctp.mainArena = mainArenaTmp;
                                Util.sendMessageToPlayers(ctp, ChatColor.RED + "Not enough players for a game. No other suitable arenas found. [Needed " + ctp.mainArena.getMinPlayers() + " players, found " + readypeople + "].");
                            }
                        }
                    } else {
                    	String notReady = "";
                    	for(String player : lobby.getPlayersInLobby().keySet()){
                    		
                    		if(!ctp.playerData.get(player).isReady()){
                    			notReady += player + ", ";
                    		}
                    	}
                    	if (!notReady.isEmpty()){
                    		notReady = notReady.substring(0, notReady.length() - 2);
                    	}
                    	
                    	ctp.sendMessage(p, ChatColor.GREEN + "Thank you for readying. Waiting for " + lobby.countUnreadyPeople() + "/" + lobby.countAllPeople() + " Players Not Ready: "+notReady+"."); // Kj
                    }
                } else { // Game already started
                    if (!ctp.mainArena.getConfigOptions().allowLateJoin) {
                    	ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "[CTP] A game has already started. You may not join."); // Kj
                        return;
                    }

                    // Player is ready.
                    if (lobby.getPlayersInLobby().get(p.getName())) {
                        if (ctp.mainArena.getConfigOptions().exactTeamMemberCount) {
                            // Uneven number of people and balanced teams is on.  
                            if (ctp.mainArena.getPlayersPlaying(ctp).size() % ctp.mainArena.getTeams().size() != 0) {
                                moveToSpawns(p.getName());
                                return; 
                            } else if (lobby.getPlayersInLobby().get(p.getName())) { // Even number of people and balanced teams is on.  
                                if (waitingToMove.size() < ctp.mainArena.getTeams().size() - 1) {
                                    if(waitingToMove.contains(p.getName())) {
                                    	ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "[CTP] There is an even number of players. Please wait or do /ctp leave.");
                                        return;
                                    }
                                    waitingToMove.add(p.getName()); // Add to queue
                                    ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "[CTP] There is an even number of players. Please wait or do /ctp leave."); // Kj
                                } else {
                                    if(waitingToMove.contains(p.getName())) {
                                    	ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "[CTP] There is an even number of players. Please wait or do /ctp leave.");
                                        return;
                                    }
                                    
                                    // Already someone waiting. Queue is cleared.
                                    for(String pl : waitingToMove) {
                                    	moveToSpawns(pl);
                                    }
                                    
                                    moveToSpawns(p.getName());
                                    
                                    waitingToMove.clear();
                                } return;
                            }

                        // Exact player count off. Player can be moved.
                        } else {
                            moveToSpawns(p.getName());
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
	public void fixHelmet(Player p) {
        PlayerInventory inv = p.getInventory();
        ctp.sendMessage(p, ChatColor.RED + "Do not remove your helmet.");
        DyeColor color1 = DyeColor.valueOf(ctp.playerData.get(p.getName()).getTeam().getColor().toUpperCase());
        ItemStack helmet = new ItemStack(Material.WOOL, 1, (short) color1.getData());

        inv.remove(Material.WOOL);
        p.getInventory().setHelmet(helmet);
        
		//It's deprecated but it's currently the only way to get the desired effect.
		p.updateInventory();
    }

    public boolean isInside(int loc, int first, int second) {
        int point1 = 0;
        int point2 = 0;
        if (first < second) {
            point1 = first;
            point2 = second;
        } else {
            point2 = first;
            point1 = second;
        }

        return (point1 <= loc) && (loc <= point2);
    }

    public void moveToSpawns() {
        for (String player : ctp.playerData.keySet()) {
            moveToSpawns(player);
        }

        //Game settings
        for (Team team : ctp.mainArena.getTeams()) {
            team.setControlledPoints(0);
            team.setScore(0);
        }
        
        if ((!ctp.mainArena.getConfigOptions().useScoreGeneration) && (ctp.mainArena.getConfigOptions().pointsToWin > ctp.mainArena.getCapturePoints().size())) {
            ctp.mainArena.getConfigOptions().pointsToWin = ctp.mainArena.getCapturePoints().size();
        }

        // Balance teams for already selected teams
        balanceTeamsFromLobby();

        ctp.blockListener.capturegame = true;
        ctp.getServer().broadcastMessage(ChatColor.AQUA+"[CTP]"+ChatColor.WHITE+" A Capture The Points game has started!");
        ctp.blockListener.preGame = false;
        ctp.blockListener.didSomeoneWin();

        // Play time for points only
        ctp.CTP_Scheduler.playTimer = ctp.getServer().getScheduler().scheduleSyncDelayedTask(ctp, new Runnable() {
            public void run () {
                if ((ctp.isGameRunning()) && (!ctp.mainArena.getConfigOptions().useScoreGeneration)) {
                    int maxPoints = -9999;
                    for (Team team : ctp.mainArena.getTeams()) {
                        if (team.getControlledPoints() > maxPoints) {
                            maxPoints = team.getControlledPoints();
                        }
                    }
                    HashMap<String, String> colors = new HashMap<String, String>();

                    for (Team team : ctp.mainArena.getTeams()) {
                        if (team.getControlledPoints() == maxPoints) {
                            colors.put(team.getColor(), team.getColor());
                        }
                    }

                    for (String player : ctp.playerData.keySet()) {
                        if ((ctp.playerData.get(player).inArena()) && (colors.containsKey(ctp.playerData.get(player).getTeam().getColor()))) {
                            ctp.playerData.get(player).setWinner(true);
                        }
                    }

                    Util.sendMessageToPlayers(ctp, "Time out! " + ChatColor.GREEN + colors.values().toString().toUpperCase().replace(",", " and") + ChatColor.WHITE + " wins!");
                    ctp.CTP_Scheduler.playTimer = 0;
                    ctp.blockListener.endGame(false);
                }
            }

        }, ctp.mainArena.getConfigOptions().playTime * 20 * 60);

        //Money giving and score generation
        ctp.CTP_Scheduler.money_Score = ctp.getServer().getScheduler().scheduleSyncRepeatingTask(ctp, new Runnable() {
            public void run () {
                if (ctp.isGameRunning()) {
                    for (PlayerData data : ctp.playerData.values()) {
                        if (data.inArena()) {
                            data.setMoney(data.getMoney() + ctp.mainArena.getConfigOptions().moneyEvery30Sec);
                        }
                    }
                    
                    if (ctp.mainArena.getConfigOptions().useScoreGeneration) {
                        for (Team team : ctp.mainArena.getTeams()) {
                            int dublicator = 1;
                            int maxPossiblePointsToCapture = 0;
                            for (Points point : ctp.mainArena.getCapturePoints()) {
                                if(point.getNotAllowedToCaptureTeams() == null || !Util.containsTeam(point.getNotAllowedToCaptureTeams(), team.getColor()))
                                    maxPossiblePointsToCapture++;
                            }

                            if (team.getControlledPoints() == maxPossiblePointsToCapture && maxPossiblePointsToCapture > 0) {
                                dublicator = ctp.mainArena.getConfigOptions().scoreMyltiplier;
                            }
                            
                            team.setScore(team.getScore() + (ctp.mainArena.getConfigOptions().onePointGeneratedScoreEvery30sec * team.getControlledPoints() * dublicator));
                        }
                    }
                    ctp.blockListener.didSomeoneWin();
                }
            }

        }, 600L, 600L);//30 sec

        //Messages about score
        ctp.CTP_Scheduler.pointMessenger = ctp.getServer().getScheduler().scheduleSyncRepeatingTask(ctp, new Runnable() {
            public void run () {
                if ((ctp.isGameRunning()) && (ctp.mainArena.getConfigOptions().useScoreGeneration)) {
                    String s = "";
                    for (Team team : ctp.mainArena.getTeams()) {
                        s = s + team.getChatColor() + team.getColor().toUpperCase() + ChatColor.WHITE + " score: " + team.getScore() + ChatColor.AQUA + " // "; // Kj -- Added teamcolour
                    }
                    for (String player : ctp.playerData.keySet()) {
                    	Player p = ctp.getServer().getPlayer(player);
                    	ctp.sendMessage(p, "Max Score: " + ChatColor.GOLD + ctp.mainArena.getConfigOptions().scoreToWin); // Kj -- Green -> Gold
                    	ctp.sendMessage(p, s);
                    }
                }
            }

        }, ctp.mainArena.getConfigOptions().scoreAnnounceTime * 20, ctp.mainArena.getConfigOptions().scoreAnnounceTime * 20);

        // Healing items cooldowns
        ctp.CTP_Scheduler.healingItemsCooldowns = ctp.getServer().getScheduler().scheduleSyncRepeatingTask(ctp, new Runnable() {
            public void run () {
                if (ctp.isGameRunning()) {
                    for (HealingItems item : ctp.healingItems) {
                        if (item != null && item.cooldowns != null && item.cooldowns.size() > 0) {
                            for (String playName : item.cooldowns.keySet()) {
                                PlayersAndCooldowns data = item.cooldowns.get(playName);
                                Player player = ctp.getServer().getPlayer(playName);
                                if (data.getCooldown() == 1) {// This is cause we begin from top
                                	player.sendMessage(ChatColor.GREEN + item.item.getItem().toString().toLowerCase() + ChatColor.WHITE + " cooldown has refreshed!");
                                }

                                if (data.getHealingTimesLeft() > 0 && data.getIntervalTimeLeft() <= 0) {
                                    if (ctp.getServer().getPlayer(playName).getHealth() + item.hotHeal > ctp.mainArena.getConfigOptions().maxPlayerHealth) {
                                    	healPlayerAndCallEvent(player, ctp.mainArena.getConfigOptions().maxPlayerHealth);
                                    } else {
                                    	healPlayerAndCallEvent(player, player.getHealth() + item.hotHeal);
                                    }
                                    data.setIntervalTimeLeft(item.hotInterval);
                                    data.setHealingTimesLeft(data.getHealingTimesLeft() - 1);
                                }
                                //ctp.getServer().getPlayer(playName).sendMessage(ChatColor.GREEN + item.item.item.toString() + ChatColor.WHITE + " cooldown: " + data.cooldown);
                                data.setIntervalTimeLeft(data.getIntervalTimeLeft() - 1);
                                data.setCooldown(data.getCooldown() - 1);

                                if (data.getCooldown() <= 0 && data.getHealingTimesLeft() <= 0) {
                                    item.cooldowns.remove(playName);
                                }
                            }
                        }
                    }
                }
            }

        }, 20L, 20L); // Every second (one)

        //Helmet Checking
        ctp.CTP_Scheduler.helmChecker = ctp.getServer().getScheduler().scheduleSyncRepeatingTask(ctp, new Runnable() {
            @SuppressWarnings("deprecation")
			public void run () {
                if (ctp.isGameRunning()) {
                    for (String player : ctp.playerData.keySet()) {
                    	Player p = ctp.getServer().getPlayer(player);
                        PlayerInventory inv = p.getInventory();
                        if (!ctp.playerData.get(player).inArena()) {
                            return;
                        }
                        
                        if (inv.getHelmet() != null && (inv.getHelmet().getType() == Material.WOOL)) {
                            return;                            
                        }
                        
                        // We dont want to respawn player who is already dead and in death screen, cause it causes bug
                        if(p.getHealth() <= 0)
                            return;

                        DyeColor color1 = DyeColor.valueOf(ctp.playerData.get(player).getTeam().getColor().toUpperCase());
                        
                        inv.remove(Material.WOOL);
                        ctp.entityListener.respawnPlayer(p, null);
                        
                        ItemStack helmet = new ItemStack(Material.WOOL, 1, color1.getData());
                        p.getInventory().setHelmet(helmet);
                        
                		//It's deprecated but it's currently the only way to get the desired effect.
                		p.updateInventory();
                    }
                }
            }

        }, 100L, 100L);
    }

	@SuppressWarnings("deprecation")
	public void moveToSpawns(String pl) {
        if(pl == null)
            return;


        if (waitingToMove != null && !waitingToMove.isEmpty()) {
            if(waitingToMove.contains(pl))
                waitingToMove.remove(pl);
        }

        //Assign team
        int smallest = 99999;
        String color = null;
        Team team = null;
        int teamNR = -1;

        if(ctp.playerData.get(pl).getTeam() == null) {
            for (int i = 0; i < ctp.mainArena.getTeams().size(); i++) {
                if (ctp.mainArena.getTeams().get(i).getMemberCount() < smallest) {
                    team = ctp.mainArena.getTeams().get(i);
                    smallest = team.getMemberCount();
                    color = team.getColor();
                    teamNR = i;
                }
            }

            try {
                ctp.mainArena.getTeams().get(teamNR).setChatColor(ChatColor.valueOf(team.getColor().toUpperCase())); // Kj
            } catch (Exception ex) {
                ctp.mainArena.getTeams().get(teamNR).setChatColor(ChatColor.GREEN);
            }

            ctp.mainArena.getTeams().get(teamNR).addOneMemeberCount();

        } else {   // For already selected team
            team = ctp.playerData.get(pl).getTeam();
            color = team.getColor();
        }

        Player p = ctp.getServer().getPlayer(pl);
        
        //Give wool
        DyeColor color1 = DyeColor.valueOf(color.toUpperCase());
        ItemStack helmet = new ItemStack(Material.WOOL, 1, color1.getData());
        p.getInventory().setHelmet(helmet);
        if (ctp.mainArena.getConfigOptions().givenWoolNumber != -1) {  // Kj -- if it equals -1, skip the giving of wool.
            ItemStack wool = new ItemStack(Material.WOOL, ctp.mainArena.getConfigOptions().givenWoolNumber, color1.getData());
            p.getInventory().addItem(wool);
        }
        
		//It's deprecated but it's currently the only way to get the desired effect.
		p.updateInventory();
		
        //Move to spawn  
        ctp.playerData.get(pl).setTeam(team);

        Spawn spawn =
                ctp.mainArena.getTeamSpawns().get(ctp.playerData.get(pl).getTeam().getColor()) != null
                ? ctp.mainArena.getTeamSpawns().get(ctp.playerData.get(pl).getTeam().getColor())
                : team.getSpawn();

        Location loc = new Location(ctp.getServer().getWorld(ctp.mainArena.getWorld()), ctp.mainArena.getTeamSpawns().get(color).getX(), ctp.mainArena.getTeamSpawns().get(color).getY() + 1D, ctp.mainArena.getTeamSpawns().get(color).getZ()); // Kj -- Y+1
        loc.setYaw((float) ctp.mainArena.getTeamSpawns().get(color).getDir());
        loc.getWorld().loadChunk(loc.getBlockX(), loc.getBlockZ());
        
        boolean teleport = p.teleport(loc);
        if (!teleport) {
            p.teleport(new Location(p.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ(), 0.0F, (float) spawn.getDir()));
        }
        ctp.mainArena.getLobby().getPlayersInLobby().remove(pl);
        ctp.playerData.get(pl).setInLobby(false);
        ctp.playerData.get(pl).setInArena(true);
    }

	@SuppressWarnings("deprecation")
	public void shop(Player p, Sign sign) {
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
            if (ctp.playerData.get(p.getName()).getTeam() == null || ctp.playerData.get(p.getName()).getTeam().getColor() == null) {
                return;
            }

            // Kj -- If player does not match the teamcolour if it is specified.
            if (!teamcolor.isEmpty() && !ctp.playerData.get(p.getName()).getTeam().getColor().trim().equalsIgnoreCase(teamcolor.trim())) {
            	ctp.sendMessage(p, ChatColor.RED + "You are not on the " + teamcolor.toUpperCase() + " team.");
                return;
            }
        }

        List<Items> list = new LinkedList<Items>();
        list = Util.getItemListFromString(sign.getLine(1));

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

        if (canPay(p.getName(), price)) {
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
            chargeAccount(p.getName(), price);

            ctp.sendMessage(p, "You bought " + ChatColor.AQUA + amount + " " + list.get(0).getItem().toString().toLowerCase() + ChatColor.WHITE + " for " + ChatColor.GREEN + price + ChatColor.WHITE + " money.");
            ctp.sendMessage(p, "You now have " + ChatColor.GREEN + ctp.playerData.get(p.getName()).getMoney() + ChatColor.WHITE + " money.");
            
            	//It's deprecated but it's currently the only way to get the desired effect.
            	p.updateInventory();
            return;
        } else {
            String message = price != Integer.MAX_VALUE
                    ? "Not enough money! You have " + ChatColor.GREEN + ctp.playerData.get(p.getName()).getMoney() + ChatColor.WHITE + " money, but you need " + ChatColor.GREEN + price + ChatColor.WHITE + " money."
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
    public void useHealingItem (PlayerInteractEvent event, Player p) {
    	//Check if the game is running or not
    	if (!ctp.isGameRunning()) return;
    	
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Material mat = p.getItemInHand().getType();
            
            if(ctp.getGlobalConfigOptions().debugMessages) ctp.sendMessage(p, "The material in hand is: " + mat.toString());
            
            for (HealingItems item : ctp.healingItems) {
                if (item.item.getItem() == mat) {
                    PlayersAndCooldowns cooldownData = null;
                    boolean alreadyExists = false;
                    
                    if (item.cooldowns != null && item.cooldowns.size() > 0) {
                        for (String playName : item.cooldowns.keySet()) {
                            if (p.getHealth() >= ctp.mainArena.getConfigOptions().maxPlayerHealth) {
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

                    if (p.getHealth() + item.instantHeal > ctp.mainArena.getConfigOptions().maxPlayerHealth) {
                    	p.setHealth(ctp.mainArena.getConfigOptions().maxPlayerHealth);
                    	p.setFoodLevel(20);
                    	EntityRegainHealthEvent regen = new EntityRegainHealthEvent(p, ctp.mainArena.getConfigOptions().maxPlayerHealth, RegainReason.CUSTOM);
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
	public void selectTeam (PlayerInteractEvent event, Player p) {
        if(ctp.isGameRunning() || !ctp.mainArena.getLobby().getPlayersInLobby().containsKey(p.getName()))
            return;
        
        if (event.hasBlock() && event.getClickedBlock().getType().equals(Material.WOOL)) {
            Block block = event.getClickedBlock();
            BlockState state = block.getState();
            MaterialData data = state.getData();
            String color = ((Wool) data).getColor().toString().toLowerCase();

            int hasThatTeam = -1;
            for(int i = 0; i < ctp.mainArena.getTeams().size(); i++) {
                if(ctp.mainArena.getTeams().get(i).getColor().equals(color)) {
                    hasThatTeam = i;
                    break;
                }
            }
            
            if(hasThatTeam == -1) {
            	ctp.sendMessage(p, ChatColor.RED + "[CTP] This arena does not contain this color -> " + ChatColor.GREEN + color.toUpperCase());
                return;
            }
            
            ctp.playerData.get(p.getName()).setTeam(ctp.mainArena.getTeams().get(hasThatTeam));

            try {
                ctp.mainArena.getTeams().get(hasThatTeam).setChatColor(ChatColor.valueOf(color.toUpperCase())); // Kj
            } catch (Exception ex) {
                ctp.mainArena.getTeams().get(hasThatTeam).setChatColor(ChatColor.GREEN);
            }

            ctp.mainArena.getTeams().get(hasThatTeam).addOneMemeberCount();


            DyeColor color1 = DyeColor.valueOf(ctp.playerData.get(p.getName()).getTeam().getColor().toUpperCase());

            ItemStack helmet = new ItemStack(Material.WOOL, 1, color1.getData());
            p.getInventory().setHelmet(helmet);
            
    		//It's deprecated but it's currently the only way to get the desired effect.
    		p.updateInventory();

            ctp.sendMessage(p, "You selected " + ChatColor.GREEN + color.toUpperCase() + ChatColor.WHITE + " team.");
        }
    }

	@SuppressWarnings("deprecation")
	private void balanceTeamsFromLobby() {
        int difference = 0;
        int optimalPlayerCountInTeam = ctp.mainArena.getPlayersPlaying(ctp).size() / ctp.mainArena.getTeams().size();
        int[] teamPlayersCount = new int[ctp.mainArena.getTeams().size()];
        List<String> playersForBalance = new ArrayList<String>();

        boolean areEqual = true;
        for(int i = 0; i < ctp.mainArena.getTeams().size(); i++) {
            teamPlayersCount[i] = ctp.mainArena.getTeams().get(i).getTeamPlayers(ctp).size();
            if(optimalPlayerCountInTeam != teamPlayersCount[i]) {
                areEqual = false;
            }
        }

        //Teams are equal, no need to balance
        if(areEqual)
            return;

        //Finding which teams are overcrowded.
        for(int i = 0; i < ctp.mainArena.getTeams().size(); i++) {
           
            List<String> TeamPlayers = ctp.mainArena.getTeams().get(i).getTeamPlayers(ctp);
                // Randam ir sudedam i sarasa zaidejus, kuriu yra per daug
                for(int j = 0; j < teamPlayersCount[i] - optimalPlayerCountInTeam; j++) {
                    playersForBalance.add(TeamPlayers.get(j));
                }
            if(teamPlayersCount[i] - optimalPlayerCountInTeam < 0) {
                difference = difference + (optimalPlayerCountInTeam - teamPlayersCount[i]);
            }
        }

        // If there are enough players to balance teams
        if(difference <= playersForBalance.size() && difference > 0) {
            for(int i = 0; i < ctp.mainArena.getTeams().size(); i++) {
                if(teamPlayersCount[i] - optimalPlayerCountInTeam < 0) {
                    List<Player> playersForRemove = new ArrayList<Player>();
                    for(int j = 0; j < optimalPlayerCountInTeam - teamPlayersCount[i]; j++) {
                    	
                        Player p = ctp.getServer().getPlayer(playersForBalance.get(j));
                        
                        ctp.playerData.get(p.getName()).getTeam().substractOneMemeberCount();
                        Team oldTeam = ctp.playerData.get(p.getName()).getTeam();
                        ctp.playerData.get(p.getName()).setTeam(null);     // For moveToSpawns team check

                        //Remove Helmet
                        p.getInventory().setHelmet(null);
                        p.getInventory().remove(Material.WOOL);
                        
                		//It's deprecated but it's currently the only way to get the desired effect.
                		p.updateInventory();
                        
                        moveToSpawns(p.getName());
                        playersForRemove.add(p);
                        ctp.sendMessage(p, ctp.playerData.get(p.getName()).getTeam().getChatColor() + "You" + ChatColor.WHITE + " changed teams from "
                                + oldTeam.getChatColor() + oldTeam.getColor() + ChatColor.WHITE + " to "+ ctp.playerData.get(p.getName()).getTeam().getChatColor() + ctp.playerData.get(p.getName()).getTeam().getColor() + ChatColor.WHITE + "! [Team-balancing]");
                    }
                    
                    for(Player p : playersForRemove)
                        playersForBalance.remove(p);
                }
            }
        }

        //If there are not enough players to balance teams
        if(ctp.mainArena.getConfigOptions().exactTeamMemberCount) {
            for(String p : playersForBalance) {
                // Moving to Lobby
                ctp.playerData.get(p).getTeam().substractOneMemeberCount();
                ctp.playerData.get(p).setTeam(null);
                ctp.playerData.get(p).setInArena(false);
                ctp.playerData.get(p).setInLobby(true);
                ctp.mainArena.getLobby().getPlayersInLobby().put(p, true);
                ctp.playerData.get(p).setReady(true);
                ctp.playerData.get(p).setJustJoined(true); // Flag for teleport
                ctp.playerData.get(p).setLobbyJoinTime(System.currentTimeMillis());
                ctp.playerData.get(p).isWarned(false);
                waitingToMove.add(p);

                // Get lobby location and move player to it.
                Location loc = new Location(ctp.getServer().getWorld(ctp.mainArena.getWorld()), ctp.mainArena.getLobby().getX(), ctp.mainArena.getLobby().getY() + 1, ctp.mainArena.getLobby().getZ());
                loc.setYaw((float) ctp.mainArena.getLobby().getDir());
                loc.getWorld().loadChunk(loc.getBlockX(), loc.getBlockZ());
                ctp.getServer().getPlayer(p).teleport(loc); // Teleport player to lobby
                
                Util.sendMessageToPlayers(ctp, ChatColor.GREEN + p + ChatColor.WHITE + " was moved to lobby! [Team-balancing]");
            }
        }
    }

    public void clearWaitingQueue() {
        if (waitingToMove != null) {
            waitingToMove.clear();
        }
    }
    
    /**
     * Heal the player (set the health) and cause an event to happen from it, thus improving relations with other plugins.
     * 
     * @param player The player to heal.
     * @param amount The amount to heal the player.
     */
    public void healPlayerAndCallEvent(Player player, int amount) {
    	player.setHealth(amount);
    	EntityRegainHealthEvent regen = new EntityRegainHealthEvent(player, amount, RegainReason.CUSTOM);
    	ctp.getPluginManager().callEvent(regen);
    }
}