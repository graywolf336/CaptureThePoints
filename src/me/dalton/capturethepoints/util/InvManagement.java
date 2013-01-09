package me.dalton.capturethepoints.util;

import me.dalton.capturethepoints.CaptureThePoints;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class InvManagement {
	private static CaptureThePoints ctp;
	
	public static void setCTP(CaptureThePoints instance) {
		ctp = instance;
	}
	
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
	 * @param player The player to save the inventory for.
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
}
