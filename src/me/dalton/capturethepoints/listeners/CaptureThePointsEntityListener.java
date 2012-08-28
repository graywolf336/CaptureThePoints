package me.dalton.capturethepoints.listeners;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import me.dalton.capturethepoints.CTPPotionEffect;
import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.HealingItems;
import me.dalton.capturethepoints.Items;
import me.dalton.capturethepoints.Spawn;
import me.dalton.capturethepoints.Util;

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


    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!ctp.isGameRunning())
            return;
        if(ctp.globalConfigOptions.enableHardArenaRestore)
            return;

        if (ctp.playerListener.isInside(event.getLocation().getBlockX(), ctp.mainArena.x1, ctp.mainArena.x2) && ctp.playerListener.isInside(event.getLocation().getBlockY(), ctp.mainArena.y1, ctp.mainArena.y2) && ctp.playerListener.isInside(event.getLocation().getBlockZ(), ctp.mainArena.z1, ctp.mainArena.z2) && event.getLocation().getWorld().getName().equalsIgnoreCase(ctp.mainArena.world)) {
            List<Block> explodedBlocks = event.blockList();

            for (Block block : explodedBlocks)
                ctp.arenaRestore.addBlock(block, true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        
        if((this.ctp.playerData.get((Player) event.getEntity()) == null))
            return;
        
        if(!ctp.isGameRunning() && this.ctp.playerData.get((Player) event.getEntity()).isInLobby)  {
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
    	
    	if(ctp.mainArena.co.regainHealth) return;
    	
    	 if (ctp.isGameRunning()) {
             if ((this.ctp.playerData.get((Player) event.getEntity()) != null)) {
            	 if (event.getRegainReason() == RegainReason.SATIATED) {
             		event.setCancelled(true);
             	}else return;
             }else return;
    	 }else return;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPotionEffect(PotionSplashEvent event) {
        // lobby damage check


        if (ctp.isGameRunning()) {
            Player thrower = (Player) event.getEntity().getShooter();
            
            if ((this.ctp.playerData.get(thrower) != null)) {
                ThrownPotion potion = event.getEntity();
                PotionEffect effect = null;
                boolean harmful = false;
                for(PotionEffect e: potion.getEffects()){
                	effect = e;
                }
                
                harmful = isHarmful(effect);
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
                		if (this.ctp.playerData.get(thrower).team.color.equalsIgnoreCase(this.ctp.playerData.get(hitPlayer).team.color)){ // Yes
                			event.setIntensity(hitPlayerEntity, 0); 
                		}else{ // No
                            if (isProtected(hitPlayer)) {
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
                        	
                            if ((this.ctp.playerData.get(hitPlayer) != null) && (hitPlayer.getHealth() - damage <= 0)) {
                            	event.setIntensity(hitPlayerEntity, 0); 
                                respawnPlayer(hitPlayer, thrower);
                            }
                        }
                	}else{                            //Positive
                		if (!this.ctp.playerData.get(thrower).team.color.equalsIgnoreCase(this.ctp.playerData.get(hitPlayer).team.color)){ 
                			event.setIntensity(hitPlayerEntity, 0); 
                		}
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

        //Only check if game is running
        if (ctp.isGameRunning()) {
            Player attacker = null;
            if ((this.ctp.playerData.get((Player) event.getEntity()) != null)) {

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
                if (this.ctp.playerData.get(playa).isInLobby || (attacker != null && this.ctp.playerData.get(attacker) != null && this.ctp.playerData.get(attacker).isInLobby)) {
                    event.setCancelled(true);
                    return;
                }

                if (isProtected(playa)) {
                    // If you damage yourself
                    if (attacker != null) {
                    	ctp.sendMessage(attacker, ChatColor.LIGHT_PURPLE + "You can't damage enemy in their spawn!");
                    }
                    event.setCancelled(true);
                    return;
                }

                //disable pvp damage
                if (attacker != null) {
                    if ((this.ctp.playerData.get(playa) != null) && (this.ctp.playerData.get(attacker) != null)) {
                        if (this.ctp.playerData.get(playa).team.color.equalsIgnoreCase(this.ctp.playerData.get(attacker).team.color)) {
                        	ctp.sendMessage(attacker, ctp.playerData.get(playa).team.chatcolor + playa.getName() + ChatColor.LIGHT_PURPLE + " is on your team!");
                            event.setCancelled(true);
                            return;
                        } else {
                        	// This is if there exists something like factions group protection
                            if (event.isCancelled()) {
                                event.setCancelled(false);
                            }
                        }
                    }
                }

                //Player has "died"
                if ((this.ctp.playerData.get(playa) != null) && (playa.getHealth() - event.getDamage() <= 0)) {
                    event.setCancelled(true);
                    respawnPlayer(playa, attacker);
                }
            }
        }
        if (ctp.playerData.get((Player) event.getEntity()) != null && ctp.playerData.get((Player) event.getEntity()).isInLobby) {
            event.setCancelled(true);
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
	private boolean dropWool(Player player) {
        if (!ctp.mainArena.co.dropWoolOnDeath) {
            return false;
        }

        PlayerInventory inv = player.getInventory();
        int ownedWool = 0;
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getTypeId() == 35) {
                if (!((Wool) item.getData()).getColor().toString().equalsIgnoreCase(ctp.playerData.get(player).team.color)) {
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
            DyeColor color = DyeColor.valueOf(ctp.playerData.get(player).team.color.toUpperCase());
            ItemStack wool = new ItemStack(35, ownedWool, color.getData());
            player.getInventory().addItem(new ItemStack[]{wool});
            
    		//It's deprecated but it's currently the only way to get the desired effect.
    		player.updateInventory();
        }
        return true;
    }
    
	@SuppressWarnings("deprecation")
	public void giveRoleItemsAfterDeath(Player player) {
    	
        PlayerInventory inv = player.getInventory();
        
        //Get wool for return
        int ownedWool = 0;
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getTypeId() == 35) {
                if (!((Wool) item.getData()).getColor().toString().equalsIgnoreCase(ctp.playerData.get(player).team.color)) {
                    inv.remove(35);
                    ItemStack tmp = new ItemStack(item.getType(), item.getAmount(), (short) ((Wool) item.getData()).getColor().getData());
                    player.getWorld().dropItem(player.getLocation(), tmp);
                } else {
                    ownedWool += item.getAmount();
                }
            }
        }
        inv.clear(); // Removes inventory
        
        for (Items item : ctp.roles.get(ctp.playerData.get(player).role)) {
            if(item.item.equals(Material.AIR))
                continue;

            if (inv.contains(item.item)) {
                if(item.item.getId() == 373) {   // Potions
                    ItemStack stack = new ItemStack(item.item);
                    stack.setAmount(item.amount);
                    stack.setDurability(item.type);

                    HashMap<Integer, ? extends ItemStack> slots = inv.all(item.item);
                    int amount = 0;
                    for (int slotNum : slots.keySet()) {
                        if(slots.get(slotNum).getDurability() == item.type)
                            amount += slots.get(slotNum).getAmount();
                    }

                    if (amount < item.amount) {
                        //Removing old potions
                        for (int slotNum : slots.keySet()) {
                            if(slots.get(slotNum).getDurability() == item.type)
                                inv.setItem(slotNum, null);
                        }

                        inv.addItem(stack);
                    }
                }
                else if (!Util.ARMORS_TYPE.contains(item.item)/* && (!Util.WEAPONS_TYPE.contains(item.getType()))*/) {
                    HashMap<Integer, ? extends ItemStack> slots = inv.all(item.item);
                    int amount = 0;
                    for (int slotNum : slots.keySet()) {
                        amount += slots.get(slotNum).getAmount();
                    }
                    
                    if (amount < item.amount) {
                        inv.remove(item.item);

                        ItemStack stack = new ItemStack(item.item);
                        stack.setAmount(item.amount);
                        if(item.type != -1)
                            stack.setDurability(item.type);
                        // Add enchantments
                        for(int j = 0; j < item.enchantments.size(); j++) {
                            stack.addEnchantment(item.enchantments.get(j), item.enchLevels.get(j));
                        }
                        
                        inv.addItem(stack);
                    }
                }
            } else {
                if (!Util.ARMORS_TYPE.contains(item.item)) {
                    ItemStack stack = new ItemStack(item.item);
                    stack.setAmount(item.amount);
                    if(item.type != -1)
                        stack.setDurability(item.type);
                    // Add enchantments
                    for(int j = 0; j < item.enchantments.size(); j++) {
                        stack.addEnchantment(item.enchantments.get(j), item.enchLevels.get(j));
                    }
                    
                    inv.addItem(stack);
                } 
                else {// find if there is somethig equiped
                    ItemStack stack = new ItemStack(item.item, item.amount);

                    // Add enchantments
                    for(int j = 0; j < item.enchantments.size(); j++) {
                        stack.addEnchantment(item.enchantments.get(j), item.enchLevels.get(j));
                    }
                    

                    if (Util.BOOTS_TYPE.contains(item.item)) {
                        if (inv.getBoots().getType() == item.item) {
                            inv.setBoots(stack);
                        } else {
                            inv.addItem(stack);
                        }
                    } else if (Util.LEGGINGS_TYPE.contains(item.item)) {
                        if (inv.getLeggings().getType() == item.item) {
                            inv.setLeggings(stack);
                        } else {
                            inv.addItem(stack);
                        }
                    } else if (Util.CHESTPLATES_TYPE.contains(item.item)) {
                        if (inv.getChestplate().getType() == item.item) {
                            inv.setChestplate(stack);
                        } else {
                            inv.addItem(stack);
                        }
                    }
                }
            }
        }
        //Re-add Wool
        if (ownedWool != 0) {
            DyeColor color = DyeColor.valueOf(ctp.playerData.get(player).team.color.toUpperCase());
            ItemStack wool = new ItemStack(35, ownedWool, color.getData());
            player.getInventory().addItem(new ItemStack[]{wool});
        }
        
		//It's deprecated but it's currently the only way to get the desired effect.
		player.updateInventory();
    }
    
    public boolean isProtected(Player player) {
        // Kj -- null checks
        if (ctp.mainArena == null || player == null) {
            return false;
        }
        if (ctp.playerData.get(player) == null) {
            return false;
        }

        Spawn spawn = new Spawn();

        try {
            spawn = ctp.playerData.get(player).team.spawn;
        } catch(Exception e) { // For debugging
            System.out.println("[ERROR][CTP] Team spawn could not be found!  Player Name: " + player.getName());
            return false;
        }
                            
        Location protectionPoint = new Location(ctp.getServer().getWorld(ctp.mainArena.world), spawn.x, spawn.y, spawn.z);
        double distance = Util.getDistance(player.getLocation(), protectionPoint); // Kj -- this method is world-friendly.
        
        if (distance == Double.NaN) {
            return false; // Kj -- it will return Double.NaN if cross-world or couldn't work out distance for whatever reason.
        } else {
            return distance <= ctp.mainArena.co.protectionDistance;
        }
    }
    
    
    public void respawnPlayer (Player player, Player attacker) {
        if (attacker != null) {
            if(!ctp.globalConfigOptions.disableKillMessages) {
                Util.sendMessageToPlayers(ctp, ctp.playerData.get(player).team.chatcolor + player.getName() + ChatColor.WHITE
                        + " was killed by " + ctp.playerData.get(attacker).team.chatcolor + attacker.getName());
            }
            
            dropWool(player);
            ctp.playerData.get(attacker).money += ctp.mainArena.co.moneyForKill;
            attacker.sendMessage("Money: " + ChatColor.GREEN + ctp.playerData.get(attacker).money);
            ctp.checkForKillMSG(attacker, false);
            ctp.checkForKillMSG(player, true);
        } else {
            if(!ctp.globalConfigOptions.disableKillMessages)
                Util.sendMessageToPlayers(ctp, ctp.playerData.get(player).team.chatcolor + player.getName() + ChatColor.WHITE
                        + " was killed by " + ChatColor.LIGHT_PURPLE + "Herobrine");
            ctp.sendMessage(player, ChatColor.RED + "Please do not remove your Helmet.");
            ctp.checkForKillMSG(player, true);
        }

        CTPPotionEffect.removeAllEffectsNew(player);
        //TODO: Set this to the event
        player.setHealth(ctp.mainArena.co.maxPlayerHealth);
        player.setFoodLevel(20);
        Spawn spawn = ctp.playerData.get(player).team.spawn;

        if (ctp.mainArena.co.giveNewRoleItemsOnRespawn) {
            giveRoleItemsAfterDeath(player);
        }

        // Reseting player cooldowns
        for (HealingItems item : ctp.healingItems) {
            if (item != null && item.cooldowns != null && item.cooldowns.size() > 0 && item.resetCooldownOnDeath) {
                for (String playName : item.cooldowns.keySet()) {
                    if (playName.equalsIgnoreCase(player.getName())) {
                        item.cooldowns.remove(playName);
                    }
                }
            }
        }

        Location loc = new Location(ctp.getServer().getWorld(ctp.mainArena.world), spawn.x, spawn.y, spawn.z);
        loc.setYaw((float) spawn.dir);
        ctp.getServer().getWorld(ctp.mainArena.world).loadChunk(loc.getBlockX(), loc.getBlockZ());
        boolean teleport = player.teleport(loc);
        
        if (!teleport) {
            player.teleport(new Location(player.getWorld(), spawn.x, spawn.y, spawn.z, 0.0F, (float)spawn.dir));
        }
    }
    

    private boolean isHarmful(PotionEffect effect) {
		
    	PotionEffectType type = effect.getType();

    	if (type.equals(PotionEffectType.HARM)){
    		return true;
    	}
    	if (type.equals(PotionEffectType.HEAL)){
    		return false;
    	}
    	if (type.equals(PotionEffectType.WEAKNESS)){
    		return true;
    	}
    	if (type.equals(PotionEffectType.REGENERATION)){
    		return false;
    	}
    	if (type.equals(PotionEffectType.INCREASE_DAMAGE)){
    		return false;
    	}
    	if (type.equals(PotionEffectType.SPEED)){
    		return false;
    	}
    	if (type.equals(PotionEffectType.SLOW)){
    		return true;
    	}
    	if (type.equals(PotionEffectType.POISON)){
    		return true;
    	}
    	
		return false;
	}
}