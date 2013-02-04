package me.dalton.capturethepoints.util;

import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.HealingItems;
import me.dalton.capturethepoints.beans.Arena;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class InvManagement {
	private static CaptureThePoints ctp;
	
	public static void setCTP(CaptureThePoints instance) {
		ctp = instance;
	}
	
    public static void restoreThings(Player p) {
    	Arena a = ctp.getArenaMaster().getArenaPlayerIsIn(p.getName());
        a.getPlayerData(p.getName()).setJustJoined(true);
        
        restoreInv(p);

        Location loc = ctp.previousLocation.get(p.getName());
        loc.setYaw((float) a.getLobby().getDir());
        if(!loc.getWorld().isChunkLoaded(loc.getChunk())) {
        	loc.getWorld().loadChunk(loc.getChunk());
        }
        
        p.teleport(ctp.previousLocation.get(p.getName()));

        // do not check double signal
        if (a.getPlayerData(p.getName()) == null) {
            return;
        }
        
        PotionManagement.removeAllEffects(p);
        PotionManagement.restorePotionEffects(p, a.getPlayerData(p.getName()).getPotionEffects());

        p.setFoodLevel(a.getPlayerData(p.getName()).getFoodLevel());
        if (a.getPlayerData(p.getName()).wasInCreative()) {
            p.setGameMode(GameMode.CREATIVE);
        }

        if (a.getPlayerData(p.getName()).getHealth() > 200 || a.getPlayerData(p.getName()).getHealth() < 0) {
            p.setHealth(20);
        } else {
            p.setHealth(a.getPlayerData(p.getName()).getHealth());
        }
       
    }
	
	/**
	 * Restore the player's inventory and armor that we stored when they joined.
	 * 
	 * @param player The player to restore to
	 */
	@SuppressWarnings("deprecation")
	public static void restoreInv (Player player) {
        PlayerInventory PlayerInv = player.getInventory();

        // Just to be sure that inventory is saved
        if (ctp.getInventories().get(player.getName()) != null) {
            PlayerInv.setContents(ctp.getInventories().get(player.getName()));
            ctp.getInventories().remove(player.getName());

            PlayerInv.setBoots(ctp.getArmor().get(player.getName())[0].getTypeId() == 0 ? null
                    : ctp.getArmor().get(player.getName())[0]); // Kj -- removed redundant casts
            PlayerInv.setLeggings(ctp.getArmor().get(player.getName())[1].getTypeId() == 0
                    ? null : ctp.getArmor().get(player.getName())[1]);
            PlayerInv.setChestplate(ctp.getArmor().get(player.getName())[2].getTypeId() == 0
                    ? null : ctp.getArmor().get(player.getName())[2]);
            PlayerInv.setHelmet(ctp.getArmor().get(player.getName())[3].getTypeId() == 0
                    ? null : ctp.getArmor().get(player.getName())[3]);
            ctp.getArmor().remove(player.getName());
            
            //It's deprecated but it's currently the only way to get the desired effect.
            player.updateInventory();
        }
    }
	
	/**
	 * Save the player's inventory when they join to a hashmap.
	 * 
	 * @param player The player to save for
	 */
    public static void saveInv (Player player) {
        PlayerInventory PlayerInv = player.getInventory();
        ctp.getInventories().put(player.getName(), PlayerInv.getContents());
        PlayerInv.clear();
        
        ctp.getArmor().put(player.getName(), PlayerInv.getArmorContents());
        PlayerInv.setHelmet(null);
        PlayerInv.setChestplate(null);
        PlayerInv.setLeggings(null);
        PlayerInv.setBoots(null);
    }
    
    /**
     * Removes the given player's cool downs.
     * 
     * @param player The player's name to remove.
     * @since 1.5.0-b123
     */
    public static void removeCoolDowns(String player) {
        // Removing player cooldowns
        for (HealingItems item : ctp.healingItems) {
            if (item != null && item.cooldowns != null && item.cooldowns.size() > 0) {
                for (String playName : item.cooldowns.keySet()) {
                    if (playName.equalsIgnoreCase(player)) {
                        item.cooldowns.remove(playName);
                    }
                }
            }
        }
    }
}
