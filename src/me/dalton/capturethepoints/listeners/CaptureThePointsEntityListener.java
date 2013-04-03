package me.dalton.capturethepoints.listeners;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.HealingItems;
import me.dalton.capturethepoints.Util;
import me.dalton.capturethepoints.beans.Arena;
import me.dalton.capturethepoints.beans.Items;
import me.dalton.capturethepoints.beans.Spawn;
import me.dalton.capturethepoints.events.CTPPlayerDeathEvent;
import me.dalton.capturethepoints.util.PotionManagement;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Wool;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CaptureThePointsEntityListener  implements Listener {

    private final CaptureThePoints ctp;

    public CaptureThePointsEntityListener(CaptureThePoints plugin) {
        this.ctp = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        for(Arena a : ctp.getArenaMaster().getArenas()) {
            if (!a.isGameRunning())
                return;
            
            if(ctp.getGlobalConfigOptions().enableHardArenaRestore)
                return;
            
            if(!a.getConfigOptions().allowExplosionBlockBreak) {
            	if(ctp.getGlobalConfigOptions().debugMessages)
            		ctp.getLogger().info("Cleared an explosion's block list because allowExplosionBlockBreak is set to false in the arena.");
            	event.blockList().clear();
            	return;
            }
            
            if (ctp.getArenaUtil().isInside(event.getLocation().getBlockX(), a.getX1(), a.getX2())
            		&& ctp.getArenaUtil().isInside(event.getLocation().getBlockY(), a.getY1(), a.getY2())
            		&& ctp.getArenaUtil().isInside(event.getLocation().getBlockZ(), a.getZ1(), a.getZ2())
            		&& event.getLocation().getWorld().getName().equalsIgnoreCase(a.getWorld().getName())) {
            	if(!a.getConfigOptions().allowExplosionBlockBreak) {
                	if(ctp.getGlobalConfigOptions().debugMessages)
                		ctp.getLogger().info("Cleared an explosion's block list because allowExplosionBlockBreak is set to false in the arena.");
                	event.blockList().clear();
                	return;
                }else {
                    List<Block> explodedBlocks = event.blockList();

                    for (Block block : explodedBlocks)
                        ctp.getArenaRestore().addBlock(block, true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        
        if(!ctp.getArenaMaster().isPlayerInAnArena(((Player) event.getEntity())))
            return;
        
        if(!ctp.getArenaMaster().getArenaPlayerIsIn((Player) event.getEntity()).isGameRunning() && ctp.getArenaMaster().getPlayerData(((Player) event.getEntity()).getName()).inLobby())  {
            event.setDroppedExp(0);
            event.getDrops().clear();
            return;
        }

        event.setDroppedExp(0);
        event.getDrops().clear();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void healthRegain(EntityRegainHealthEvent event) {
	if (!(event.getEntity() instanceof Player)) return;
	
	Player player = (Player) event.getEntity();
	if(!ctp.getArenaMaster().isPlayerInAnArena(player))
		return; //not in an arena
	
	if (ctp.getArenaMaster().getArenaPlayerIsIn(player).isGameRunning()) {
		if(!ctp.getArenaMaster().getArenaPlayerIsIn(player).getConfigOptions().regainHealth) {
			if (event.getRegainReason() == RegainReason.SATIATED) {
				event.setCancelled(true);
				if(ctp.getGlobalConfigOptions().debugMessages)
					ctp.getLogger().info("Just cancelled a EntityRegainHealthEvent as you have it turned off during the game.");
			}else return;
		}else return;
	}else return;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPotionEffect(PotionSplashEvent event) {
        // lobby damage check
    	if(!(event.getEntity().getShooter() instanceof Player)) return; //the shooter is not a player, so don't handle
    	if(!ctp.getArenaMaster().isPlayerInAnArena((Player) event.getEntity().getShooter())) return; //the shooter is not in the arena, so don't handle
    	
    	Player thrower = (Player) event.getEntity().getShooter();
    	
        if (ctp.getArenaMaster().getArenaPlayerIsIn(thrower).isGameRunning()) {
            ThrownPotion potion = event.getEntity();
            PotionEffect effect = null;
            boolean harmful = false;
            for(PotionEffect e: potion.getEffects()){
            	effect = e;
            }
            
            harmful = PotionManagement.isHarmful(effect);
            for(Iterator<LivingEntity> iter = event.getAffectedEntities().iterator(); iter.hasNext();){
            	LivingEntity hitPlayerEntity = iter.next();
            	Player hitPlayer = (Player)hitPlayerEntity;
            	
            	//Is potion negative/positive
            	if(harmful){	                   //Negative
            		//If hit self
            		if(thrower.equals(hitPlayer)){
            			event.setIntensity(hitPlayerEntity, 0); 
            		}
            		//Is thrower on the same team as player hit
            		if (ctp.getArenaMaster().getPlayerData(thrower).getTeam().getColor().equalsIgnoreCase(ctp.getArenaMaster().getPlayerData(hitPlayer).getTeam().getColor())){ // Yes
            			event.setIntensity(hitPlayerEntity, 0); 
            		}else{ // No
                        if (isProtected(ctp.getArenaMaster().getArenaPlayerIsIn(hitPlayer), hitPlayer)) {
                        	event.setIntensity(hitPlayerEntity, 0);                		                	
                        }	
            		}
                    //Player has "died"
                    if(effect.getType().equals(PotionEffectType.HARM)){
                    	int damage = 6;
                    	
                    	if(effect.getAmplifier()==1){
                    		damage = 12;
                    	}
                    	
                    	double intensity = event.getIntensity(hitPlayerEntity);
                    	
                    	double tmpDamage = ((double)damage)*intensity;
                    	damage = (int) tmpDamage;
                    	
                    	tmpDamage = tmpDamage - ((int)tmpDamage);
                    	
                    	if(tmpDamage >= .5){
                    		damage++;
                    	}
                    	
                        if (ctp.getArenaMaster().getPlayerData(hitPlayer) != null && hitPlayer.getHealth() - damage <= 0) {
                        	event.setIntensity(hitPlayerEntity, 0); 
                            respawnPlayer(ctp.getArenaMaster().getArenaPlayerIsIn(hitPlayer), hitPlayer, thrower);
                        }
                    }
            	}else{                            //Positive
            		if (!ctp.getArenaMaster().getPlayerData(thrower).getTeam().getColor().equalsIgnoreCase(ctp.getArenaMaster().getPlayerData(hitPlayer).getTeam().getColor())){ 
            			event.setIntensity(hitPlayerEntity, 0); 
            		}
            	}
            }
        }
    }


	@EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            // Kj -- Didn't involve a player. So we don't care.
            return;
        }
        
        Player attacker = null;
        if (ctp.getArenaMaster().getPlayerData(((Player) event.getEntity()).getName()) != null) {

            // for melee
            if (checkForPlayerEvent(event)) {
                attacker = ((Player) ((EntityDamageByEntityEvent) event).getDamager());
            }

            // for arrows
            if ((event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) && (((Projectile) ((EntityDamageByEntityEvent) event).getDamager()).getShooter() instanceof Player)) {
                attacker = (Player) ((Projectile) ((EntityDamageByEntityEvent) event).getDamager()).getShooter();
            }

            Player playa = (Player) event.getEntity();

            // lobby damage check
            if (ctp.getArenaMaster().getPlayerData(playa).inLobby() || (attacker != null && ctp.getArenaMaster().getPlayerData(attacker) != null && ctp.getArenaMaster().getPlayerData(attacker).inLobby())) {
                event.setCancelled(true);
                if(ctp.getGlobalConfigOptions().debugMessages)
                	ctp.getLogger().info("Just cancelled a EntityDamageEvent because the player is in the lobby.");
                return;
            }

            if (isProtected(ctp.getArenaMaster().getArenaPlayerIsIn(playa), playa)) {
                // If you damage yourself
                if (attacker != null) {
                	ctp.sendMessage(attacker, ChatColor.LIGHT_PURPLE + "You can't damage enemy in their spawn!");
                }
                
                event.setCancelled(true);
                if(ctp.getGlobalConfigOptions().debugMessages)
                	ctp.getLogger().info("Just cancelled a EntityDamageEvent because the player is in his/her spawn area.");
                return;
            }

            //disable pvp damage
            if (attacker != null) {
                if ((ctp.getArenaMaster().getPlayerData(playa) != null) && (ctp.getArenaMaster().getPlayerData(attacker) != null)) {
                    if (ctp.getArenaMaster().getPlayerData(playa).getTeam().getColor().equalsIgnoreCase(ctp.getArenaMaster().getPlayerData(attacker).getTeam().getColor())) {
                    	ctp.sendMessage(attacker, ctp.getArenaMaster().getPlayerData(playa).getTeam().getChatColor() + playa.getName() + ChatColor.LIGHT_PURPLE + " is on your team!");
                        event.setCancelled(true);
                        if(ctp.getGlobalConfigOptions().debugMessages)
                        	ctp.getLogger().info("Just cancelled a EntityDamageEvent because the player is on the same team as the attacker.");
                        return;
                    } else {
                    	// This is if there exists something like factions group protection
                        if (event.isCancelled()) {
                            event.setCancelled(false);
                            if(ctp.getGlobalConfigOptions().debugMessages)
                            	ctp.getLogger().info("Just uncancelled a EntityDamageEvent because the event was cancelled by some other plugin.");
                        }
                    }
                }
            }

            //Player has "died"
            if ((ctp.getArenaMaster().getPlayerData(playa) != null) && (playa.getHealth() - event.getDamage() <= 0)) {
                event.setCancelled(true);
                if(ctp.getGlobalConfigOptions().debugMessages)
                	ctp.getLogger().info("Just cancelled a EntityDamageEvent because the player 'died' therefore we are respawning it.");
                respawnPlayer(ctp.getArenaMaster().getArenaPlayerIsIn(playa), playa, attacker);
                
                //Throw a custom event for when the player dies in the arena
                CTPPlayerDeathEvent CTPevent = new CTPPlayerDeathEvent(playa, ctp.getArenaMaster().getArenaPlayerIsIn(playa), ctp.getArenaMaster().getPlayerData(playa));
                ctp.getPluginManager().callEvent(CTPevent);
            }
        }
        
        if (ctp.getArenaMaster().getPlayerData(((Player) event.getEntity())) != null && ctp.getArenaMaster().getPlayerData(((Player) event.getEntity()).getName()).inLobby()) {
            event.setCancelled(true);
            if(ctp.getGlobalConfigOptions().debugMessages)
            	ctp.getLogger().info("Just cancelled a EntityDamageEvent because the player is in the lobby.");
        }
    }
    
    private boolean checkForPlayerEvent(EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent)) {
            return false;
        }
        // You now know the player getting damaged was damaged by another entity
        if (!(((EntityDamageByEntityEvent) event).getDamager() instanceof Player)) {
            return false;
        }
        // You now know the entity that is attacking is a player
        return true;
    }
    
	@SuppressWarnings("deprecation")
	private boolean dropWool(Arena arena, Player player) {
        if (!arena.getConfigOptions().dropWoolOnDeath) {
            return false;
        }

        PlayerInventory inv = player.getInventory();
        int ownedWool = 0;
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getTypeId() == 35) {
                if (!((Wool) item.getData()).getColor().toString().equalsIgnoreCase(ctp.getArenaMaster().getPlayerData(player.getName()).getTeam().getColor())) {
                    inv.remove(35);
                    ItemStack tmp = new ItemStack(item.getType(), item.getAmount(), (short) ((Wool) item.getData()).getColor().getData());
                    player.getWorld().dropItem(player.getLocation(), tmp);
                } else {
                    ownedWool += item.getAmount();
                }
            }
        }
        inv.remove(Material.WOOL);
        
        if (ownedWool != 0) {
            DyeColor color = DyeColor.valueOf(ctp.getArenaMaster().getPlayerData(player.getName()).getTeam().getColor().toUpperCase());
            ItemStack wool = new ItemStack(35, ownedWool, color.getData());
            player.getInventory().addItem(new ItemStack[]{wool});
            
    		//It's deprecated but it's currently the only way to get the desired effect.
    		player.updateInventory();
        }
        return true;
    }
    
	@SuppressWarnings("deprecation")
	private void giveRoleItemsAfterDeath(Player player) {
    	
        PlayerInventory inv = player.getInventory();
        
        //Get wool for return
        int ownedWool = 0;
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getTypeId() == 35) {
                if (!((Wool) item.getData()).getColor().toString().equalsIgnoreCase(ctp.getArenaMaster().getPlayerData(player.getName()).getTeam().getColor())) {
                    inv.remove(35);
                    ItemStack tmp = new ItemStack(item.getType(), item.getAmount(), (short) ((Wool) item.getData()).getColor().getData());
                    player.getWorld().dropItem(player.getLocation(), tmp);
                } else {
                    ownedWool += item.getAmount();
                }
            }
        }
        
        inv.clear(); // Removes inventory
        
        for (Items item : ctp.getRoles().get(ctp.getArenaMaster().getPlayerData(player.getName()).getRole())) {
            if(item.getItem().equals(Material.AIR))
                continue;

            if (inv.contains(item.getItem())) {
                if(item.getItem().getId() == 373) {   // Potions
                    ItemStack stack = new ItemStack(item.getItem());
                    stack.setAmount(item.getAmount());
                    stack.setDurability(item.getType());

                    HashMap<Integer, ? extends ItemStack> slots = inv.all(item.getItem());
                    int amount = 0;
                    for (int slotNum : slots.keySet()) {
                        if(slots.get(slotNum).getDurability() == item.getType())
                            amount += slots.get(slotNum).getAmount();
                    }

                    if (amount < item.getAmount()) {
                        //Removing old potions
                        for (int slotNum : slots.keySet()) {
                            if(slots.get(slotNum).getDurability() == item.getType())
                                inv.setItem(slotNum, null);
                        }

                        inv.addItem(stack);
                    }
                }
                else if (!Util.ARMORS_TYPE.contains(item.getItem())/* && (!Util.WEAPONS_TYPE.contains(item.getType()))*/) {
                    HashMap<Integer, ? extends ItemStack> slots = inv.all(item.getItem());
                    int amount = 0;
                    for (int slotNum : slots.keySet()) {
                        amount += slots.get(slotNum).getAmount();
                    }
                    
                    if (amount < item.getAmount()) {
                        inv.remove(item.getItem());

                        ItemStack stack = new ItemStack(item.getItem());
                        stack.setAmount(item.getAmount());
                        if(item.getType() != -1)
                            stack.setDurability(item.getType());
                        // Add enchantments
                        for(int j = 0; j < item.getEnchantments().size(); j++) {
                            stack.addEnchantment(item.getEnchantments().get(j), item.getEnchantmentLevels().get(j));
                        }
                        
                        inv.addItem(stack);
                    }
                }
            } else {
                if (!Util.ARMORS_TYPE.contains(item.getItem())) {
                    ItemStack stack = new ItemStack(item.getItem());
                    stack.setAmount(item.getAmount());
                    if(item.getType() != -1)
                        stack.setDurability(item.getType());
                    // Add enchantments
                    for(int j = 0; j < item.getEnchantments().size(); j++) {
                        stack.addEnchantment(item.getEnchantments().get(j), item.getEnchantmentLevels().get(j));
                    }
                    
                    inv.addItem(stack);
                } else {// find if there is something equipped
                    ItemStack stack = new ItemStack(item.getItem(), item.getAmount());

                    // Add enchantments
                    for(int j = 0; j < item.getEnchantments().size(); j++)
                        stack.addEnchantment(item.getEnchantments().get(j), item.getEnchantmentLevels().get(j));
                    
                    //If the armour slot is the role's thing, reset it to reset the durability but if it isn't (besides being null) then we just add this to their inventory.
                    //This way players keep their extra armor that they could have bought from the in game store.
                    if (Util.BOOTS_TYPE.contains(item.getItem())) {
                    	if(inv.getBoots() == null)
                    		inv.setBoots(stack);
                    	else if (inv.getBoots().getType() == item.getItem())
                            inv.setBoots(stack);
                        else
                            inv.addItem(stack);
                    } else if (Util.LEGGINGS_TYPE.contains(item.getItem())) {
                    	if (inv.getLeggings() == null)
                    		inv.setLeggings(stack);
                    	else if (inv.getLeggings().getType() == item.getItem())
                            inv.setLeggings(stack);
                        else
                            inv.addItem(stack);
                    } else if (Util.CHESTPLATES_TYPE.contains(item.getItem())) {
                    	if (inv.getChestplate() == null)
                    		inv.setChestplate(stack);
                    	else if (inv.getChestplate().getType() == item.getItem())
                            inv.setChestplate(stack);
                        else
                            inv.addItem(stack);
                    }
                }
            }
        }
        
        //Re-add Wool
        if (ownedWool != 0) {
            DyeColor color = DyeColor.valueOf(ctp.getArenaMaster().getPlayerData(player.getName()).getTeam().getColor().toUpperCase());
            ItemStack wool = new ItemStack(35, ownedWool, color.getData());
            player.getInventory().addItem(new ItemStack[]{wool});
        }
        
		//It's deprecated but it's currently the only way to get the desired effect.
		player.updateInventory();
    }
    
	private boolean isProtected(Arena arena, Player player) {
        // Kj -- null checks
        if (arena == null || player == null) {
            return false;
        }
        if (arena.getPlayerData(player) == null)
            return false;

        Spawn spawn = new Spawn();

        try {
            spawn = arena.getPlayerData(player).getTeam().getSpawn();
        } catch(Exception e) { // For debugging
            System.out.println("[ERROR][CTP] Team spawn could not be found!  Player Name: " + player.getName());
            return false;
        }
                            
        Location protectionPoint = new Location(arena.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ());
        double distance = ctp.getUtil().getDistance(player.getLocation(), protectionPoint); // Kj -- this method is world-friendly.
        
        if (distance == Double.NaN) {
            return false; // Kj -- it will return Double.NaN if cross-world or couldn't work out distance for whatever reason.
        } else {
            return distance <= arena.getConfigOptions().protectionDistance;
        }
    }
    
    private void respawnPlayer(Arena arena, Player player, Player attacker) {
        if (attacker != null) {
            if(!ctp.getGlobalConfigOptions().disableKillMessages) {
                ctp.getUtil().sendMessageToPlayers(arena, arena.getPlayerData(player).getTeam().getChatColor() + player.getName() + ChatColor.WHITE
                        + " was killed by " + arena.getPlayerData(attacker).getTeam().getChatColor() + attacker.getName());
            }
            
            dropWool(arena, player);
            arena.getPlayerData(attacker).setMoney(arena.getPlayerData(attacker).getMoney() + arena.getConfigOptions().moneyForKill);
            attacker.sendMessage("Money: " + ChatColor.GREEN + arena.getPlayerData(attacker).getMoney());
            ctp.checkForKillMSG(arena, attacker, false);
            ctp.checkForKillMSG(arena, player, true);
        } else {
            if(!ctp.getGlobalConfigOptions().disableKillMessages)
                ctp.getUtil().sendMessageToPlayers(arena, arena.getPlayerData(player).getTeam().getChatColor() + player.getName() + ChatColor.WHITE
                        + " was killed by " + ChatColor.LIGHT_PURPLE + "Herobrine");
            ctp.checkForKillMSG(arena, player, true);
        }

        PotionManagement.removeAllEffects(player);
        ctp.getArenaUtil().setFullHealthPlayerAndCallEvent(arena, player);
        player.setFoodLevel(20);
        Spawn spawn = arena.getPlayerData(player).getTeam().getSpawn();

        if (arena.getConfigOptions().giveNewRoleItemsOnRespawn) {
            giveRoleItemsAfterDeath(player);
        }

        // Reseting player cooldowns
        for (HealingItems item : ctp.getHealingItems())
            if (item != null && item.cooldowns != null && item.cooldowns.size() > 0 && item.resetCooldownOnDeath)
                for (String playName : item.cooldowns.keySet())
                    if (playName.equalsIgnoreCase(player.getName()))
                        item.cooldowns.remove(playName);

        Location loc = new Location(arena.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ());
        loc.setYaw((float) spawn.getDir());
        arena.getWorld().loadChunk(loc.getBlockX(), loc.getBlockZ());
        boolean teleport = player.teleport(loc);
        
        if (!teleport) {
            player.teleport(new Location(player.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ(), 0.0F, (float)spawn.getDir()));
        }
    }
}