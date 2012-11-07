package me.dalton.capturethepoints.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import me.dalton.capturethepoints.ArenaData;
import me.dalton.capturethepoints.CTPPotionEffect;
import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.HealingItems;
import me.dalton.capturethepoints.Lobby;
import me.dalton.capturethepoints.PlayerData;
import me.dalton.capturethepoints.Spawn;
import me.dalton.capturethepoints.Team;
import me.dalton.capturethepoints.Util;
import me.dalton.capturethepoints.beans.Items;
import me.dalton.capturethepoints.beans.PlayersAndCooldowns;
import me.dalton.capturethepoints.beans.Points;
import me.dalton.capturethepoints.commands.PJoinCommand;

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

    public List<Player> waitingToMove = new LinkedList<Player>();

    public CaptureThePointsPlayerListener (CaptureThePoints plugin) {
        this.ctp = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess (PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        
        if (ctp.mainArena != null && ctp.mainArena.co != null && !ctp.mainArena.co.allowCommands) {
            String[] args = event.getMessage().split(" ");
            if (!ctp.canAccess(player, false, new String[] { "ctp.*", "ctp.admin" }) && ctp.isGameRunning() && ctp.playerData.containsKey(player)
                    && !args[0].equalsIgnoreCase("/ctp")) {
            	ctp.sendMessage(player, ChatColor.RED + "You can't use commands while playing!");
                event.setCancelled(true);
                if(ctp.globalConfigOptions.debugMessages)
                	ctp.getLogger().info("Just cancelled a PlayerCommandPreprocessEvent because the player is playing.");
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDropItem (PlayerDropItemEvent event) {
        if (ctp.playerData.containsKey(event.getPlayer())) {
            Player player = event.getPlayer();
            
            //Player in the lobby
            if (ctp.playerData.get(player).isInLobby) {
                event.setCancelled(true);
                if(ctp.globalConfigOptions.debugMessages)
                	ctp.getLogger().info("Just cancelled a PlayerDropItemEvent because the player is in the lobby.");
                ctp.sendMessage(player, ChatColor.RED + "You cannot drop items in the lobby!");
                return;
            }
            if (!ctp.mainArena.co.allowDropItems) {
                event.setCancelled(true);
                if(ctp.globalConfigOptions.debugMessages)
                	ctp.getLogger().info("Just cancelled a PlayerDropItemEvent because you have the config option allowDropItems set to false.");
                ctp.sendMessage(player, ChatColor.RED + "You may not drop items.");
                return;
            }
        }else return;
    }
    
	@EventHandler (priority = EventPriority.HIGHEST)
	public void invClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		
		if (ctp.playerData.containsKey(p)) {
			if (event.getInventory().getName().equalsIgnoreCase("container.crafting") && event.getRawSlot() == 5 && event.getSlotType() == SlotType.ARMOR) {
				ctp.sendMessage(p, ChatColor.RED + "You can't remove your helmet.");
				event.setCancelled(true);
				if(ctp.globalConfigOptions.debugMessages)
                	ctp.getLogger().info("Just cancelled a InventoryClickEvent because the player tried to remove his/her helmet.");
				return;
			}
		}else return;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract (PlayerInteractEvent event){
        if (ctp.mainArena == null) return;
        if (ctp.mainArena.lobby == null) return;

        if (ctp.playerData.containsKey(event.getPlayer())) {
            Player p = event.getPlayer();
            //Potion Throwing In Lobby
            if (ctp.playerData.get(p).isInLobby && p.getItemInHand().getTypeId() == 373){
            	if (event.hasBlock() && !((event.getClickedBlock().getState()) instanceof Sign) && event.getClickedBlock().getTypeId() != 42){
                   event.setCancelled(true);
                   if(ctp.globalConfigOptions.debugMessages)
                   	ctp.getLogger().info("Just cancelled a PlayerInteractEvent because the player tried to throw a potion in the lobby.");
                   ctp.sendMessage(p, ChatColor.RED + "You cannot throw potions in the Lobby!");
                   p.updateInventory();
                   return;
                   
            	}
            	
            	if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
            		if(event.getClickedBlock().getState() instanceof Sign || event.getClickedBlock().getTypeId() == 42){
                        p.updateInventory();
            			event.setCancelled(true);
            			if(ctp.globalConfigOptions.debugMessages)
                           	ctp.getLogger().info("Just cancelled a PlayerInteractEvent because the player tried to throw a potion in the lobby.");
            		}
            	}else if(event.getAction() == Action.RIGHT_CLICK_AIR){
                    event.setCancelled(true);
                    ctp.sendMessage(p, ChatColor.RED + "You cannot throw potions in the Lobby!");
                    if(ctp.globalConfigOptions.debugMessages)
                       	ctp.getLogger().info("Just cancelled a PlayerInteractEvent because the player tried to throw a potion in the lobby.");
                    p.updateInventory();
                    return;
            	}
            	
            }
            
            // Iron block
            if (event.hasBlock() && event.getClickedBlock().getTypeId() == 42) {
                //If this role exists
                if (ctp.roles.containsKey(ctp.playerData.get(p).role)) {
                    if (!ctp.playerData.get(p).isReady) {
                        Util.sendMessageToPlayers(ctp, p, ChatColor.GREEN + p.getName() + ChatColor.WHITE + " is ready.");
                    }
                    ctp.playerData.get(p).isReady = true;
                    ctp.mainArena.lobby.playersinlobby.put(p, true); // Kj
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
                    if (ctp.mainArena.lobby.playersinlobby.containsKey(p)) {
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
                        
                        CTPPotionEffect.removeAllEffectsNew(p);
                        
                        String oldRole = "";
                        
                        if (ctp.playerData.get(p).role != null && !ctp.playerData.get(p).role.isEmpty()){
                        	oldRole = ctp.playerData.get(p).role;
                        }
                        
                        if(!ctp.blockListener.assignRole(p, role.toLowerCase())) // Try to assign new role
                            return;

                        if (ctp.playerData.get(p).role != null && !ctp.playerData.get(p).role.isEmpty() && !oldRole.isEmpty()) {
                            
                        	ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "Changing your role from " + ChatColor.GOLD + oldRole.substring(0, 1).toUpperCase() + oldRole.substring(1).toLowerCase()
                                    + ChatColor.LIGHT_PURPLE + " to " + ChatColor.GOLD + role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase() + ChatColor.LIGHT_PURPLE + ".");
                            ctp.sendMessage(p, "Remember to hit the iron block to ready up!");

                        } else {
                        	ctp.sendMessage(p, ChatColor.GOLD + role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase()
                                    + ChatColor.LIGHT_PURPLE + " selected. Hit the iron block to ready up!");
                        }
                        
                        ctp.playerData.get(p).isReady = false; // Un-ready the player
                        ctp.mainArena.lobby.playersinlobby.put(p, false);
                        return;

                        // Player is in game choosing role.
                    } else if (ctp.playerData.get(p).isInArena) {
                        
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
                            String oldRole = ctp.playerData.get(p).role;
                            ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "Changing your role from " + ChatColor.GOLD + oldRole.substring(0, 1).toUpperCase() + oldRole.substring(1).toLowerCase()
                                    + ChatColor.LIGHT_PURPLE + " to " + ChatColor.GOLD + role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase() + ChatColor.LIGHT_PURPLE + ".");

                            ctp.blockListener.assignRole(p, role.toLowerCase()); // Assign new role
                        } else {
                            if (canPay(p, price)) {
                                chargeAccount(p, price);
                                String oldRole = ctp.playerData.get(p).role;
                                ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "Successfully bought new role for " + ChatColor.GREEN + price + ChatColor.LIGHT_PURPLE + ". "
                                        + "You changed from " + ChatColor.GOLD + oldRole.substring(0, 1).toUpperCase() + oldRole.substring(1).toLowerCase()
                                        + ChatColor.LIGHT_PURPLE + " to " + ChatColor.GOLD + role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase() + ChatColor.LIGHT_PURPLE + ".");
                                ctp.blockListener.assignRole(p, role.toLowerCase()); // Assign new role
                                return;
                            } else {
                                String message =
                                        price != Integer.MAX_VALUE
                                        ? "Not enough money! You have " + ChatColor.GREEN + ctp.playerData.get(p).money + ChatColor.WHITE + " money, but you need " + ChatColor.GREEN + price + ChatColor.WHITE + " money."
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

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove (PlayerMoveEvent event) {
        if (!(ctp.isGameRunning())) return;
        Location loc = event.getTo();
        Player p = event.getPlayer();
        
        // Find if player is in arena
        if (this.ctp.playerData.get(p) != null && !ctp.playerData.get(p).isInLobby) {
            if (ctp.playerData.get(p).moveChecker >= 10) {
                ctp.playerData.get(p).moveChecker = 0;
                if (isInside(loc.getBlockY(), ctp.mainArena.y1, ctp.mainArena.y2) && isInside(loc.getBlockX(), ctp.mainArena.x1, ctp.mainArena.x2) && isInside(loc.getBlockZ(), ctp.mainArena.z1, ctp.mainArena.z2) && loc.getWorld().getName().equalsIgnoreCase(ctp.mainArena.world)) {
                    return;
                } else {
                    String color = ctp.playerData.get(p).team.color;
                    Location loc2 = new Location(ctp.getServer().getWorld(ctp.mainArena.world), ctp.mainArena.teamSpawns.get(color).x, ctp.mainArena.teamSpawns.get(color).y + 1, ctp.mainArena.teamSpawns.get(color).z);
                    loc2.setYaw((float) ctp.mainArena.teamSpawns.get(color).dir);
                    loc2.getWorld().loadChunk(loc2.getBlockX(), loc2.getBlockZ());
                    p.teleport(loc2);
                }
            } else {
                ctp.playerData.get(p).moveChecker++;
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit (PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (this.ctp.playerData.get(player) != null) {
            ctp.leaveGame(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn (PlayerRespawnEvent event) {
    	Player p = event.getPlayer();
    	
        if((this.ctp.playerData.get(p) == null))
            return;

        if(!ctp.isGameRunning() && ctp.playerData.get(p).isInLobby) {
            ctp.playerData.get(p).isInArena = false;
            ctp.playerData.get(p).isInLobby = false;
            ctp.mainArena.lobby.playersinlobby.remove(p);
            event.setRespawnLocation(ctp.previousLocation.get(p));
            ctp.leaveGame(p);
            p.sendMessage(ChatColor.LIGHT_PURPLE + "[CTP] You left the CTP game.");
            return;
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerTeleport (PlayerTeleportEvent event) {
        if (!(ctp.isGameRunning())) {
            if (this.ctp.playerData.get(event.getPlayer()) != null && ctp.playerData.get(event.getPlayer()).isInLobby) {
                if (isInside(event.getTo().getBlockX(), ctp.mainArena.x1, ctp.mainArena.x2) && isInside(event.getTo().getBlockY(), ctp.mainArena.y1, ctp.mainArena.y2) && isInside(event.getTo().getBlockZ(), ctp.mainArena.z1, ctp.mainArena.z2) && event.getTo().getWorld().getName().equalsIgnoreCase(ctp.mainArena.world)) {
                    ctp.playerData.get(event.getPlayer()).justJoined = false;
                    return;
                } else {
                    if (this.ctp.playerData.get(event.getPlayer()).justJoined) { // allowed to teleport
                        this.ctp.playerData.get(event.getPlayer()).justJoined = false;
                        return;
                    } else {
                        event.setCancelled(true);
                        ctp.playerData.get(event.getPlayer()).isInArena = false;
                        ctp.playerData.get(event.getPlayer()).isInLobby = false;
                        ctp.mainArena.lobby.playersinlobby.remove(event.getPlayer());
                        ctp.leaveGame(event.getPlayer());
                        event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "[CTP] You left the CTP game.");
                    }
                }
            }
            return;
        }

        Player play = event.getPlayer();
        if (ctp.playerData.get(play) == null) {
            return;
        }

        //If ctp leave command
        if (ctp.playerData.get(event.getPlayer()).justJoined) {
            ctp.playerData.get(event.getPlayer()).justJoined = false;
            return;
        }

        // Find if player is in arena
        if (ctp.playerData.get(play).isInArena) {
            Spawn playerspawn = ctp.playerData.get(play).team.spawn; // Get the player's spawnpoint
            if (event.getTo().getX() == playerspawn.x && event.getTo().getZ() == playerspawn.z) {
                // The player is going to their spawn.
                return;
            }
            if (isInside(event.getTo().getBlockX(), ctp.mainArena.x1, ctp.mainArena.x2) && isInside(event.getTo().getBlockZ(), ctp.mainArena.z1, ctp.mainArena.z2) && event.getTo().getWorld().getName().equalsIgnoreCase(ctp.mainArena.world)) {
                // The player is teleporting in the arena.
                return;
            } else {
                // The player is teleporting out of the arena!
                event.setCancelled(true);
                if(ctp.globalConfigOptions.debugMessages)
                   	ctp.getLogger().info("Just cancelled a PlayerTeleportEvent because the player tried to teleport out of the arena.");
                ctp.sendMessage(play, ChatColor.RED + "Not allowed to teleport out of the arena!");
            }
        }
    }

    /** Check if the player can afford this price */
    public boolean canPay (Player player, int price) {
        return (price != Integer.MAX_VALUE && ctp.playerData.get(player).money >= price);
    }

    /** Deduct the price from the player's account. Returns boolean whether play had enough funds to do so. */
    public boolean chargeAccount (Player player, int price) {
        if (ctp.playerData.get(player).money >= price) {
            ctp.playerData.get(player).money -= price;
            return true;
        }
        return false;
    }

    // Ideally we want to take out the Player parameter (without losing its purpose, of course).
    /** Check the lobby to see if player[s] can be transferred.  */
    private void checkLobby (Player p) {
        // Kj -- If autostart is turned off, might as well ignore this. However, if a game has started and someone wants to join, that's different.
        if (ctp.mainArena.co.autoStart || !ctp.isPreGame()) {
            Lobby lobby = ctp.mainArena.lobby;
            int readypeople = lobby.countReadyPeople();

            // The maximum number of players must be greater than the players already playing.
            if (ctp.mainArena.maximumPlayers > ctp.mainArena.getPlayersPlaying(ctp).size()) {
                // Game not yet started
                if (ctp.isPreGame()) {
                    if (!lobby.hasUnreadyPeople()) {
                        if (readypeople >= ctp.mainArena.minimumPlayers) {
                            if (readypeople % ctp.mainArena.teams.size() == 0) {  // There may be more than two teams playing. 
                                moveToSpawns();
                            } else {
                                if (ctp.mainArena.co.exactTeamMemberCount) {
                                    if (readypeople / ctp.mainArena.teams.size() >= 1) {
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
                                Util.sendMessageToPlayers(ctp, ChatColor.RED + "Not enough players for a game. Attempting to change arena. [Needed " + ctp.mainArena.minimumPlayers + " players, found " + readypeople + "].");
                                List<Player> transport = new LinkedList<Player>(lobby.playersinlobby.keySet());
                                ctp.blockListener.endGame(true);
                                ctp.chooseSuitableArena(readypeople);
                                for (Player aPlayer : transport) {
                                    PJoinCommand pj = new PJoinCommand(ctp); 
                                    pj.execute(ctp.getServer().getConsoleSender(), Arrays.asList("ctp", "pjoin", aPlayer.getName()));
                                }
                            } else {
                            	//Reseting main Arena back
                            	ctp.mainArena = mainArenaTmp;
                                Util.sendMessageToPlayers(ctp, ChatColor.RED + "Not enough players for a game. No other suitable arenas found. [Needed " + ctp.mainArena.minimumPlayers + " players, found " + readypeople + "].");
                            }
                        }
                    } else {
                    	String notReady = "";
                    	for(Player player: lobby.playersinlobby.keySet()){
                    		
                    		if(!ctp.playerData.get(player).isReady){
                    			notReady+= player.getName()+", ";
                    		}
                    	}
                    	if (!notReady.isEmpty()){
                    		notReady = notReady.substring(0,notReady.length()-2);
                    	}
                    	
                    	ctp.sendMessage(p, ChatColor.GREEN + "Thank you for readying. Waiting for " + lobby.countUnreadyPeople() + "/" + lobby.countAllPeople() + " Players Not Ready: "+notReady+"."); // Kj
                    }
                } else { // Game already started
                    if (!ctp.mainArena.co.allowLateJoin) {
                    	ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "[CTP] A game has already started. You may not join."); // Kj
                        return;
                    }

                    // Player is ready.
                    if (lobby.playersinlobby.get(p)) {
                        if (ctp.mainArena.co.exactTeamMemberCount) {
                            // Uneven number of people and balanced teams is on.  
                            if (ctp.mainArena.getPlayersPlaying(ctp).size() % ctp.mainArena.teams.size() != 0) {
                                moveToSpawns(p);
                                return; 
                            } else if (lobby.playersinlobby.get(p)) { // Even number of people and balanced teams is on.  
                                if (waitingToMove.size() < ctp.mainArena.teams.size() - 1) {
                                    if(waitingToMove.contains(p)) {
                                    	ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "[CTP] There is an even number of players. Please wait or do /ctp leave.");
                                        return;
                                    }
                                    waitingToMove.add(p); // Add to queue
                                    ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "[CTP] There is an even number of players. Please wait or do /ctp leave."); // Kj
                                } else {
                                    if(waitingToMove.contains(p)) {
                                    	ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "[CTP] There is an even number of players. Please wait or do /ctp leave.");
                                        return;
                                    }
                                    
                                    // Already someone waiting. Queue is cleared.
                                    for(Player pl : waitingToMove) {
                                    	moveToSpawns(pl);
                                    }
                                    
                                    moveToSpawns(p);
                                    
                                    waitingToMove.clear();
                                } return;
                            }

                        // Exact player count off. Player can be moved.
                        } else {
                            moveToSpawns(p);
                        }
                    }
                }
            } else {
            	ctp.sendMessage(p, ChatColor.LIGHT_PURPLE + "[CTP] This arena is full."); // Kj
                return;
            }
        }
    }

	@SuppressWarnings("deprecation")
	public void fixHelmet (Player p) {
        PlayerInventory inv = p.getInventory();
        ctp.sendMessage(p, ChatColor.RED + "Do not remove your helmet.");
        DyeColor color1 = DyeColor.valueOf(ctp.playerData.get(p).team.color.toUpperCase());
        ItemStack helmet = new ItemStack(Material.WOOL, 1, (short) color1.getData());

        inv.remove(Material.WOOL);
        p.getInventory().setHelmet(helmet);
        
		//It's deprecated but it's currently the only way to get the desired effect.
		p.updateInventory();
    }

    public boolean isInside (int loc, int first, int second) {
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

    public void moveToSpawns () {
        for (Player player : ctp.playerData.keySet()) {
            moveToSpawns(player);
        }

        //Game settings
        for (Team team : ctp.mainArena.teams) {
            team.controlledPoints = 0;
            team.score = 0;
        }
        
        if ((!ctp.mainArena.co.useScoreGeneration) && (ctp.mainArena.co.pointsToWin > ctp.mainArena.capturePoints.size())) {
            ctp.mainArena.co.pointsToWin = ctp.mainArena.capturePoints.size();
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
                if ((ctp.isGameRunning()) && (!ctp.mainArena.co.useScoreGeneration)) {
                    int maxPoints = -9999;
                    for (Team team : ctp.mainArena.teams) {
                        if (team.controlledPoints > maxPoints) {
                            maxPoints = team.controlledPoints;
                        }
                    }
                    HashMap<String, String> colors = new HashMap<String, String>();

                    for (Team team : ctp.mainArena.teams) {
                        if (team.controlledPoints == maxPoints) {
                            colors.put(team.color, team.color);
                        }
                    }

                    for (Player player : ctp.playerData.keySet()) {
                        if ((ctp.playerData.get(player).isInArena) && (colors.containsKey(ctp.playerData.get(player).team.color))) {
                            ctp.playerData.get(player).winner = true;
                        }
                    }

                    Util.sendMessageToPlayers(ctp, "Time out! " + ChatColor.GREEN + colors.values().toString().toUpperCase().replace(",", " and") + ChatColor.WHITE + " wins!");
                    ctp.CTP_Scheduler.playTimer = 0;
                    ctp.blockListener.endGame(false);
                }
            }

        }, ctp.mainArena.co.playTime * 20 * 60);

        //Money giving and score generation
        ctp.CTP_Scheduler.money_Score = ctp.getServer().getScheduler().scheduleSyncRepeatingTask(ctp, new Runnable() {
            public void run () {
                if (ctp.isGameRunning()) {
                    for (PlayerData data : ctp.playerData.values()) {
                        if (data.isInArena) {
                            data.money += ctp.mainArena.co.moneyEvery30Sec;
                        }
                    }
                    
                    if (ctp.mainArena.co.useScoreGeneration) {
                        for (Team team : ctp.mainArena.teams) {
                            int dublicator = 1;
                            int maxPossiblePointsToCapture = 0;
                            for (Points point : ctp.mainArena.capturePoints) {
                                if(point.getNotAllowedToCaptureTeams() == null || !Util.containsTeam(point.getNotAllowedToCaptureTeams(), team.color))
                                    maxPossiblePointsToCapture++;
                            }

                            if (team.controlledPoints == maxPossiblePointsToCapture && maxPossiblePointsToCapture > 0) {
                                dublicator = ctp.mainArena.co.scoreMyltiplier;
                            }
                            
                            team.score += ctp.mainArena.co.onePointGeneratedScoreEvery30sec * team.controlledPoints * dublicator;
                        }
                    }
                    ctp.blockListener.didSomeoneWin();
                }
            }

        }, 600L, 600L);//30 sec

        //Messages about score
        ctp.CTP_Scheduler.pointMessenger = ctp.getServer().getScheduler().scheduleSyncRepeatingTask(ctp, new Runnable() {
            public void run () {
                if ((ctp.isGameRunning()) && (ctp.mainArena.co.useScoreGeneration)) {
                    String s = "";
                    for (Team team : ctp.mainArena.teams) {
                        s = s + team.chatcolor + team.color.toUpperCase() + ChatColor.WHITE + " score: " + team.score + ChatColor.AQUA + " // "; // Kj -- Added teamcolour
                    }
                    for (Player play : ctp.playerData.keySet()) {
                    	ctp.sendMessage(play, "Max Score: " + ChatColor.GOLD + ctp.mainArena.co.scoreToWin); // Kj -- Green -> Gold
                    	ctp.sendMessage(play, s);
                    }
                }
            }

        }, ctp.mainArena.co.scoreAnnounceTime * 20, ctp.mainArena.co.scoreAnnounceTime * 20);

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
                                    if (ctp.getServer().getPlayer(playName).getHealth() + item.hotHeal > ctp.mainArena.co.maxPlayerHealth) {
                                    	healPlayerAndCallEvent(player, ctp.mainArena.co.maxPlayerHealth);
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
                    for (Player player : ctp.playerData.keySet()) {
                        PlayerInventory inv = player.getInventory();
                        if (!ctp.playerData.get(player).isInArena) {
                            return;
                        }
                        
                        if (inv.getHelmet() != null && (inv.getHelmet().getType() == Material.WOOL)) {
                            return;                            
                        }
                        
                        // We dont want to respawn player who is already dead and in death screen, cause it causes bug
                        if(player.getHealth() <= 0)
                            return;

                        DyeColor color1 = DyeColor.valueOf(ctp.playerData.get(player).team.color.toUpperCase());
                        
                        inv.remove(Material.WOOL);
                        ctp.entityListener.respawnPlayer(player, null);
                        
                        ItemStack helmet = new ItemStack(Material.WOOL, 1, color1.getData());
                        player.getInventory().setHelmet(helmet);
                        
                		//It's deprecated but it's currently the only way to get the desired effect.
                		player.updateInventory();
                    }
                }
            }

        }, 100L, 100L);
    }

	@SuppressWarnings("deprecation")
	public void moveToSpawns (Player player) {
        if(player == null)
            return;


        if (waitingToMove != null && !waitingToMove.isEmpty()) {
            if(waitingToMove.contains(player))
                waitingToMove.remove(player);
        }

        //Assign team
        int smallest = 99999;
        String color = null;
        Team team = null;
        int teamNR = -1;

        if(ctp.playerData.get(player).team == null) {
            for (int i = 0; i < ctp.mainArena.teams.size(); i++) {
                if (ctp.mainArena.teams.get(i).memberCount < smallest) {
                    team = ctp.mainArena.teams.get(i);
                    smallest = team.memberCount;
                    color = team.color;
                    teamNR = i;
                }
            }

            try {
                ctp.mainArena.teams.get(teamNR).chatcolor = ChatColor.valueOf(team.color.toUpperCase()); // Kj
            } catch (Exception ex) {
                ctp.mainArena.teams.get(teamNR).chatcolor = ChatColor.GREEN;
            }

            ctp.mainArena.teams.get(teamNR).memberCount++;

        } else {   // For already selected team
            team = ctp.playerData.get(player).team;
            color = team.color;
        }

        //Give wool
        DyeColor color1 = DyeColor.valueOf(color.toUpperCase());
        ItemStack helmet = new ItemStack(Material.WOOL, 1, color1.getData());
        player.getInventory().setHelmet(helmet);
        if (ctp.mainArena.co.givenWoolNumber != -1) {  // Kj -- if it equals -1, skip the giving of wool.
            ItemStack wool = new ItemStack(Material.WOOL, ctp.mainArena.co.givenWoolNumber, color1.getData());
            player.getInventory().addItem(wool);
        }
        
		//It's deprecated but it's currently the only way to get the desired effect.
		player.updateInventory();
		
        //Move to spawn  
        ctp.playerData.get(player).team = team;

        Spawn spawn =
                ctp.mainArena.teamSpawns.get(ctp.playerData.get(player).team.color) != null
                ? ctp.mainArena.teamSpawns.get(ctp.playerData.get(player).team.color)
                : team.spawn;

        Location loc = new Location(ctp.getServer().getWorld(ctp.mainArena.world), ctp.mainArena.teamSpawns.get(color).x, ctp.mainArena.teamSpawns.get(color).y + 1D, ctp.mainArena.teamSpawns.get(color).z); // Kj -- Y+1
        loc.setYaw((float) ctp.mainArena.teamSpawns.get(color).dir);
        loc.getWorld().loadChunk(loc.getBlockX(), loc.getBlockZ());
        
        boolean teleport = player.teleport(loc);
        if (!teleport) {
            player.teleport(new Location(player.getWorld(), spawn.x, spawn.y, spawn.z, 0.0F, (float) spawn.dir));
        }
        ctp.mainArena.lobby.playersinlobby.remove(player);
        ctp.playerData.get(player).isInLobby = false;
        ctp.playerData.get(player).isInArena = true;
    }

	@SuppressWarnings("deprecation")
	public void shop (Player p, Sign sign) {
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
            if (ctp.playerData.get(p).team == null || ctp.playerData.get(p).team.color == null) {
                return;
            }

            // Kj -- If player does not match the teamcolour if it is specified.
            if (!teamcolor.isEmpty() && !ctp.playerData.get(p).team.color.trim().equalsIgnoreCase(teamcolor.trim())) {
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

        if (canPay(p, price)) {
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
            chargeAccount(p, price);

            ctp.sendMessage(p, "You bought " + ChatColor.AQUA + amount + " " + list.get(0).getItem().toString().toLowerCase() + ChatColor.WHITE + " for " + ChatColor.GREEN + price + ChatColor.WHITE + " money.");
            ctp.sendMessage(p, "You now have " + ChatColor.GREEN + ctp.playerData.get(p).money + ChatColor.WHITE + " money.");
            
            	//It's deprecated but it's currently the only way to get the desired effect.
            	p.updateInventory();
            return;
        } else {
            String message = price != Integer.MAX_VALUE
                    ? "Not enough money! You have " + ChatColor.GREEN + ctp.playerData.get(p).money + ChatColor.WHITE + " money, but you need " + ChatColor.GREEN + price + ChatColor.WHITE + " money."
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
            
            if(ctp.globalConfigOptions.debugMessages) ctp.sendMessage(p, "The material in hand is: " + mat.toString());
            
            for (HealingItems item : ctp.healingItems) {
                if (item.item.getItem() == mat) {
                    PlayersAndCooldowns cooldownData = null;
                    boolean alreadyExists = false;
                    
                    if (item.cooldowns != null && item.cooldowns.size() > 0) {
                        for (String playName : item.cooldowns.keySet()) {
                            if (p.getHealth() >= ctp.mainArena.co.maxPlayerHealth) {
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

                    if (p.getHealth() + item.instantHeal > ctp.mainArena.co.maxPlayerHealth) {
                    	p.setHealth(ctp.mainArena.co.maxPlayerHealth);
                    	EntityRegainHealthEvent regen = new EntityRegainHealthEvent(p, ctp.mainArena.co.maxPlayerHealth, RegainReason.CUSTOM);
                    	CaptureThePoints.pluginManager.callEvent(regen);
                    } else {
                    	p.setHealth(p.getHealth() + item.instantHeal);
                    	EntityRegainHealthEvent regen = new EntityRegainHealthEvent(p, p.getHealth() + item.instantHeal, RegainReason.CUSTOM);
                    	CaptureThePoints.pluginManager.callEvent(regen);
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
                    if(ctp.globalConfigOptions.debugMessages)
                       	ctp.getLogger().info("Just cancelled a PlayerInteractEvent because the player just 'consumed' an item.");
                    return;
                }
            }
        }else return;
    }

	@SuppressWarnings("deprecation")
	public void selectTeam (PlayerInteractEvent event, Player p) {
        if(ctp.isGameRunning() || !ctp.mainArena.lobby.playersinlobby.containsKey(p))
            return;
        
        if (event.hasBlock() && event.getClickedBlock().getType().equals(Material.WOOL)) {
            Block block = event.getClickedBlock();
            BlockState state = block.getState();
            MaterialData data = state.getData();
            String color = ((Wool) data).getColor().toString().toLowerCase();

            int hasThatTeam = -1;
            for(int i = 0; i < ctp.mainArena.teams.size(); i++) {
                if(ctp.mainArena.teams.get(i).color.equals(color)) {
                    hasThatTeam = i;
                    break;
                }
            }
            
            if(hasThatTeam == -1) {
            	ctp.sendMessage(p, ChatColor.RED + "[CTP] This arena does not contain this color -> " + ChatColor.GREEN + color.toUpperCase());
                return;
            }
            
            ctp.playerData.get(p).team = ctp.mainArena.teams.get(hasThatTeam);

            try {
                ctp.mainArena.teams.get(hasThatTeam).chatcolor = ChatColor.valueOf(color.toUpperCase()); // Kj
            } catch (Exception ex) {
                ctp.mainArena.teams.get(hasThatTeam).chatcolor = ChatColor.GREEN;
            }

            ctp.mainArena.teams.get(hasThatTeam).memberCount++;


            DyeColor color1 = DyeColor.valueOf(ctp.playerData.get(p).team.color.toUpperCase());

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
        int optimalPlayerCountInTeam = ctp.mainArena.getPlayersPlaying(ctp).size() / ctp.mainArena.teams.size();
        int[] teamPlayersCount = new int[ctp.mainArena.teams.size()];
        List<Player> playersForBalance = new ArrayList<Player>();

        boolean areEqual = true;
        for(int i = 0; i < ctp.mainArena.teams.size(); i++) {
            teamPlayersCount[i] = ctp.mainArena.teams.get(i).getTeamPlayers(ctp).size();
            if(optimalPlayerCountInTeam != teamPlayersCount[i]) {
                areEqual = false;
            }
        }

        //Teams are equal, no need to balance
        if(areEqual)
            return;

        //Finding which teams are overcrowded.
        for(int i = 0; i < ctp.mainArena.teams.size(); i++) {
           
            List<Player> TeamPlayers = ctp.mainArena.teams.get(i).getTeamPlayers(ctp);
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
            for(int i = 0; i < ctp.mainArena.teams.size(); i++) {
                if(teamPlayersCount[i] - optimalPlayerCountInTeam < 0) {
                    List<Player> playersForRemove = new ArrayList<Player>();
                    for(int j = 0; j < optimalPlayerCountInTeam - teamPlayersCount[i]; j++) {
                        Player p = playersForBalance.get(j);
                        
                        ctp.playerData.get(p).team.memberCount--;
                        Team oldTeam = ctp.playerData.get(p).team;
                        ctp.playerData.get(p).team = null;     // For moveToSpawns team check

                        //Remove Helmet
                        p.getInventory().setHelmet(null);
                        p.getInventory().remove(Material.WOOL);
                        
                		//It's deprecated but it's currently the only way to get the desired effect.
                		p.updateInventory();
                        
                        moveToSpawns(p);
                        playersForRemove.add(p);
                        ctp.sendMessage(p, ctp.playerData.get(p).team.chatcolor + "You" + ChatColor.WHITE + " changed teams from "
                                + oldTeam.chatcolor + oldTeam.color + ChatColor.WHITE + " to "+ ctp.playerData.get(p).team.chatcolor + ctp.playerData.get(p).team.color + ChatColor.WHITE + "! [Team-balancing]");
                    }
                    
                    for(Player p : playersForRemove)
                        playersForBalance.remove(p);
                }
            }
        }

        //If there are not enough players to balance teams
        if(ctp.mainArena.co.exactTeamMemberCount) {
            for(Player p : playersForBalance) {
                // Moving to Lobby
                ctp.playerData.get(p).team.memberCount--;
                ctp.playerData.get(p).team = null;
                ctp.playerData.get(p).isInArena = false;
                ctp.playerData.get(p).isInLobby = true;
                ctp.mainArena.lobby.playersinlobby.put(p, true);
                ctp.playerData.get(p).isReady = true;
                ctp.playerData.get(p).justJoined = true; // Flag for teleport
                ctp.playerData.get(p).lobbyJoinTime = System.currentTimeMillis();
                ctp.playerData.get(p).warnedAboutActivity = false;
                waitingToMove.add(p);

                // Get lobby location and move player to it.
                Location loc = new Location(ctp.getServer().getWorld(ctp.mainArena.world), ctp.mainArena.lobby.x, ctp.mainArena.lobby.y + 1, ctp.mainArena.lobby.z);
                loc.setYaw((float) ctp.mainArena.lobby.dir);
                loc.getWorld().loadChunk(loc.getBlockX(), loc.getBlockZ());
                p.teleport(loc); // Teleport player to lobby
                
                Util.sendMessageToPlayers(ctp, ChatColor.GREEN + p.getName() + ChatColor.WHITE + " was moved to lobby! [Team-balancing]");
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
    	CaptureThePoints.pluginManager.callEvent(regen);
    }
}