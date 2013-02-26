package me.dalton.capturethepoints.listeners;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.beans.Arena;
import me.dalton.capturethepoints.beans.ArenaBoundaries;
import me.dalton.capturethepoints.beans.PlayerData;
import me.dalton.capturethepoints.beans.Points;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;

public class CaptureThePointsBlockListener implements Listener {
    private final CaptureThePoints ctp;

    public CaptureThePointsBlockListener (CaptureThePoints ctp) {
        this.ctp = ctp;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        if (!ctp.getArenaMaster().isPlayerInAnArena(player)) { // If tries to break arena blocks out of game
            for(ArenaBoundaries bound : ctp.getArenaMaster().getArenasBoundaries().values()){
                if (ctp.getArenaUtil().isInside(block.getLocation().getBlockX(), bound.getx1(), bound.getx2())
                		&& ctp.getArenaUtil().isInside(block.getLocation().getBlockY(), bound.gety1(), bound.gety2())
                		&& ctp.getArenaUtil().isInside(block.getLocation().getBlockZ(), bound.getz1(), bound.getz2())
                		&& block.getLocation().getWorld().getName().equalsIgnoreCase(bound.getWorld())) {
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
        
        // If it tries to break in lobby
        if (ctp.getArenaMaster().getPlayerData(player).inLobby()) {
            // breaks block beneath player(it causes teleport event if you cancel action)
            int playerLocX = player.getLocation().getBlockX();
            int playerLocY = player.getLocation().getBlockY() - 1;
            int playerLocZ = player.getLocation().getBlockZ();

            if (playerLocX == block.getX() && playerLocY == block.getY() && playerLocZ == block.getZ()) {
                // allow teleport
                ctp.getArenaMaster().getPlayerData(player.getName()).setJustJoined(true);
                ctp.playerNameForTeleport = player.getName();

                // player can not drop down so we need to reset teleport flag
                ctp.getServer().getScheduler().scheduleSyncDelayedTask(ctp, new Runnable() {
                    public void run () {
                        if (!ctp.playerNameForTeleport.isEmpty()) {
                        	ctp.getArenaMaster().getPlayerData(ctp.playerNameForTeleport).setJustJoined(false);
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

        //So far we've verified:
        //   the player is not in an arena
        //   the player is/isn't in an a arena lobby
        
        //We've verified that they are currently playing in an arena, so now we can get the arena they're in and their data
        Arena arena = ctp.getArenaMaster().getArenaPlayerIsIn(player);
        PlayerData playerdata = arena.getPlayerData(player);
        
        if (!arena.isGameRunning()) {//idk what this does
            return;
        }

        BlockState state = block.getState();
        MaterialData data = state.getData();
        // check for sign destroy
        if (state instanceof Sign) {
            event.setCancelled(true);
            if(ctp.getGlobalConfigOptions().debugMessages)
            	ctp.getLogger().info("Just cancelled a BlockBreakEvent because the player tried to break a sign while playing the game.");
            return;
        }
        
        boolean inPoint = false; // Kj -- for block breaking checker
        if (data instanceof Wool) {//in game wool check
            Location loc = block.getLocation();

            for (Points point : arena.getCapturePoints()) { // Kj -- s -> point
                Location pointLocation = new Location(player.getWorld(), point.getX(), point.getY(), point.getZ());
                double distance = pointLocation.distance(loc);
                if (distance < 5.0D) {
                    // Check if player team can capture point
                    if(point.getNotAllowedToCaptureTeams() != null && ctp.getUtil().containsTeam(point.getNotAllowedToCaptureTeams(), playerdata.getTeam().getColor())) {
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
                        if (checkForFillHor(point, loc, playerdata.getTeam().getColor(), ((Wool) data).getColor().toString(), true)) {
                            if (playerdata.getTeam().getColor().equalsIgnoreCase(((Wool) data).getColor().toString())) {
                            	if(!arena.getConfigOptions().allowBreakingOwnCapturedPointWool) {
                                    event.setCancelled(true);
                                    ctp.sendMessage(player, ChatColor.RED + "Why would you want to uncapture your own point?!");
                                    if(ctp.getGlobalConfigOptions().debugMessages)
                                    	ctp.logInfo("Just cancelled a BlockBreakEvent because a player tried to break their own wool in a horizontal point they captured.");
                                    return;
                            	}
                            }
                            
                            if (point.getControlledByTeam() != null) {
                                point.setControlledByTeam(null);
                                ctp.getUtil().sendMessageToPlayers(arena, ctp.getArenaUtil().subtractPoints(arena, ((Wool) data).getColor().toString(), point.getName()));
                                break;
                            }
                        }
                    } else {
                        if (checkForFillVert(point, loc, playerdata.getTeam().getColor(), ((Wool) data).getColor().toString(), true))  {
                            if (playerdata.getTeam().getColor().equalsIgnoreCase(((Wool) data).getColor().toString())) {
                            	if(!arena.getConfigOptions().allowBreakingOwnCapturedPointWool) {
                                    event.setCancelled(true);
                                    ctp.sendMessage(player, ChatColor.RED + "Why would you want to uncapture your own point?!");
                                    if(ctp.getGlobalConfigOptions().debugMessages)
                                    	ctp.logInfo("Just cancelled a BlockBreakEvent because a player tried to break their own wool in a vertical point they captured.");
                                    return;
                            	}
                            }
                            
                            if (point.getControlledByTeam() != null) {
                            	point.setControlledByTeam(null);
                                ctp.getUtil().sendMessageToPlayers(arena, ctp.getArenaUtil().subtractPoints(arena, ((Wool) data).getColor().toString(), point.getName()));
                                break;
                            }
                        }
                    }
                }
            }
        }

        // Kj -- block breaking checker blocks breaking of anything not in the CTPPoint if the config has set AllowBlockBreak to false.
        if (!arena.getConfigOptions().allowBlockBreak && !inPoint) {
            event.setCancelled(true);
            if(ctp.getGlobalConfigOptions().debugMessages)
            	ctp.getLogger().info("Just cancelled a BlockBreakEvent because you have allowBlockBreak set to false.");
            return;
        }
        
        if(!ctp.getGlobalConfigOptions().enableHardArenaRestore) {
            ctp.getArenaRestore().addBlock(block, false);
        }


        /* Kj -- this checks to see if the event was cancelled. If it wasn't, then it's a legit block break.
         * If the config option is set to no items on block break, then cancel the event and set the block
         * to air instead. That way, it does not drop items. */
        if (!arena.getConfigOptions().breakingBlocksDropsItems) {
            if (!event.isCancelled()) {
                event.setCancelled(true);
                block.setType(Material.AIR);
            }
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace (BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (!ctp.getArenaMaster().isPlayerInAnArena(player)) {// If tries to place blocks in arena out of game
            for(ArenaBoundaries bound : ctp.getArenaMaster().getArenasBoundaries().values()) {
                if (ctp.getArenaUtil().isInside(block.getLocation().getBlockX(), bound.getx1(), bound.getx2())
                		&& ctp.getArenaUtil().isInside(block.getLocation().getBlockY(), bound.gety1(), bound.gety2())
                		&& ctp.getArenaUtil().isInside(block.getLocation().getBlockZ(), bound.getz1(), bound.getz2())
                		&& block.getLocation().getWorld().getName().equalsIgnoreCase(bound.getWorld())) {
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
        
        // If it tries to place in lobby
        if (ctp.getArenaMaster().getPlayerData(player.getName()).inLobby()) {
            event.setCancelled(true);
            if(ctp.getGlobalConfigOptions().debugMessages)
            	ctp.getLogger().info("Just cancelled a BlockPlaceEvent because the player tried to place a block in the lobby.");
            return;
        }
        
        //So far we've verified:
        //   the player is not in an arena
        //   the player is/isn't in an a arena lobby
        
        //We've verified that they are currently playing in an arena, so now we can get the arena they're in and their data
        Arena arena = ctp.getArenaMaster().getArenaPlayerIsIn(player);
        PlayerData playerdata = arena.getPlayerData(player);

        if (!arena.isGameRunning()) {//TODO: Verify the purpose of this, I'm totally confused on as to why this is here exactly
            return;
        }
        
        BlockState state = block.getState();
        MaterialData data = state.getData();
        boolean inPoint = false; // Kj -- for block placement checker
        if ((data instanceof Wool)) {
            Location loc = block.getLocation();

            for (Points point : arena.getCapturePoints()) {
                Location pointLocation = new Location(player.getWorld(), point.getX(), point.getY(), point.getZ());
                double distance = pointLocation.distance(loc);
                if (distance < 5)  {// Found nearest point ( points can't be closer than 5 blocks)
                    // Check if player team can capture point
                    if(point.getNotAllowedToCaptureTeams() != null && ctp.getUtil().containsTeam(point.getNotAllowedToCaptureTeams(), playerdata.getTeam().getColor())) {
                        ctp.sendMessage(player, ChatColor.RED + "Your team can't capture this point.");
                        event.setCancelled(true);
                        if(ctp.getGlobalConfigOptions().debugMessages)
                        	ctp.getLogger().info("Just cancelled a BlockPlaceEvent because the player's team couldn't capture this point.");
                        return;
                    }

                    // If building near the point with not your own colored wool(to prevent wool destroy bug)
                    if (!playerdata.getTeam().getColor().equalsIgnoreCase(((Wool) data).getColor().toString())) {
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

                        if (checkForFillHor(point, loc, playerdata.getTeam().getColor(), ((Wool) data).getColor().toString(), false)) {
                            if (point.getControlledByTeam() == null) {
                                point.setControlledByTeam(playerdata.getTeam().getColor());
                                ctp.getUtil().sendMessageToPlayers(arena, ctp.getArenaUtil().addPoints(arena, ((Wool) data).getColor().toString(), point.getName()));
                                playerdata.setPointsCaptured(playerdata.getPointsCaptured() + 1);
                                playerdata.setMoney(playerdata.getMoney() + arena.getConfigOptions().moneyForPointCapture);
                                player.sendMessage("Money: " + ChatColor.GREEN + playerdata.getMoney());
                                if (ctp.getArenaUtil().didSomeoneWin(arena)) {
                                    loc.getBlock().setTypeId(0);
                                }
                                break;
                            }
                        }
                    }else {
                        //Check if wool is placed on top of point
                        if (checkForWoolOnTopVertical(loc, point)) {
                            event.setCancelled(true);
                            if(ctp.getGlobalConfigOptions().debugMessages)
                            	ctp.getLogger().info("Just cancelled a BlockPlaceEvent because the player tried to place a block on top of a point.");
                            return;
                        }

                        if (checkForFillVert(point, loc, playerdata.getTeam().getColor(), ((Wool) data).getColor().toString(), false)) {
                            if (point.getControlledByTeam() == null) {
                                point.setControlledByTeam(playerdata.getTeam().getColor());
                                ctp.getUtil().sendMessageToPlayers(arena, ctp.getArenaUtil().addPoints(arena, ((Wool) data).getColor().toString(), point.getName()));
                                playerdata.setPointsCaptured(playerdata.getPointsCaptured() + 1);
                                playerdata.setMoney(playerdata.getMoney() + arena.getConfigOptions().moneyForPointCapture);
                                player.sendMessage("Money: " + ChatColor.GREEN + playerdata.getMoney());
                                if (ctp.getArenaUtil().didSomeoneWin(arena)) {
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
        if (!arena.getConfigOptions().allowBlockPlacement && !inPoint) {
            event.setCancelled(true);
            if(ctp.getGlobalConfigOptions().debugMessages)
            	ctp.getLogger().info("Just cancelled a BlockBreakEvent because you have allowBlockPlacement set to false on the arena named + " + arena.getName() + ".");
            return;
        }
        
        if(!ctp.getGlobalConfigOptions().enableHardArenaRestore) {
            ctp.getArenaRestore().addBlock(block, false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChange(SignChangeEvent event) {
        if (!ctp.getArenaMaster().getArenaPlayerIsIn(event.getPlayer()).isGameRunning()) {
            return;
        }
        if (ctp.getArenaMaster().isPlayerInAnArena(event.getPlayer())) {
            event.getPlayer().sendMessage(ChatColor.RED + "Cannot break sign whilst playing.");
            event.setCancelled(true);
            if(ctp.getGlobalConfigOptions().debugMessages)
            	ctp.getLogger().info("Just cancelled a SignChangeEvent because the player was playing a game.");
            return;
        }
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

    private DyeColor itsColor(Block b) {
        BlockState state = b.getState();
        MaterialData data = state.getData();
        if ((data instanceof Wool)) {
            Wool wool = (Wool) data;
            return wool.getColor();
        }
        return null;
    }
}