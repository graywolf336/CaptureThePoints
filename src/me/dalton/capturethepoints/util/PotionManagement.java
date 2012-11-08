package me.dalton.capturethepoints.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

/**
 *
 * @author Humsas
 */
public class PotionManagement {    
    /**
     * The player to remove all the potions effects for.
     * 
     * @param player The player in which to remove potion effects from.
     */
    public static void removeAllEffects(Player player) {
         for (PotionEffect pef : player.getActivePotionEffects()) {
         	player.removePotionEffect(pef.getType());
         }
    }
    
    /**
     * Provides a way to store the potion effects from the player.
     * 
     * @param player Player to store the effects for.
     * @return A List of the potion effects.
     */
	public static List<PotionEffect> storePlayerPotionEffects(Player player) {
        List<PotionEffect> effects = new ArrayList<PotionEffect>();
        
        Collection<PotionEffect> potions = player.getActivePotionEffects();
        
        for (PotionEffect potion : potions) {
        	effects.add(potion);
        }
        
        return effects;
    }
	
	/**
	 * Provides a way to restore the potions that the player had.
	 * 
	 * @param player The player who we need to restore them back to.
	 * @param effects A List<CTPPotionEffect> of the potions that need to be restored.
	 */
	public static void restorePotionEffects(Player player, List<PotionEffect> effects) {
		for(PotionEffect effect : effects) {
			player.addPotionEffect(effect);
		}
	}
}
