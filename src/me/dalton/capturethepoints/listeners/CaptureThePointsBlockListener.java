package me.dalton.capturethepoints.listeners;
import java.util.ArrayList;
import java.util.List;
import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.HealingItems;
import me.dalton.capturethepoints.Util;
import me.dalton.capturethepoints.beans.ArenaBoundaries;
import me.dalton.capturethepoints.beans.Items;
import me.dalton.capturethepoints.beans.Points;
import me.dalton.capturethepoints.beans.Team;
import me.dalton.capturethepoints.util.PotionManagement;
import me.dalton.capturethepoints.util.InvManagement;
import me.dalton.capturethepoints.util.Permissions;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;

public class CaptureThePointsBlockListener implements Listener {
    private final CaptureThePoints ctp;

    public boolean capturegame = false;

    public boolean preGame = true;

    public CaptureThePointsBlockListener (CaptureThePoints ctp) {
        this.ctp = ctp;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // If it tries to break in lobby
        if (ctp.playerData.containsKey(player.getName()) && ctp.playerData.get(player.getName()).inLobby()) {
            // breaks block beneath player(it causes teleport event if you cancel action)
            int playerLocX = player.getLocation().getBlockX();
            int playerLocY = player.getLocation().getBlockY() - 1;
            int playerLocZ = player.getLocation().getBlockZ();

            if (playerLocX == block.getX() && playerLocY == block.getY() && playerLocZ == block.getZ()) {
                // allow teleport
                ctp.playerData.get(player.getName()).setJustJoined(true);
                ctp.playerNameForTeleport = player.getName();

                // player can not drop down so we need to reset teleport flag
                ctp.getServer().getScheduler().scheduleSyncDelayedTask(ctp, new Runnable() {
                    public void run () {
                        if (!ctp.playerNameForTeleport.isEmpty()) {
                            ctp.playerData.get(ctp.playerNameForTeleport).setJustJoined(false);
                            ctp.playerNameForTeleport = "";
                        }
                    }

                }, 5L);  //I think one second is too much and can cause some troubles if player break another block
            }
            event.setCancelled(true);
            if(ctp.getGlobalConfigOptions().debugMessages)
            	ctp.getLogger().info("Just cancelled a BlockBreakEvent because the player tried to break a block in the Lobby.");
            return;
        }

        
        if (!ctp.playerData.containsKey(player.getName())) { // If tries to break arena blocks out of game
        
            for(ArenaBoundaries bound : ctp.arenasBoundaries.values()){
                if (ctp.playerListener.isInside(block.getLocation().getBlockX(), bound.getx1(), bound.getx2()) && ctp.playerListener.isInside(block.getLocation().getBlockY(), bound.gety1(), bound.gety2()) && ctp.playerListener.isInside(block.getLocation().getBlockZ(), bound.getz1(), bound.getz2()) && block.getLocation().getWorld().getName().equalsIgnoreCase(bound.getWorld())) {
                    if (Permissions.canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.canModify"})) {
                        return; // Player can edit arena
                    }

                    player.sendMessage(ChatColor.RED + "You do not have permission to do that.");
                    event.setCancelled(true);
                    if(ctp.getGlobalConfigOptions().debugMessages)
                    	ctp.getLogger().info("Just cancelled a BlockBreakEvent because the player tried to break a block that was the arena but the player wasn't playing.");
                    return;
                }
            }
            return;
        }

        if (!ctp.isGameRunning()) {
            return;
        }

        BlockState state = block.getState();
        MaterialData data = state.getData();
        if (isAlreadyInGame(player.getName())) {
            // check for sign destroy
            if (state instanceof Sign) {
                event.setCancelled(true);
                if(ctp.getGlobalConfigOptions().debugMessages)
                	ctp.getLogger().info("Just cancelled a BlockBreakEvent because the player tried to break a sign while playing the game.");
                return;
            }
            
            boolean inPoint = false; // Kj -- for block breaking checker

            //in game wool check
            if (data instanceof Wool) {
                Location loc = block.getLocation();

                for (Points point : ctp.mainArena.getCapturePoints()) { // Kj -- s -> point
                    Location pointLocation = new Location(player.getWorld(), point.getX(), point.getY(), point.getZ());
                    double distance = pointLocation.distance(loc);
                    if (distance < 5.0D) {
                        // Check if player team can capture point
                        if(point.getNotAllowedToCaptureTeams() != null && Util.containsTeam(point.getNotAllowedToCaptureTeams(), ctp.playerData.get(player.getName()).getTeam().getColor())) {
                            ctp.sendMessage(player, ChatColor.RED + "Your team can't capture this point.");
                            event.setCancelled(true);
                            if(ctp.getGlobalConfigOptions().debugMessages)
                            	ctp.getLogger().info("Just cancelled a BlockBreakEvent because the player tried to break a block that the playing player's team couldn't capture.");
                            return;
                        }

                        if(isInsidePoint(point, loc) || (point.getPointDirection() != null && isInsidePointVert(point, loc))) {
                            inPoint = true; // Kj -- for block placement checker
                        }

                        if (point.getPointDirection() == null) {
                            if (checkForFillHor(point, loc, ctp.playerData.get(player.getName()).getTeam().getColor(), ((Wool) data).getColor().toString(), true)) {
                                if (ctp.playerData.get(player.getName()).getTeam().getColor().equalsIgnoreCase(((Wool) data).getColor().toString())) {
                                	if(!ctp.mainArena.getConfigOptions().allowBreakingOwnCapturedPointWool) {
                                        event.setCancelled(true);
                                        ctp.sendMessage(player, ChatColor.RED + "Why would you want to uncapture your own point?!");
                                        if(ctp.getGlobalConfigOptions().debugMessages)
                                        	ctp.logInfo("Just cancelled a BlockBreakEvent because a player tried to break their own wool in a horizontal point they captured.");
                                        return;
                                	}
                                }
                                
                                if (point.getControlledByTeam() != null) {
                                    point.setControlledByTeam(null);
                                    Util.sendMessageToPlayers(ctp, subtractPoints(((Wool) data).getColor().toString(), point.getName()));
                                    break;
                                }
                            }
                        } else {
                            if (checkForFillVert(point, loc, ctp.playerData.get(player.getName()).getTeam().getColor(), ((Wool) data).getColor().toString(), true))  {
                                if (ctp.playerData.get(player.getName()).getTeam().getColor().equalsIgnoreCase(((Wool) data).getColor().toString())) {
                                	if(!ctp.mainArena.getConfigOptions().allowBreakingOwnCapturedPointWool) {
                                        event.setCancelled(true);
                                        ctp.sendMessage(player, ChatColor.RED + "Why would you want to uncapture your own point?!");
                                        if(ctp.getGlobalConfigOptions().debugMessages)
                                        	ctp.logInfo("Just cancelled a BlockBreakEvent because a player tried to break their own wool in a vertical point they captured.");
                                        return;
                                	}
                                }
                                
                                if (point.getControlledByTeam() != null) {
                                	point.setControlledByTeam(null);
                                    Util.sendMessageToPlayers(ctp, subtractPoints(((Wool) data).getColor().toString(), point.getName()));
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            // Kj -- block breaking checker blocks breaking of anything not in the CTPPoint if the config has set AllowBlockBreak to false.
            if (!ctp.mainArena.getConfigOptions().allowBlockBreak && !inPoint) {
                event.setCancelled(true);
                if(ctp.getGlobalConfigOptions().debugMessages)
                	ctp.getLogger().info("Just cancelled a BlockBreakEvent because you have allowBlockBreak set to false.");
                return;
            }
            
            if(!ctp.getGlobalConfigOptions().enableHardArenaRestore) {
                ctp.arenaRestore.addBlock(block, false);
            }


            /* Kj -- this checks to see if the event was cancelled. If it wasn't, then it's a legit block break.
             * If the config option is set to no items on block break, then cancel the event and set the block
             * to air instead. That way, it does not drop items. */
            if (!ctp.mainArena.getConfigOptions().breakingBlocksDropsItems) {
                if (!event.isCancelled()) {
                    event.setCancelled(true);
                    block.setType(Material.AIR);
                }
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace (BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        
        // If it tries to place in lobby
        if (ctp.playerData.containsKey(player.getName()) && ctp.playerData.get(player.getName()).inLobby()) {
            event.setCancelled(true);
            if(ctp.getGlobalConfigOptions().debugMessages)
            	ctp.getLogger().info("Just cancelled a BlockPlaceEvent because the player tried to place a block in the lobby.");
            return;
        }

        if (!ctp.playerData.containsKey(player.getName())) {// If tries to place blocks in arena out of game
            for(ArenaBoundaries bound : ctp.arenasBoundaries.values()) {
                if (ctp.playerListener.isInside(block.getLocation().getBlockX(), bound.getx1(), bound.getx2()) && ctp.playerListener.isInside(block.getLocation().getBlockY(), bound.gety1(), bound.gety2()) && ctp.playerListener.isInside(block.getLocation().getBlockZ(), bound.getz1(), bound.getz2()) && block.getLocation().getWorld().getName().equalsIgnoreCase(bound.getWorld())) {
                    if (Permissions.canAccess(player, false, new String[]{"ctp.*", "ctp.admin", "ctp.admin.canModify"})) {
                        return; // Player can edit arena
                    }

                    player.sendMessage(ChatColor.RED + "You do not have permission to do that.");
                    event.setCancelled(true);
                    if(ctp.getGlobalConfigOptions().debugMessages)
                    	ctp.getLogger().info("Just cancelled a BlockPlaceEvent because the player tried to place a block that was inside arena but the player wasn't playing.");
                    return;
                }
            }
            return;
        }

        if (!ctp.isGameRunning()) {
            return;
        }
        
        BlockState state = block.getState();
        MaterialData data = state.getData();
        if (isAlreadyInGame(player.getName())) {
            boolean inPoint = false; // Kj -- for block placement checker
            if ((data instanceof Wool)) {
                Location loc = block.getLocation();

                for (Points point : ctp.mainArena.getCapturePoints()) {
                    Location pointLocation = new Location(player.getWorld(), point.getX(), point.getY(), point.getZ());
                    double distance = pointLocation.distance(loc);
                    if (distance < 5)  {// Found nearest point ( points can't be closer than 5 blocks)
                        // Check if player team can capture point
                        if(point.getNotAllowedToCaptureTeams() != null && Util.containsTeam(point.getNotAllowedToCaptureTeams(), ctp.playerData.get(player.getName()).getTeam().getColor())) {
                            ctp.sendMessage(player, ChatColor.RED + " Your team can't capture this point.");
                            event.setCancelled(true);
                            if(ctp.getGlobalConfigOptions().debugMessages)
                            	ctp.getLogger().info("Just cancelled a BlockPlaceEvent because the player's team couldn't capture this point.");
                            return;
                        }

                        // If building near the point with not your own colored wool(to prevent wool destroy bug)
                        if (!ctp.playerData.get(player.getName()).getTeam().getColor().equalsIgnoreCase(((Wool) data).getColor().toString())) {
                            event.setCancelled(true);
                            return;
                        }

                        if(isInsidePoint(point, loc) || (point.getPointDirection() != null && isInsidePointVert(point, loc))) {
                            inPoint = true; // Kj -- for block placement checker
                        }
                        
                        if (point.getPointDirection() == null) {
                            //Check if wool is placed on top of point
                            if (checkForWoolOnTopHorizontal(loc, point)) {
                                event.setCancelled(true);
                                if(ctp.getGlobalConfigOptions().debugMessages)
                                	ctp.getLogger().info("Just cancelled a BlockPlaceEvent because the player tried to place a block on top of a point.");
                                return;
                            }

                            if (checkForFillHor(point, loc, ctp.playerData.get(player.getName()).getTeam().getColor(), ((Wool) data).getColor().toString(), false)) {
                                if (point.getControlledByTeam() == null) {
                                    point.setControlledByTeam(ctp.playerData.get(player.getName()).getTeam().getColor());
                                    Util.sendMessageToPlayers(ctp, addPoints(((Wool) data).getColor().toString(), point.getName()));
                                    ctp.playerData.get(player.getName()).setPointsCaptured(
											ctp.playerData.get(player.getName()).getPointsCaptured() + 1);
                                    ctp.playerData.get(player.getName()).setMoney(ctp.playerData.get(player.getName()).getMoney() + ctp.mainArena.getConfigOptions().moneyForPointCapture);
                                    player.sendMessage("Money: " + ChatColor.GREEN + ctp.playerData.get(player.getName()).getMoney());
                                    if (didSomeoneWin()) {
                                        loc.getBlock().setTypeId(0);
                                    }
                                    break;
                                }
                            }
                        } 
                        else {
                            //Check if wool is placed on top of point
                            if (checkForWoolOnTopVertical(loc, point)) {
                                event.setCancelled(true);
                                if(ctp.getGlobalConfigOptions().debugMessages)
                                	ctp.getLogger().info("Just cancelled a BlockPlaceEvent because the player tried to place a block on top of a point.");
                                return;
                            }

                            if (checkForFillVert(point, loc, ctp.playerData.get(player.getName()).getTeam().getColor(), ((Wool) data).getColor().toString(), false)) {
                                if (point.getControlledByTeam() == null) {
                                    point.setControlledByTeam(ctp.playerData.get(player.getName()).getTeam().getColor());
                                    Util.sendMessageToPlayers(ctp, addPoints(((Wool) data).getColor().toString(), point.getName()));
                                    ctp.playerData.get(player.getName()).setPointsCaptured(
											ctp.playerData.get(player.getName()).getPointsCaptured() + 1);
                                    ctp.playerData.get(player.getName()).setMoney(ctp.playerData.get(player.getName()).getMoney() + ctp.mainArena.getConfigOptions().moneyForPointCapture);
                                    player.sendMessage("Money: " + ChatColor.GREEN + ctp.playerData.get(player.getName()).getMoney());
                                    if (didSomeoneWin()) {
                                        loc.getBlock().setTypeId(0);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            
            // Kj -- block placement checker blocks placement of anything not in the CTPPoint if the config has set AllowBlockPlacement to false.
            if (!ctp.mainArena.getConfigOptions().allowBlockPlacement && !inPoint) {
                event.setCancelled(true);
                if(ctp.getGlobalConfigOptions().debugMessages)
                	ctp.getLogger().info("Just cancelled a BlockBreakEvent because you have allowBlockPlacement set to false.");
                return;
            }
            
            if(!ctp.getGlobalConfigOptions().enableHardArenaRestore) {
                ctp.arenaRestore.addBlock(block, false);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChange (SignChangeEvent event) {
        if (!ctp.isGameRunning()) {
            return;
        }
        if (isAlreadyInGame(event.getPlayer().getName())) {
            event.getPlayer().sendMessage(ChatColor.RED + "Cannot break sign whilst playing.");
            event.setCancelled(true);
            if(ctp.getGlobalConfigOptions().debugMessages)
            	ctp.getLogger().info("Just cancelled a SignChangeEvent because the player was playing a game.");
            return;
        }
    }

    public String addPoints (String aTeam, String gainedpoint) { // Kj -- remade.
        if (this.capturegame) {
            for (Team team : ctp.mainArena.getTeams()) {
                if (team.getColor().equalsIgnoreCase(aTeam)) {
                    team.addOneControlledPoints();
                    if (!ctp.mainArena.getConfigOptions().useScoreGeneration) {
                        return team.getChatColor() + aTeam.toUpperCase() + ChatColor.WHITE + " captured " + ChatColor.GOLD + gainedpoint + ChatColor.WHITE + ". (" + team.getControlledPoints() + "/" + ctp.mainArena.getConfigOptions().pointsToWin + " points).";
                    } else {
                        return team.getChatColor() + aTeam.toUpperCase() + ChatColor.WHITE + " captured " + ChatColor.GOLD + gainedpoint + ChatColor.WHITE + ". (" + team.getControlledPoints() + "/" + ctp.mainArena.getCapturePoints().size() + " points).";
                    }
                }
            }
            return null;
        }
        return null;
    }

    @SuppressWarnings("deprecation")
	public boolean assignRole(Player p, String role) {
        // role changing cooldown
        if(ctp.playerData.get(p.getName()).getClassChangeTime() == 0) {
            ctp.playerData.get(p.getName()).setClassChangeTime(System.currentTimeMillis());
        } else if((System.currentTimeMillis() - ctp.playerData.get(p.getName()).getClassChangeTime() <= 1000)) { // 1 sec 
            ctp.sendMessage(p, ChatColor.RED + "You can change roles only every 1 sec!");
            return false;
        } else {
            ctp.playerData.get(p.getName()).setClassChangeTime(System.currentTimeMillis());
        }

        p.setHealth(20);
        PlayerInventory inv = p.getInventory();
        inv.clear();
        inv.setHelmet(null);
        if(ctp.playerData.get(p.getName()).getTeam() != null) {
            DyeColor color1 = DyeColor.valueOf(ctp.playerData.get(p.getName()).getTeam().getColor().toUpperCase());

            ItemStack helmet = new ItemStack(Material.WOOL, 1, color1.getData());
            p.getInventory().setHelmet(helmet);
        }

        inv.setChestplate(null);
        inv.setLeggings(null);
        inv.setBoots(null);
        
		//It's deprecated but it's currently the only way to get the desired effect.
		p.updateInventory();

        ctp.playerData.get(p.getName()).setRole(role);

        for (Items item : ctp.roles.get(role.toLowerCase())) {
            if (Util.ARMORS_TYPE.contains(item.getItem()) && (!Util.HELMETS_TYPE.contains(item.getItem()))) {
                ItemStack i = new ItemStack(item.getItem(), 1);
                
                // Add enchantments
                for(int j = 0; j < item.getEnchantments().size(); j++) {
                    i.addEnchantment(item.getEnchantments().get(j), item.getEnchantmentLevels().get(j));
                }
                
                Util.equipArmorPiece(i, inv);
            } else {
                ItemStack stack;
                // If something is wrong in config file
                try {
                    // If exp or economy money - do not allow to pass(only for rewards)
                    if(item.getItem().equals(Material.AIR))
                        continue;

                    stack = new ItemStack(item.getItem());
                    stack.setAmount(item.getAmount());
                    if(item.getType() != -1)
                        stack.setDurability(item.getType());

                    // Add enchantments
                    for(int j = 0; j < item.getEnchantments().size(); j++) {
                        stack.addEnchantment(item.getEnchantments().get(j), item.getEnchantmentLevels().get(j));
                    }
                } catch(Exception e) {
                    ctp.logInfo("There is error in your config file, with roles. Please check them!");
                    return false;
                }
                inv.addItem(stack);
            }
        }
        
		//It's deprecated but it's currently the only way to get the desired effect.
		p.updateInventory();

        return true;
    }

    private boolean checkForColor (String color, Location loc1, Location loc2, Location loc3) {
        DyeColor color1 = itsColor(loc1.getBlock());
        DyeColor color2 = itsColor(loc2.getBlock());
        DyeColor color3 = itsColor(loc3.getBlock());
        if (color1 == null || color2 == null || color3 == null) {
            return false;
        }
        return color1.toString().equalsIgnoreCase(color) && color2.toString().equalsIgnoreCase(color) && color3.toString().equalsIgnoreCase(color);
    }

    private boolean checkForFillHor(Points point, Location loc, String color, String placedWoolColor, boolean onBlockBreak) {
        //If player is placing not his own wool
        if ((!onBlockBreak) && (!placedWoolColor.equalsIgnoreCase(color))) {
            return false;
        }
        if (isInsidePoint(point, loc)) {
            Location loc1 = new Location(loc.getWorld(), point.getX(), point.getY(), point.getZ());
            Location loc2 = new Location(loc.getWorld(), point.getX() + 1, point.getY(), point.getZ());
            Location loc3 = new Location(loc.getWorld(), point.getX() + 1, point.getY(), point.getZ() + 1);
            Location loc4 = new Location(loc.getWorld(), point.getX(), point.getY(), point.getZ() + 1);
            if (loc.equals(loc1)) {
                return checkForColor(placedWoolColor, loc2, loc3, loc4);
            } else if (loc.equals(loc2)) {
                return checkForColor(placedWoolColor, loc1, loc3, loc4);
            } else if (loc.equals(loc3)) {
                return checkForColor(placedWoolColor, loc1, loc2, loc4);
            } else if (loc.equals(loc4)) {
                return checkForColor(placedWoolColor, loc1, loc2, loc3);
            }
        }
        return false;
    }

    private boolean checkForFillVert (Points point, Location loc, String color, String placedWoolColor, boolean onBlockBreak) {
        //If player is placing not his own wool
        if ((!onBlockBreak) && (!placedWoolColor.equalsIgnoreCase(color))) {
            return false;
        }

        if (isInsidePointVert(point, loc)) {
            Location loc1 = new Location(loc.getWorld(), 0, 0, 0);
            Location loc2 = new Location(loc.getWorld(), 0, 0, 0);
            Location loc3 = new Location(loc.getWorld(), 0, 0, 0);
            Location loc4 = new Location(loc.getWorld(), 0, 0, 0);

            if (point.getPointDirection().equals("NORTH") || point.getPointDirection().equals("SOUTH")) {
                loc1 = new Location(loc.getWorld(), point.getX(), point.getY(), point.getZ());
                loc2 = new Location(loc.getWorld(), point.getX(), point.getY() + 1, point.getZ());
                loc3 = new Location(loc.getWorld(), point.getX(), point.getY(), point.getZ() + 1);
                loc4 = new Location(loc.getWorld(), point.getX(), point.getY() + 1, point.getZ() + 1);
            } else if (point.getPointDirection().equals("WEST") || point.getPointDirection().equals("EAST")) {
                loc1 = new Location(loc.getWorld(), point.getX(), point.getY(), point.getZ());
                loc2 = new Location(loc.getWorld(), point.getX(), point.getY() + 1, point.getZ());
                loc3 = new Location(loc.getWorld(), point.getX() + 1, point.getY(), point.getZ());
                loc4 = new Location(loc.getWorld(), point.getX() + 1, point.getY() + 1, point.getZ());
            }

            // This way because wool block is not placed yet
            if (loc.equals(loc1)) {
                return checkForColor(placedWoolColor, loc2, loc3, loc4);
            } else if (loc.equals(loc2)) {
                return checkForColor(placedWoolColor, loc1, loc3, loc4);
            } else if (loc.equals(loc3)) {
                return checkForColor(placedWoolColor, loc1, loc2, loc4);
            } else if (loc.equals(loc4)) {
                return checkForColor(placedWoolColor, loc1, loc2, loc3);
            }
        }
        return false;
    }

    private boolean checkForWoolOnTopHorizontal (Location loc, Points s) {
        for (int x = (int) s.getX() + 2; x >= s.getX() - 1; x--) {
            for (int y = (int) s.getY() + 1; y <= s.getY() + 2; y++) {
                for (int z = (int) s.getZ() - 1; z <= s.getZ() + 2; z++) {
                    if ((loc.getBlockX() == x) && (loc.getBlockY() == y) && (loc.getBlockZ() == z)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkForWoolOnTopVertical (Location loc, Points point) {
        if (point.getPointDirection().equals("NORTH")) {
            if (loc.getX() >= point.getX() - 2 && loc.getX() < point.getX()) {
                if (loc.getY() >= point.getY() - 1 && loc.getY() < point.getY() + 3) {
                    if (loc.getZ() >= point.getZ() - 1 && loc.getZ() < point.getZ() + 3) {
                        return true;
                    }
                }
            }
        } else if (point.getPointDirection().equals("EAST")) {
            if (loc.getX() >= point.getX() - 1 && loc.getX() < point.getX() + 3) {
                if (loc.getY() >= point.getY() - 1 && loc.getY() < point.getY() + 3) {
                    if (loc.getZ() >= point.getZ() - 2 && loc.getZ() < point.getZ()) {
                        return true;
                    }
                }
            }
        } else if (point.getPointDirection().equals("SOUTH")) {
            if (loc.getX() >= point.getX() + 1 && loc.getX() < point.getX() + 3) {
                if (loc.getY() >= point.getY() - 1 && loc.getY() < point.getY() + 3) {
                    if (loc.getZ() >= point.getZ() - 1 && loc.getZ() < point.getZ() + 3) {
                        return true;
                    }
                }
            }
        } else if (point.getPointDirection().equals("WEST")) {
            if (loc.getX() >= point.getX() - 1 && loc.getX() < point.getX() + 3) {
                if (loc.getY() >= point.getY() - 1 && loc.getY() < point.getY() + 3) {
                    if (loc.getZ() >= point.getZ() + 1 && loc.getZ() < point.getZ() + 3) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean didSomeoneWin() {
        List<Team> winningteams = new ArrayList<Team>();
        String WinMessage = "";
        if (ctp.mainArena.getConfigOptions().useScoreGeneration) {
            for (Team team : ctp.mainArena.getTeams()) {
                if (team.getScore() >= ctp.mainArena.getConfigOptions().scoreToWin) {
                    winningteams.add(team);
                    WinMessage = team.getChatColor() + team.getColor().toUpperCase() + ChatColor.WHITE + " wins!";
                }
            }
        } else {
            for (Team team : ctp.mainArena.getTeams()) {
                if (team.getControlledPoints() >= ctp.mainArena.getConfigOptions().pointsToWin) {
                    winningteams.add(team);
                    WinMessage = team.getChatColor() + team.getColor().toUpperCase() + ChatColor.WHITE + " wins!";
                }
            }
        }

        if (winningteams.isEmpty()) {
            return false;
        } else if (winningteams.size() > 1) {
            if (ctp.mainArena.getConfigOptions().useScoreGeneration) {
                WinMessage = "It's a tie! " + winningteams.size() + " teams have passed " + ctp.mainArena.getConfigOptions().pointsToWin + " points!";
            } else {
                WinMessage = "It's a tie! " + winningteams.size() + " teams have a score of " + ctp.mainArena.getConfigOptions().scoreToWin + "!";
            }
        }

        for (Team team : winningteams) {
            for (String player : ctp.playerData.keySet()) {
                if ((ctp.playerData.get(player).inArena()) && (ctp.playerData.get(player).getTeam() == team)) {
                    ctp.playerData.get(player).setWinner(true);
                }
            }
        }

        Util.sendMessageToPlayers(ctp, WinMessage);
        String message = "";
        if (ctp.mainArena.getConfigOptions().useScoreGeneration) {
            for (Team aTeam : ctp.mainArena.getTeams()) {
                message = message + aTeam.getChatColor() + aTeam.getColor().toUpperCase() + ChatColor.WHITE + " final score: " + aTeam.getScore() + ChatColor.AQUA + " // ";
            }
        } else {
            for (Team aTeam : ctp.mainArena.getTeams()) {
                message = message + aTeam.getChatColor() + aTeam.getColor().toUpperCase() + ChatColor.WHITE + " final points: " + aTeam.getControlledPoints() + ChatColor.AQUA + " // ";
            }
        }

        Util.sendMessageToPlayers(ctp, message);
        endGame(false);

        return true;
    }

    public void endGame (boolean noRewards) {
        Util.sendMessageToPlayers(ctp, "A Capture The Points game has ended!");

        // Task canceling
        if (ctp.CTP_Scheduler.playTimer != 0) {
            ctp.getServer().getScheduler().cancelTask(ctp.CTP_Scheduler.playTimer);
            ctp.CTP_Scheduler.playTimer = 0;
        }
        if (ctp.CTP_Scheduler.money_Score != 0) {
            ctp.getServer().getScheduler().cancelTask(ctp.CTP_Scheduler.money_Score);
            ctp.CTP_Scheduler.money_Score = 0;
        }
        if (ctp.CTP_Scheduler.pointMessenger != 0) {
            ctp.getServer().getScheduler().cancelTask(ctp.CTP_Scheduler.pointMessenger);
            ctp.CTP_Scheduler.pointMessenger = 0;
        }
        if (ctp.CTP_Scheduler.helmChecker != 0) {
            ctp.getServer().getScheduler().cancelTask(ctp.CTP_Scheduler.helmChecker);
            ctp.CTP_Scheduler.helmChecker = 0;
        }
        if (ctp.CTP_Scheduler.healingItemsCooldowns != 0) {
            ctp.getServer().getScheduler().cancelTask(ctp.CTP_Scheduler.healingItemsCooldowns);
            ctp.CTP_Scheduler.healingItemsCooldowns = 0;
        }

        for (Points s : ctp.mainArena.getCapturePoints()) {
            s.setControlledByTeam(null);
        }

        this.preGame = true;
        this.capturegame = false;

        for (String player : this.ctp.playerData.keySet()) {
        	Player p = ctp.getServer().getPlayer(player);
            restoreThings(p);
            if (!noRewards) {
                Util.rewardPlayer(ctp, p);
            }
        }
        
        //Arena restore
        if(ctp.getGlobalConfigOptions().enableHardArenaRestore) {
            ctp.arenaRestore.restoreMySQLBlocks();
        } else {
            ctp.arenaRestore.restoreAllBlocks();
        }

        for (HealingItems item : ctp.healingItems) {
            if (!item.cooldowns.isEmpty()) {
                item.cooldowns.clear();
            }
        }
        this.ctp.mainArena.getLobby().clearLobbyPlayerData();
        this.ctp.previousLocation.clear();
        this.ctp.playerData.clear();
        for (int i = 0; i < ctp.mainArena.getTeams().size(); i++) {
            ctp.mainArena.getTeams().get(i).setMemberCount(0);
        }
    }

    public boolean isAlreadyInGame(String player) {
        return ctp.playerData.get(player) != null;
    }

    private boolean isInsidePoint (Points point, Location loc) {
        if (loc.getBlockX() == point.getX() || loc.getBlockX() == point.getX() + 1) {
            if (loc.getBlockY() == point.getY()) {
                if (loc.getBlockZ() == point.getZ() || loc.getBlockZ() == point.getZ() + 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInsidePointVert (Points point, Location loc) {
        if (point.getPointDirection().equals("NORTH")) {
            if (loc.getBlockX() == point.getX()) {
                if ((loc.getBlockY() == point.getY()) || (loc.getBlockY() == point.getY() + 1)) {
                    if (loc.getBlockZ() == point.getZ() || loc.getBlockZ() == point.getZ() + 1) {
                        return true;
                    }
                }
            }
        } else if (point.getPointDirection().equals("EAST")) {
            if ((loc.getBlockX() == point.getX()) || (loc.getBlockX() == point.getX() + 1)) {
                if ((loc.getBlockY() == point.getY()) || (loc.getBlockY() == point.getY() + 1)) {
                    if (loc.getBlockZ() == point.getZ()) {
                        return true;
                    }
                }
            }
        } else if (point.getPointDirection().equals("SOUTH")) {
            if (loc.getBlockX() == point.getX()) {
                if (loc.getBlockY() == point.getY() || loc.getBlockY() == point.getY() + 1) {
                    if (loc.getBlockZ() == point.getZ() || loc.getBlockZ() == point.getZ() + 1) {
                        return true;
                    }
                }
            }
        } else if (point.getPointDirection().equals("WEST")) {
            if (loc.getBlockX() == point.getX() || loc.getBlockX() == point.getX() + 1) {
                if (loc.getBlockY() == point.getY() || loc.getBlockY() == point.getY() + 1) {
                    if (loc.getBlockZ() == point.getZ()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public DyeColor itsColor (Block b) {
        BlockState state = b.getState();
        MaterialData data = state.getData();
        if ((data instanceof Wool)) {
            Wool wool = (Wool) data;
            return wool.getColor();
        }
        return null;
    }

    public void restoreThings(Player p) {
        ctp.playerData.get(p.getName()).setJustJoined(true);
        InvManagement.restoreInv(p);

        Location loc = ctp.previousLocation.get(p.getName());
        //loc.getWorld().loadChunk(loc.getBlockX(), loc.getBlockZ());
        loc.setYaw((float) ctp.mainArena.getLobby().getDir());
        if(!loc.getWorld().isChunkLoaded(loc.getChunk())) {
        	loc.getWorld().loadChunk(loc.getChunk());
            //Packet packet = new Packet51MapChunk((int)loc.getX() - 5, (int)loc.getY() - 2, (int)loc.getZ() - 5, (int)loc.getX() + 5, (int)loc.getY() + 2, (int)loc.getZ() + 5, ((CraftWorld)loc.getWorld()).getHandle().worldProvider.a);
            //((CraftPlayer)p).getHandle().netServerHandler.sendPacket(packet);
        }
        p.teleport(this.ctp.previousLocation.get(p.getName()));

        // do not check double signal
        if (ctp.playerData.get(p.getName()) == null) {
            return;
        }
        
        PotionManagement.removeAllEffects(p);
        PotionManagement.restorePotionEffects(p, ctp.playerData.get(p.getName()).getPotionEffects());

        p.setFoodLevel(ctp.playerData.get(p.getName()).getFoodLevel());
        if (ctp.playerData.get(p.getName()).wasInCreative()) {
            p.setGameMode(GameMode.CREATIVE);
        }

        if (ctp.playerData.get(p.getName()).getHealth() > 200 || ctp.playerData.get(p.getName()).getHealth() < 0) {
            p.setHealth(20);
        } else {
            p.setHealth(ctp.playerData.get(p.getName()).getHealth());
        }
       
    }

    public String subtractPoints (String aTeam, String lostpoint) { // Kj -- remade.
        if (this.capturegame) {
            for (Team team : ctp.mainArena.getTeams()) {
                if (team.getColor().equalsIgnoreCase(aTeam)) {
                    team.substractOneControlledPoints();
                    if (team.getControlledPoints() < 0) {
                        team.setControlledPoints(0);
                    }
                    return team.getChatColor() + aTeam.toUpperCase() + ChatColor.WHITE + " lost " + ChatColor.GOLD + lostpoint + ".";
                }
            }
            return null;
        }
        return null;
    }

}