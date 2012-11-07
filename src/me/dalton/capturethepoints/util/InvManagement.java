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
        if (ctp.getInventories().get(player) != null) {
            PlayerInv.setContents(ctp.getInventories().get(player));
            ctp.getInventories().remove(player);

            PlayerInv.setBoots(ctp.getArmor().get(player)[0].getTypeId() == 0 ? null
                    : ctp.getArmor().get(player)[0]); // Kj -- removed redundant casts
            PlayerInv.setLeggings(ctp.getArmor().get(player)[1].getTypeId() == 0
                    ? null : ctp.getArmor().get(player)[1]);
            PlayerInv.setChestplate(ctp.getArmor().get(player)[2].getTypeId() == 0
                    ? null : ctp.getArmor().get(player)[2]);
            PlayerInv.setHelmet(ctp.getArmor().get(player)[3].getTypeId() == 0
                    ? null : ctp.getArmor().get(player)[3]);
            ctp.getArmor().remove(player);
            
            //It's deprecated but it's currently the only way to get the desired effect.
            player.updateInventory();
        }
    }
}
